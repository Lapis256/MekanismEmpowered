package dev.lapis256.mekanism_empowered.integration

import dev.lapis256.mekanism_empowered.integration.provider.IntegrationProvider


internal fun interface IntegrationProviderRegistry {
    fun registerProvider(provider: IntegrationProvider)
}
