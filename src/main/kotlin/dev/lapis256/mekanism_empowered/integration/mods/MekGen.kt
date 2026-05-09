package dev.lapis256.mekanism_empowered.integration.mods

import dev.lapis256.mekanism_empowered.api.MekEmpUpgrade
import dev.lapis256.mekanism_empowered.core.common.util.TileUpgradeSupportFallbackRegistry
import dev.lapis256.mekanism_empowered.integration.ModIntegration
import mekanism.generators.common.tile.TileEntityGenerator
import net.neoforged.bus.api.IEventBus


internal object MekGen : ModIntegration {
    override val modId = "mekanismgenerators"

    override fun initCommon(modEventBus: IEventBus) {
        val unsupported = setOf(
            MekEmpUpgrade.AUTO_INSERTER,
            MekEmpUpgrade.IO_CAPACITY,
            MekEmpUpgrade.FAST_ITEM_INSERT
        )

        TileUpgradeSupportFallbackRegistry.registerUnsupportedUpgradePredicate { tile, upgrade ->
            tile is TileEntityGenerator && unsupported.contains(upgrade)
        }
    }
}
