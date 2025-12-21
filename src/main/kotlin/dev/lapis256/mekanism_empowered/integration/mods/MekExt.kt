package dev.lapis256.mekanism_empowered.integration.mods

import com.jerry.mekanism_extras.common.block.attribute.ExtraAttributeTier
import com.jerry.mekanism_extras.common.registry.ExtraBlockType
import com.jerry.mekanism_extras.common.tier.AdvancedFactoryTier
import com.jerry.mekanism_extras.common.util.ExtraEnumUtils
import dev.lapis256.mekanism_empowered.common.init.MekEmpUpgrades.SPEED_AND_ENERGY_UPGRADES
import dev.lapis256.mekanism_empowered.common.util.AttributeUtil
import dev.lapis256.mekanism_empowered.core.common.util.AdditionalUpgradeUtil
import dev.lapis256.mekanism_empowered.integration.IntegrationProviderRegistry
import dev.lapis256.mekanism_empowered.integration.ModIntegration
import dev.lapis256.mekanism_empowered.integration.provider.FactoryParallelIntegration
import dev.lapis256.mekanism_empowered.integration.provider.FactoryUpgradeIntegration


internal object MekExt : ModIntegration {
    override val modId = "mekanism_extras"

    override fun initCommon() {
        AdditionalUpgradeUtil.addSupported(ExtraBlockType.ADVANCED_ELECTRIC_PUMP, *SPEED_AND_ENERGY_UPGRADES)
    }

    override fun initProvider(registry: IntegrationProviderRegistry) {
        registry.registerProvider(FactoryUpgradeIntegration { type, upgrades ->
            for (tier in ExtraEnumUtils.ADVANCED_FACTORY_TIERS) {
                val blockType = ExtraBlockType.getAdvancedFactory(tier, type) ?: continue
                AdditionalUpgradeUtil.addSupported(blockType, *upgrades)
            }
        })
        registry.registerProvider(FactoryParallelIntegration {
            val tier = AttributeUtil.get(it.blockType, ExtraAttributeTier::class)?.tier as? AdvancedFactoryTier
            tier?.processes ?: 1
        })
    }
}
