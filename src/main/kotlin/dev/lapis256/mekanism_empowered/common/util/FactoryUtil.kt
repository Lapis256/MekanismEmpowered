package dev.lapis256.mekanism_empowered.common.util

import dev.lapis256.mekanism_empowered.integration.Integrations
import dev.lapis256.mekanism_empowered.integration.provider.FactoryParallelIntegration
import mekanism.common.tier.FactoryTier
import mekanism.common.tile.base.TileEntityMekanism


private fun getAdditionalFactoryParallelCount(tile: TileEntityMekanism) =
    Integrations.getProviders<FactoryParallelIntegration>().firstNotNullOfOrNull { it.getParallelCount(tile).takeIf { c -> c > 0 } }

val TileEntityMekanism.parallelCount
    get() = AttributeUtil.getTier<FactoryTier>(blockType)?.processes ?: getAdditionalFactoryParallelCount(this) ?: 1
