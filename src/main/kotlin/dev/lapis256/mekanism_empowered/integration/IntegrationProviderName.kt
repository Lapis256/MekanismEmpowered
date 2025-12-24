package dev.lapis256.mekanism_empowered.integration

import dev.lapis256.mekanism_empowered.integration.provider.IntegrationProvider
import kotlin.reflect.KClass


@JvmInline
value class IntegrationProviderName(val name: String) {
    companion object {
        fun of(provider: KClass<out IntegrationProvider>) =
            provider.simpleName?.let { IntegrationProviderName(it) } ?: error("Anonymous IntegrationProvider classes are not supported")
    }
}
