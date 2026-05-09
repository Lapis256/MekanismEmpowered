package dev.lapis256.mekanism_empowered.common.tile.component

import dev.lapis256.mekanism_empowered.api.MekEmpSerializationConstants
import dev.lapis256.mekanism_empowered.api.MekEmpUpgrade
import dev.lapis256.mekanism_empowered.common.config.MekEmpGeneralConfig.AutoInsert
import dev.lapis256.mekanism_empowered.core.common.util.WrappedCapabilityCache
import dev.lapis256.mekanism_empowered.core.extension.fractionUpgrades
import dev.lapis256.mekanism_empowered.core.extension.getInstalledOrDefault
import dev.lapis256.mekanism_empowered.core.extension.isUpgradeInstalled
import dev.lapis256.mekanism_empowered.extension.inserterConfig
import dev.lapis256.mekanism_empowered.extension.isAutoInsertTarget
import mekanism.api.Action
import mekanism.api.AutomationType
import mekanism.api.chemical.IChemicalHandler
import mekanism.api.energy.IStrictEnergyHandler
import mekanism.api.math.MathUtils
import mekanism.common.lib.transmitter.TransmissionType
import mekanism.common.tile.component.ITileComponent
import mekanism.common.tile.component.config.ConfigInfo
import mekanism.common.tile.component.config.DataType
import mekanism.common.tile.component.config.slot.ChemicalSlotInfo
import mekanism.common.tile.component.config.slot.EnergySlotInfo
import mekanism.common.tile.component.config.slot.FluidSlotInfo
import mekanism.common.tile.component.config.slot.InventorySlotInfo
import mekanism.common.tile.prefab.TileEntityConfigurableMachine
import mekanism.common.util.EnumUtils
import net.minecraft.SharedConstants
import net.minecraft.core.Direction
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerLevel
import net.neoforged.neoforge.fluids.capability.IFluidHandler
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction
import net.neoforged.neoforge.items.IItemHandler
import java.util.*
import kotlin.math.pow


class TileComponentInserter(private val tile: TileEntityConfigurableMachine) : ITileComponent {
    private val wrappedCapabilityCaches = EnumMap<TransmissionType, EnumMap<Direction, WrappedCapabilityCache<*>>>(TransmissionType::class.java)

    /**
     * @param side 搬入設定の面方向
     */
    private inline fun <reified CAPABILITY : Any> getCapability(type: TransmissionType, side: Direction): CAPABILITY? {
        val typeCaches = wrappedCapabilityCaches.computeIfAbsent(type) { EnumMap(Direction::class.java) }
        val cache = typeCaches.computeIfAbsent(side) {
            val level = tile.level as ServerLevel
            val pos = tile.blockPos.relative(side)
            WrappedCapabilityCache.createFromTransmissionType(type, level, pos, side.opposite)
        }

        return cache.capability as? CAPABILITY
    }

    init {
        tile.addComponent(this)
    }

