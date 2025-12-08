package dev.lapis256.mekanism_empowered.common.tile.component

import dev.lapis256.mekanism_empowered.api.MekEmpSerializationConstants
import dev.lapis256.mekanism_empowered.api.MekEmpUpgrade
import dev.lapis256.mekanism_empowered.common.config.MekEmpGeneralConfig
import dev.lapis256.mekanism_empowered.core.common.tile.component.IAdditionalTileComponent
import dev.lapis256.mekanism_empowered.core.extension.canInput
import dev.lapis256.mekanism_empowered.core.extension.getInstalledOrDefault
import dev.lapis256.mekanism_empowered.core.extension.isUpgradeInstalled
import dev.lapis256.mekanism_empowered.core.extension.minusAssign
import dev.lapis256.mekanism_empowered.extension.inserterConfig
import mekanism.api.Action
import mekanism.api.AutomationType
import mekanism.api.chemical.Chemical
import mekanism.api.chemical.ChemicalStack
import mekanism.api.chemical.IChemicalHandler
import mekanism.api.chemical.IChemicalTank
import mekanism.api.math.FloatingLong
import mekanism.api.math.MathUtils
import mekanism.common.capabilities.Capabilities
import mekanism.common.integration.energy.EnergyCompatUtils
import mekanism.common.lib.transmitter.TransmissionType
import mekanism.common.tile.base.TileEntityMekanism
import mekanism.common.tile.component.ITileComponent
import mekanism.common.tile.component.config.ConfigInfo
import mekanism.common.tile.component.config.DataType
import mekanism.common.tile.component.config.slot.ChemicalSlotInfo
import mekanism.common.tile.component.config.slot.EnergySlotInfo
import mekanism.common.tile.component.config.slot.FluidSlotInfo
import mekanism.common.tile.component.config.slot.InventorySlotInfo
import mekanism.common.tile.prefab.TileEntityConfigurableMachine
import mekanism.common.util.CapabilityUtils
import mekanism.common.util.EnumUtils
import net.minecraft.SharedConstants
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ForgeCapabilities
import net.minecraftforge.fluids.capability.IFluidHandler
import java.util.*
import kotlin.jvm.optionals.getOrNull
import kotlin.math.pow


class TileComponentInserter(private val tile: TileEntityConfigurableMachine) : ITileComponent, IAdditionalTileComponent {
    private var tickDelay = 0

    private val blockPos get() = tile.blockPos

    private var ioCapacities = buildIOCapacities()

    init {
        tile.addComponent(this)
    }

    fun tickServer() {
        if (!tile.isUpgradeInstalled(MekEmpUpgrade.AUTO_INSERTER)) {
            return
        }

        ioCapacities = buildIOCapacities()

        for (type in EnumUtils.TRANSMISSION_TYPES) {
            val info = tile.config.getConfig(type) ?: continue

            if (type == TransmissionType.ITEM) {
                if (tickDelay == 0) {
                    insertItems(tile.direction, info)
                } else {
                    tickDelay--
                }
            } else if (type != TransmissionType.HEAT) {
                insert(tile.direction, type, info)
            }
        }
    }

    private fun insert(facing: Direction, type: TransmissionType, info: ConfigInfo) {
        val level = tile.level as? ServerLevel ?: return

        for (dataType in info.supportedDataTypes) {
            if (!dataType.canInput) {
                continue
            }
            val slotInfo = info.getSlotInfo(dataType) ?: continue

            for (side in getSidesForData(info, facing, dataType)) {
                val target = getTarget(level, side) ?: continue
                when {
                    type.isChemical && slotInfo is ChemicalSlotInfo<*, *, *> -> insertChemical(type, target, slotInfo, side.opposite)

                    type == TransmissionType.FLUID && slotInfo is FluidSlotInfo -> {
                        val capability = getCapability(target, ForgeCapabilities.FLUID_HANDLER, side.opposite) ?: continue

                        for (tank in slotInfo.tanks) {
                            val simulated = capability.drain(ioCapacities[type]?.toInt() ?: 1024, IFluidHandler.FluidAction.SIMULATE)
                            if (simulated.isEmpty) {
                                continue
                            }
                            val remaining = tank.insert(simulated, Action.EXECUTE, AutomationType.EXTERNAL)
                            simulated.amount -= remaining.amount
                            capability.fill(simulated, IFluidHandler.FluidAction.EXECUTE)
                        }
                    }

                    type == TransmissionType.ENERGY && slotInfo is EnergySlotInfo -> {
                        val capability = EnergyCompatUtils.getLazyStrictEnergyHandler(target, side).resolve().getOrNull() ?: continue

                        for (container in slotInfo.containers) {
                            val simulated = capability.extractEnergy(FloatingLong.create(ioCapacities[type] ?: 1024), Action.SIMULATE)
                            if (simulated <= FloatingLong.ZERO) {
                                continue
                            }
                            val remaining = container.insert(simulated, Action.EXECUTE, AutomationType.EXTERNAL)
                            simulated -= remaining
                            capability.insertEnergy(simulated, Action.EXECUTE)
                        }
                    }
                }
            }
        }
    }

    private fun <HANDLER> getCapability(tile: BlockEntity, capability: Capability<HANDLER>, side: Direction): HANDLER? =
        CapabilityUtils.getCapability(tile, capability, side).resolve().orElse(null)

