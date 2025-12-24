package dev.lapis256.mekanism_empowered.integration.mods

import dev.lapis256.mekanism_empowered.common.init.MekEmpUpgrades.ITEM_IN_OUT_MACHINE_UPGRADES
import dev.lapis256.mekanism_empowered.core.common.util.AdditionalUpgradeUtil
import dev.lapis256.mekanism_empowered.integration.ModIntegration
import io.github.masyumero.emextras.common.content.blocktype.EMExtraFactoryType
import io.github.masyumero.emextras.common.registry.EMExtrasBlockType
import io.github.masyumero.emextras.common.util.EMExtraEnumUtils
import mekanism.api.Upgrade


internal object EvoMekExt : ModIntegration {
    override val modId = "emextras"

    override fun initCommon() {
        addSupportedFactoryUpgrades(EMExtraFactoryType.ALLOYING, *ITEM_IN_OUT_MACHINE_UPGRADES)

        addSupportedFactoryUpgrades(EMExtraFactoryType.ENRICHING, *ITEM_IN_OUT_MACHINE_UPGRADES)
        addSupportedFactoryUpgrades(EMExtraFactoryType.CRUSHING, *ITEM_IN_OUT_MACHINE_UPGRADES)
        addSupportedFactoryUpgrades(EMExtraFactoryType.SMELTING, *ITEM_IN_OUT_MACHINE_UPGRADES)
        addSupportedFactoryUpgrades(EMExtraFactoryType.SAWING, *ITEM_IN_OUT_MACHINE_UPGRADES)
        addSupportedFactoryUpgrades(EMExtraFactoryType.COMPRESSING, *ITEM_IN_OUT_MACHINE_UPGRADES)
        addSupportedFactoryUpgrades(EMExtraFactoryType.COMBINING, *ITEM_IN_OUT_MACHINE_UPGRADES)
        addSupportedFactoryUpgrades(EMExtraFactoryType.INFUSING, *ITEM_IN_OUT_MACHINE_UPGRADES)
        addSupportedFactoryUpgrades(EMExtraFactoryType.PURIFYING, *ITEM_IN_OUT_MACHINE_UPGRADES)
        addSupportedFactoryUpgrades(EMExtraFactoryType.INJECTING, *ITEM_IN_OUT_MACHINE_UPGRADES)
    }

    private fun addSupportedFactoryUpgrades(type: EMExtraFactoryType, vararg upgrades: Upgrade) {
        for (tier in EMExtraEnumUtils.EMEXTRA_FACTORY_TIERS) {
            if (type != EMExtraFactoryType.ALLOYING && !tier.isEvolved) {
                continue
            }

            AdditionalUpgradeUtil.addSupported(EMExtrasBlockType.getEMExtraFactory(tier, type), *upgrades)
        }
    }
}
