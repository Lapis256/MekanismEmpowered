package dev.lapis256.mekanism_empowered.integration.mods

import com.jerry.mekextras.common.registries.ExtraBlockTypes
import com.jerry.mekextras.common.util.ExtraEnumUtils
import dev.lapis256.mekanism_empowered.common.init.MekEmpUpgrades.SPEED_AND_ENERGY_UPGRADES
import dev.lapis256.mekanism_empowered.core.common.util.AdditionalUpgradeUtil
import dev.lapis256.mekanism_empowered.integration.IntegrationProviderRegistry
import dev.lapis256.mekanism_empowered.integration.ModIntegration
import dev.lapis256.mekanism_empowered.integration.provider.FactoryUpgradeIntegration


internal object MekExt : ModIntegration {
    override val modId = "mekanism_extras"

    override fun initCommon() {
        AdditionalUpgradeUtil.addSupported(ExtraBlockTypes.ADVANCED_ELECTRIC_PUMP, *SPEED_AND_ENERGY_UPGRADES)
    }

    override fun initProvider(registry: IntegrationProviderRegistry) {
        registry.registerProvider(FactoryUpgradeIntegration { type, upgrades ->
            for (tier in ExtraEnumUtils.EXTRA_FACTORY_TIERS) {
                val blockType = ExtraBlockTypes.getAdvancedFactory(tier, type) ?: continue
                AdditionalUpgradeUtil.addSupported(blockType, *upgrades)
            }
        })
    }
}
