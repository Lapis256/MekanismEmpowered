package dev.lapis256.mekanism_empowered.integration

import dev.lapis256.mekanism_empowered.common.MekanismEmpowered
import net.minecraftforge.fml.ModList


internal interface ModIntegration {
    val modId: String

    val isLoaded: Boolean
        get() = ModList.get().isLoaded(modId)

    fun initCommon()

    fun initCommonIntegration() {
        if (!isLoaded) return

        MekanismEmpowered.LOGGER.info("Initializing integration with $modId")

        initCommon()
    }

    fun initClient() {}

    fun initClientIntegration() {
        if (!isLoaded) return

        MekanismEmpowered.LOGGER.info("Initializing client integration with $modId")

        initClient()
    }

    fun initProvider(registry: IntegrationProviderRegistry) {}

    fun initIntegrationProvider(registry: IntegrationProviderRegistry) {
        if (!isLoaded) return

        MekanismEmpowered.LOGGER.info("Initializing integration providers for $modId")

        initProvider(registry)
    }
}
