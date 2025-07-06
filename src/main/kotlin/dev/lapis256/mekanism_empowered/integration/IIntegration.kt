package dev.lapis256.mekanism_empowered.integration

import dev.lapis256.mekanism_empowered.common.MekanismEmpowered
import net.neoforged.fml.ModList


interface IIntegration {
    val modId: String

    val loaded: Boolean
        get() = ModList.get().isLoaded(modId)

    fun initCommon()

    fun initCommonIntegration() {
        if (!loaded) return

        MekanismEmpowered.LOGGER.info("Initializing integration with $modId")

        initCommon()
    }

    fun initClient()

    fun initClientIntegration() {
        if (!loaded) return

        MekanismEmpowered.LOGGER.info("Initializing client integration with $modId")

        initClient()
    }
}
