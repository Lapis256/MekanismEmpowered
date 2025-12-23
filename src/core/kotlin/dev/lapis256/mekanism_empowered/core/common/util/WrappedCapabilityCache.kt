package dev.lapis256.mekanism_empowered.core.common.util

import mekanism.api.chemical.IChemicalHandler
import mekanism.api.energy.IStrictEnergyHandler
import mekanism.api.heat.IHeatHandler
import mekanism.common.capabilities.Capabilities
import mekanism.common.integration.energy.BlockEnergyCapabilityCache
import mekanism.common.lib.transmitter.TransmissionType
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.neoforged.neoforge.capabilities.BlockCapabilityCache
import net.neoforged.neoforge.fluids.capability.IFluidHandler
import net.neoforged.neoforge.items.IItemHandler


/**
 * キャッシュされた Capability をラップするクラス
 */
abstract class WrappedCapabilityCache<CAPABILITY> {
    abstract val capability: CAPABILITY?

    companion object {
        fun createFromTransmissionType(
            type: TransmissionType,
            level: ServerLevel,
            blockPos: BlockPos,
            context: Direction
        ): WrappedCapabilityCache<*> =
            when (type) {
                TransmissionType.ITEM -> Item(level, blockPos, context)
                TransmissionType.CHEMICAL -> Chemical(level, blockPos, context)
                TransmissionType.FLUID -> Fluid(level, blockPos, context)
                TransmissionType.ENERGY -> Energy(level, blockPos, context)
                TransmissionType.HEAT -> Heat(level, blockPos, context)
            }

    }

    abstract class WrappedBlockCapabilityCache<CAPABILITY>(val cache: BlockCapabilityCache<CAPABILITY, Direction?>) :
        WrappedCapabilityCache<CAPABILITY>() {
        override val capability get() = cache.capability
    }

    class Item(cache: BlockCapabilityCache<IItemHandler, Direction?>) : WrappedBlockCapabilityCache<IItemHandler>(cache) {
        constructor(level: ServerLevel, blockPos: BlockPos, context: Direction?) : this(Capabilities.ITEM.createCache(level, blockPos, context))
    }

    class Chemical(cache: BlockCapabilityCache<IChemicalHandler, Direction?>) : WrappedBlockCapabilityCache<IChemicalHandler>(cache) {
        constructor(level: ServerLevel, blockPos: BlockPos, context: Direction?) : this(Capabilities.CHEMICAL.createCache(level, blockPos, context))
    }

    class Fluid(cache: BlockCapabilityCache<IFluidHandler, Direction?>) : WrappedBlockCapabilityCache<IFluidHandler>(cache) {
        constructor(level: ServerLevel, blockPos: BlockPos, context: Direction?) : this(Capabilities.FLUID.createCache(level, blockPos, context))
    }

    class Heat(cache: BlockCapabilityCache<IHeatHandler, Direction?>) : WrappedBlockCapabilityCache<IHeatHandler>(cache) {
        constructor(level: ServerLevel, blockPos: BlockPos, context: Direction?) : this(
            BlockCapabilityCache.create(
                Capabilities.HEAT,
                level,
                blockPos,
                @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS") context
            )
        )
    }

    class Energy(val cache: BlockEnergyCapabilityCache) : WrappedCapabilityCache<IStrictEnergyHandler>() {
        constructor(level: ServerLevel, blockPos: BlockPos, context: Direction?) : this(BlockEnergyCapabilityCache.create(level, blockPos, context))

        override val capability get() = cache.capability
    }
}
