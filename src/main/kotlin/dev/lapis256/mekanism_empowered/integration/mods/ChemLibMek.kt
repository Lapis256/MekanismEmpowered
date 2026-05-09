package dev.lapis256.mekanism_empowered.integration.mods

import com.hecookin.chemlibmekanized.common.registries.CMLBlockTypes
import dev.lapis256.mekanism_empowered.common.init.MekEmpUpgrades.ITEM_IN_OUT_MACHINE_UPGRADES
import dev.lapis256.mekanism_empowered.core.common.util.AdditionalUpgradeUtil.addDeferredSupported
import dev.lapis256.mekanism_empowered.integration.ModIntegration
import net.neoforged.bus.api.IEventBus


internal object ChemLibMek : ModIntegration {
    override val modId = "chemlibmekanized"

    override fun initCommon(modEventBus: IEventBus) {
        addDeferredSupported({ CMLBlockTypes.STOICHIOMETRIC_REACTOR }, *ITEM_IN_OUT_MACHINE_UPGRADES)
    }
}
