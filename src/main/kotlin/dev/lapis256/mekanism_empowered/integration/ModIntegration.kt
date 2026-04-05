package dev.lapis256.mekanism_empowered.integration

import dev.lapis256.mekanism_empowered.common.MekanismEmpowered
import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.ModList


internal interface ModIntegration {
    val modId: String

    val isLoaded: Boolean
        get() = ModList.get().isLoaded(modId)

    fun initCommon(modEventBus: IEventBus)

    fun initCommonIntegration(modEventBus: IEventBus) {
        if (!isLoaded) return

        MekanismEmpowered.LOGGER.info("Initializing integration with $modId")

        initCommon(modEventBus)
    }

    fun initClient(modEventBus: IEventBus) {}

    fun initClientIntegration(modEventBus: IEventBus) {
        if (!isLoaded) return

        MekanismEmpowered.LOGGER.info("Initializing client integration with $modId")

        initClient(modEventBus)
    }

    fun initProvider(registry: IntegrationProviderRegistry) {}

    fun initIntegrationProvider(registry: IntegrationProviderRegistry) {
        if (!isLoaded) return

        MekanismEmpowered.LOGGER.info("Initializing integration providers for $modId")

        initProvider(registry)
    }
}
