package dev.lapis256.mekanism_empowered.common.tile.component

import dev.lapis256.mekanism_empowered.api.MekEmpSerializationConstants
import dev.lapis256.mekanism_empowered.api.MekEmpUpgrade
import dev.lapis256.mekanism_empowered.common.config.MekEmpGeneralConfig.AutoInserter
import dev.lapis256.mekanism_empowered.common.util.parallelCount
import dev.lapis256.mekanism_empowered.core.api.tile.component.IAdditionalTileComponent
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

    private val parallelCount by lazy { tile.parallelCount }

    init {
        tile.addComponent(this)
    }

    fun tickServer() {
        if (!tile.isUpgradeInstalled(MekEmpUpgrade.AUTO_INSERTER)) {
            return
        }

        for (type in EnumUtils.TRANSMISSION_TYPES) {
            val info = tile.config.getConfig(type) ?: continue

            if (type == TransmissionType.ITEM) {
                if (tickDelay == 0) {
                    insert(tile.direction, type, info)
                    resetTickDelay()
                } else {
                    tickDelay--
                }
            } else if (type != TransmissionType.HEAT) {
                insert(tile.direction, type, info)
            }
        }
    }

    private fun resetTickDelay() {
        val max = MekEmpUpgrade.FAST_ITEM_INSERT.max.toDouble()
        val installed = tile.getInstalledOrDefault(MekEmpUpgrade.FAST_ITEM_INSERT)
        tickDelay = MathUtils.clampToInt((SharedConstants.TICKS_PER_SECOND + 1.0).pow((max - installed) / max) - 1)
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
                val targetSide = side.opposite
                when {
                    type.isChemical && slotInfo is ChemicalSlotInfo<*, *, *> -> insertChemical(type, target, slotInfo, targetSide)
                    type == TransmissionType.FLUID && slotInfo is FluidSlotInfo -> insertFluid(target, slotInfo, targetSide)
                    type == TransmissionType.ENERGY && slotInfo is EnergySlotInfo -> insertEnergy(target, slotInfo, targetSide)
                    type == TransmissionType.ITEM && slotInfo is InventorySlotInfo -> insertItem(target, slotInfo, targetSide)
                }
            }
        }
    }

    /**
     * @param type 搬入する [TransmissionType][type]
     * @param target 搬入元となる [BlockEntity]
     * @param slotInfo 搬入先の [ChemicalSlotInfo]
     * @param targetSide 搬入元の面方向
     */
    private fun <CHEMICAL : Chemical<CHEMICAL>, STACK : ChemicalStack<CHEMICAL>> insertChemical(
        type: TransmissionType,
        target: BlockEntity,
        slotInfo: ChemicalSlotInfo<*, *, *>,
        targetSide: Direction,
    ) {
        val handler = getChemicalCapability(type, target, targetSide) ?: return
        for (tank in slotInfo.tanks) {
            val simulated = handler.extractChemical(getIOCapacity(type), Action.SIMULATE)
            if (simulated.isEmpty) {
                continue
            }
            @Suppress("UNCHECKED_CAST")
            val remaining = (tank as IChemicalTank<CHEMICAL, STACK>).insert(simulated as STACK, Action.EXECUTE, AutomationType.EXTERNAL)
            simulated.amount -= remaining.amount
            handler.extractChemical(simulated.amount, Action.EXECUTE)
        }
    }

    /**
     * @param target 搬入元となる [BlockEntity]
     * @param slotInfo 搬入先の [FluidSlotInfo]
     * @param targetSide 搬入元の面方向
     */
    private fun insertFluid(target: BlockEntity, slotInfo: FluidSlotInfo, targetSide: Direction) {
        val fromHandler = getCapability(target, ForgeCapabilities.FLUID_HANDLER, targetSide) ?: return

        for (toTank in slotInfo.tanks) {
            val simulated = fromHandler.drain(getIOCapacity(TransmissionType.FLUID).toInt(), IFluidHandler.FluidAction.SIMULATE)
            if (simulated.isEmpty) {
                continue
            }
            val remaining = toTank.insert(simulated, Action.EXECUTE, AutomationType.EXTERNAL)
            simulated.amount -= remaining.amount
            fromHandler.drain(simulated, IFluidHandler.FluidAction.EXECUTE)
        }
    }

    /**
     * @param target 搬入元となる [BlockEntity]
     * @param slotInfo 搬入先の [EnergySlotInfo]
     * @param targetSide 搬入元の面方向
     */
    private fun insertEnergy(target: BlockEntity, slotInfo: EnergySlotInfo, targetSide: Direction) {
        val fromHandler = EnergyCompatUtils.getLazyStrictEnergyHandler(target, targetSide).resolve().getOrNull() ?: return

        for (toContainer in slotInfo.containers) {
            val simulated = fromHandler.extractEnergy(FloatingLong.create(getIOCapacity(TransmissionType.ENERGY)), Action.SIMULATE)
            if (simulated <= FloatingLong.ZERO) {
                continue
            }

            val remaining = toContainer.insert(simulated, Action.EXECUTE, AutomationType.EXTERNAL)
            simulated -= remaining
            fromHandler.extractEnergy(simulated, Action.EXECUTE)
        }
    }

    /**
     * @param target 搬入元となる [BlockEntity]
     * @param slotInfo 搬入先の [InventorySlotInfo]
     * @param targetSide 搬入元の面方向
     */
    private fun insertItem(target: BlockEntity, slotInfo: InventorySlotInfo, targetSide: Direction) {
        val fromHandler = getCapability(target, ForgeCapabilities.ITEM_HANDLER, targetSide) ?: return

        val notEmptySlots = (0..<fromHandler.slots).filterNot { fromHandler.getStackInSlot(it).isEmpty }.toMutableList()
        if (notEmptySlots.isEmpty()) {
            return
        }

        for (toSlot in slotInfo.slots) {
            var extractCount = getIOCapacity(TransmissionType.ITEM).toInt()

            for (i in notEmptySlots.toList()) {
                val simulated = fromHandler.extractItem(i, extractCount, true)
                if (simulated.isEmpty) {
                    notEmptySlots.remove(i)
                    continue
                }

                val remaining = toSlot.insertItem(simulated, Action.EXECUTE, AutomationType.EXTERNAL)
                simulated.count -= remaining.count
                extractCount -= simulated.count
                fromHandler.extractItem(i, simulated.count, false)

                if (extractCount <= 0) {
                    break
                }
            }
        }
    }

    private fun getTarget(level: ServerLevel, side: Direction) = level.getBlockEntity(blockPos.relative(side))

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

    private fun getIOCapacity(type: TransmissionType): Long {
        val capacityRatio = tile.getInstalledOrDefault(MekEmpUpgrade.IO_CAPACITY) / MekEmpUpgrade.IO_CAPACITY.max.toDouble()
        val rateMultiplier = 1 + 32 * capacityRatio * parallelCount
        return MathUtils.clampToLong(
            when {
                type == TransmissionType.ITEM -> AutoInserter.itemRate * 8.0.pow(capacityRatio) * parallelCount
                type == TransmissionType.FLUID -> AutoInserter.fluidRate * rateMultiplier
                type == TransmissionType.ENERGY -> AutoInserter.energyRate * rateMultiplier
                type.isChemical -> AutoInserter.chemicalRate * rateMultiplier
                else -> error("Unsupported transmission type: $type")
            }
        )
    }

    override fun read(nbtTags: CompoundTag) = Unit
    override fun write(nbtTags: CompoundTag) = Unit

    // IAdditionalTileComponent
    override val componentKey = MekEmpSerializationConstants.COMPONENT_INSERTER

    override fun loadComponentNBT(tile: TileEntityMekanism, dataMap: CompoundTag) {
        // No data to restore
    }
}