    private var tickDelay = 0

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
        for (dataType in info.supportedDataTypes) {
            if (!dataType.isAutoInsertTarget) {
                continue
            }
            val slotInfo = info.getSlotInfo(dataType) ?: continue

            for (side in getSidesForData(info, facing, dataType)) {
                when (slotInfo) {
                    is ChemicalSlotInfo -> insertChemical(slotInfo, getCapability(type, side) ?: continue)
                    is FluidSlotInfo -> insertFluid(slotInfo, getCapability(type, side) ?: continue)
                    is EnergySlotInfo -> insertEnergy(slotInfo, getCapability(type, side) ?: continue)
                    is InventorySlotInfo -> insertItem(slotInfo, getCapability(type, side) ?: continue)
                }
            }
        }
    }

    /**
     * @param slotInfo 搬入先の [ChemicalSlotInfo]
     * @param fromHandler 搬入元の [IChemicalHandler]
     */
    private fun insertChemical(slotInfo: ChemicalSlotInfo, fromHandler: IChemicalHandler) {
        for (tank in slotInfo.tanks) {
            val simulated = fromHandler.extractChemical(getIOCapacity(TransmissionType.CHEMICAL), Action.SIMULATE)
            if (simulated.isEmpty) {
                continue
            }
            val remaining = tank.insert(simulated, Action.EXECUTE, AutomationType.EXTERNAL)
            val insertedAmount = simulated.amount - remaining.amount
            fromHandler.extractChemical(insertedAmount, Action.EXECUTE)
        }
    }

    /**
     * @param slotInfo 搬入先の [FluidSlotInfo]
     * @param fromHandler 搬入元の [IFluidHandler]
     */
    private fun insertFluid(slotInfo: FluidSlotInfo, fromHandler: IFluidHandler) {
        for (toTank in slotInfo.tanks) {
            val simulated = fromHandler.drain(getIOCapacity(TransmissionType.FLUID).toInt(), FluidAction.SIMULATE)
            if (simulated.isEmpty) {
                continue
            }
            val remaining = toTank.insert(simulated, Action.EXECUTE, AutomationType.EXTERNAL)
            val insertedAmount = simulated.amount - remaining.amount
            fromHandler.drain(insertedAmount, FluidAction.EXECUTE)
        }
    }

    /**
     * @param slotInfo 搬入先の [EnergySlotInfo]
     * @param fromHandler 搬入元の [IStrictEnergyHandler]
     */
    private fun insertEnergy(slotInfo: EnergySlotInfo, fromHandler: IStrictEnergyHandler) {
        for (toContainer in slotInfo.containers) {
            val simulated = fromHandler.extractEnergy(getIOCapacity(TransmissionType.ENERGY), Action.SIMULATE)
            if (simulated <= 0) {
                continue
            }

            val remaining = toContainer.insert(simulated, Action.EXECUTE, AutomationType.EXTERNAL)
            val inserted = simulated - remaining
            fromHandler.extractEnergy(inserted, Action.EXECUTE)
        }
    }


    /**
     * @param slotInfo 搬入先の [InventorySlotInfo]
     * @param fromHandler 搬入元の面方向
     */
    private fun insertItem(slotInfo: InventorySlotInfo, fromHandler: IItemHandler) {
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
                val insertedCount = simulated.count - remaining.count
                extractCount -= insertedCount
                fromHandler.extractItem(i, insertedCount, false)

                if (extractCount <= 0) {
                    break
                }
            }
        }
    }

    private fun getSidesForData(info: ConfigInfo, facing: Direction, dataType: DataType): MutableSet<Direction> {
        return EnumSet.noneOf(Direction::class.java).also {
            for ((key, value) in info.sideConfig) {
                if (value == dataType && tile.inserterConfig.isSideEnabled(key)) {
                    it.add(key.getDirection(facing))
                }
            }
        }
    }

    private fun getIOCapacity(type: TransmissionType): Long {
        val capacityRatio = tile.fractionUpgrades(MekEmpUpgrade.IO_CAPACITY)
        val rateMultiplier = 1 + 32 * capacityRatio
        return MathUtils.clampToLong(
            when (type) {
                TransmissionType.ITEM -> AutoInsert.itemRate * 8.0.pow(capacityRatio)
                TransmissionType.FLUID -> AutoInsert.fluidRate * rateMultiplier
                TransmissionType.ENERGY -> AutoInsert.energyRate * rateMultiplier
                TransmissionType.CHEMICAL -> AutoInsert.chemicalRate * rateMultiplier
                else -> error("Unsupported transmission type: $type")
            }
        )
    }

    override fun getComponentKey() = MekEmpSerializationConstants.COMPONENT_INSERTER
    override fun deserialize(componentTag: CompoundTag, provider: HolderLookup.Provider) = Unit
    override fun serialize(provider: HolderLookup.Provider) = CompoundTag()
}
