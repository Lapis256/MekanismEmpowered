package dev.lapis256.mekanism_empowered.integration

import dev.lapis256.mekanism_empowered.integration.mods.*
import dev.lapis256.mekanism_empowered.integration.provider.IntegrationProvider
import net.neoforged.bus.api.IEventBus
import kotlin.reflect.KClass


internal object Integrations {
    private val integrations = listOf(
        ChemLibMek,
        EvoMek,
        EvoMekExt,
        MekElm,
        MekExt,
        MekMM,
    )

    private val integrationProviders: MutableMap<IntegrationProviderName, MutableSet<IntegrationProvider>> = mutableMapOf()

    init {
        val providerRegistry = IntegrationProviderRegistry { integrationProviders.getOrPut(it.name, ::mutableSetOf).add(it) }
        integrations.forEach { it.initIntegrationProvider(providerRegistry) }
    }

    @Suppress("UNCHECKED_CAST")
    fun <P : IntegrationProvider> getProviders(type: KClass<out P>) =
        integrationProviders.getOrPut(IntegrationProviderName.of(type), ::mutableSetOf).toSet() as Set<P>

    inline fun <reified T : IntegrationProvider> getProviders() = getProviders(T::class)

    fun initCommon(modEventBus: IEventBus) {
        integrations.forEach { it.initCommonIntegration(modEventBus) }
    }

    fun initClient(modEventBus: IEventBus) {
        integrations.forEach { it.initClientIntegration(modEventBus) }
    }
}
