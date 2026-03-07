package dev.lapis256.mekanism_empowered.integration.mods

import com.hecookin.chemlibmekanized.common.registries.CMLBlockTypes
import dev.lapis256.mekanism_empowered.common.init.MekEmpUpgrades.ITEM_IN_OUT_MACHINE_UPGRADES
import dev.lapis256.mekanism_empowered.core.common.util.AdditionalUpgradeUtil
import dev.lapis256.mekanism_empowered.integration.ModIntegration


internal object ChemLibMek : ModIntegration {
    override val modId = "chemlibmekanized"

    override fun initCommon() {
        AdditionalUpgradeUtil.addSupported(CMLBlockTypes.STOICHIOMETRIC_REACTOR, *ITEM_IN_OUT_MACHINE_UPGRADES)
    }
}
