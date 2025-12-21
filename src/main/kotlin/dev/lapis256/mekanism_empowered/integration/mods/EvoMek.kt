package dev.lapis256.mekanism_empowered.integration.mods

import dev.lapis256.mekanism_empowered.common.init.MekEmpUpgrades.ITEM_IN_OUT_MACHINE_UPGRADES
import dev.lapis256.mekanism_empowered.common.init.MekEmpUpgrades.addSupportedFactoryUpgrades
import dev.lapis256.mekanism_empowered.core.common.util.AdditionalUpgradeUtil
import dev.lapis256.mekanism_empowered.integration.ModIntegration
import fr.iglee42.evolvedmekanism.registries.EMBlockTypes
import fr.iglee42.evolvedmekanism.registries.EMFactoryType


internal object EvoMek : ModIntegration {
    override val modId = "evolvedmekanism"

    override fun initCommon() {
        addSupportedFactoryUpgrades(EMFactoryType.ALLOYING, *ITEM_IN_OUT_MACHINE_UPGRADES)

        AdditionalUpgradeUtil.addSupported(EMBlockTypes.ALLOYER, *ITEM_IN_OUT_MACHINE_UPGRADES)
        AdditionalUpgradeUtil.addSupported(EMBlockTypes.CHEMIXER, *ITEM_IN_OUT_MACHINE_UPGRADES)
        AdditionalUpgradeUtil.addSupported(EMBlockTypes.MELTER, *ITEM_IN_OUT_MACHINE_UPGRADES)
        AdditionalUpgradeUtil.addSupported(EMBlockTypes.SOLIDIFIER, *ITEM_IN_OUT_MACHINE_UPGRADES)
    }
}