    private fun getChemicalCapability(
        type: TransmissionType,
        tile: BlockEntity,
        side: Direction,
    ): IChemicalHandler<out Chemical<*>, out ChemicalStack<out Chemical<*>>>? {
        val cap = when (type) {
            TransmissionType.GAS -> Capabilities.GAS_HANDLER
            TransmissionType.INFUSION -> Capabilities.INFUSION_HANDLER
            TransmissionType.PIGMENT -> Capabilities.PIGMENT_HANDLER
            TransmissionType.SLURRY -> Capabilities.SLURRY_HANDLER
            else -> null
        } ?: return null

        return getCapability(tile, cap, side)
    }

    private fun <CHEMICAL : Chemical<CHEMICAL>, STACK : ChemicalStack<CHEMICAL>> insertChemical(
        type: TransmissionType,
        target: BlockEntity,
        slotInfo: ChemicalSlotInfo<*, *, *>,
        side: Direction,
    ) {
        val handler = getChemicalCapability(type, target, side) ?: return
        for (tank in slotInfo.tanks) {
            val simulated = handler.extractChemical(ioCapacities[type] ?: 1024, Action.SIMULATE)
            if (simulated.isEmpty) {
                continue
            }
            @Suppress("UNCHECKED_CAST")
            val remaining = (tank as IChemicalTank<CHEMICAL, STACK>).insert(simulated as STACK, Action.EXECUTE, AutomationType.EXTERNAL)
            simulated.amount -= remaining.amount
            handler.extractChemical(simulated.amount, Action.EXECUTE)
        }
    }

    private fun insertItems(facing: Direction, info: ConfigInfo) {
        val level = tile.level as? ServerLevel ?: return

        for (dataType in info.supportedDataTypes) {
            if (!dataType.canInput) {
                continue
            }
            val slotInfo = info.getSlotInfo(dataType) as? InventorySlotInfo ?: continue

            for (side in getSidesForData(info, facing, dataType)) {
                val capability = getCapability(getTarget(level, side) ?: continue, ForgeCapabilities.ITEM_HANDLER, side) ?: continue

                val notEmptySlots = (0..<capability.slots).filterNot { capability.getStackInSlot(it).isEmpty }.toMutableList()
                if (notEmptySlots.isEmpty()) {
                    continue
                }

                for (slot in slotInfo.slots) {
                    var extractCount = ioCapacities[TransmissionType.ITEM]?.toInt() ?: 8

                    for (i in notEmptySlots.toList()) {
                        val simulated = capability.extractItem(i, extractCount, true)
                        if (simulated.isEmpty) {
                            notEmptySlots.remove(i)
                            continue
                        }

                        val remaining = slot.insertItem(simulated, Action.EXECUTE, AutomationType.EXTERNAL)
                        simulated.count -= remaining.count
                        extractCount -= simulated.count
                        capability.extractItem(i, simulated.count, false)

                        if (extractCount <= 0) {
                            break
                        }
                    }
                }
            }
        }

        tickDelay = run {
            val max = MekEmpUpgrade.FAST_ITEM_INSERT.max.toDouble()
            val installed = tile.getInstalledOrDefault(MekEmpUpgrade.FAST_ITEM_INSERT)
            MathUtils.clampToInt((SharedConstants.TICKS_PER_SECOND + 1.0).pow((max - installed) / max) - 1)
        }
    }

    override fun read(nbtTags: CompoundTag) = Unit
    override fun write(nbtTags: CompoundTag) = Unit

    private fun getSidesForData(info: ConfigInfo, facing: Direction, dataType: DataType): MutableSet<Direction> {
        return EnumSet.noneOf(Direction::class.java).also {
            for (side in EnumUtils.SIDES) {
                val type = info.getDataType(side)
                if (type == dataType && tile.inserterConfig.isSideEnabled(side)) {
                    it.add(side.getDirection(facing))
                }
            }
        }
    }

    private fun getTarget(level: ServerLevel, side: Direction) = level.getBlockEntity(blockPos.relative(side))

    private fun buildIOCapacities() = buildMap {
        val installed = tile.getInstalledOrDefault(MekEmpUpgrade.IO_CAPACITY)
        val max = MekEmpUpgrade.IO_CAPACITY.max.toDouble()
        put(TransmissionType.ITEM, MathUtils.clampToLong(MekEmpGeneralConfig.AutoInserter.itemRate * 8.0.pow(installed / max)))
        put(TransmissionType.GAS, MathUtils.clampToLong(MekEmpGeneralConfig.AutoInserter.chemicalRate * (1 + 32 * (installed / max))))
        put(TransmissionType.INFUSION, MathUtils.clampToLong(MekEmpGeneralConfig.AutoInserter.chemicalRate * (1 + 32 * (installed / max))))
        put(TransmissionType.PIGMENT, MathUtils.clampToLong(MekEmpGeneralConfig.AutoInserter.chemicalRate * (1 + 32 * (installed / max))))
        put(TransmissionType.SLURRY, MathUtils.clampToLong(MekEmpGeneralConfig.AutoInserter.chemicalRate * (1 + 32 * (installed / max))))
        put(TransmissionType.FLUID, MathUtils.clampToLong(MekEmpGeneralConfig.AutoInserter.fluidRate * (1 + 32 * (installed / max))))
        put(TransmissionType.ENERGY, MathUtils.clampToLong(MekEmpGeneralConfig.AutoInserter.energyRate * (1 + 32 * (installed / max))))
    }

    // IAdditionalTileComponent
    override val componentKey = MekEmpSerializationConstants.COMPONENT_INSERTER

    override fun loadComponentNBT(tile: TileEntityMekanism, dataMap: CompoundTag) {
        // No data to restore
    }
}
