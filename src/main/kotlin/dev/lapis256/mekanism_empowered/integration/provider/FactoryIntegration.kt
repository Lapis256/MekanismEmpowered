package dev.lapis256.mekanism_empowered.integration.provider

import dev.lapis256.mekanism_empowered.integration.IntegrationProviderName
import mekanism.api.Upgrade
import mekanism.common.content.blocktype.FactoryType


internal fun interface FactoryUpgradeIntegration : IntegrationProvider {
    override val name get() = IntegrationProviderName.of(FactoryUpgradeIntegration::class)

    fun addSupportedFactoryUpgrades(type: FactoryType, vararg upgrades: Upgrade)
}
