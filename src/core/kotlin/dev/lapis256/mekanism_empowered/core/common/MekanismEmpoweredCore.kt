package dev.lapis256.mekanism_empowered.core.common

import dev.lapis256.mekanism_empowered.core.api.MekanismEmpoweredCoreAPI
import dev.lapis256.mekanism_empowered.core.common.init.GlobalLootModifierSerializers
import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.ModContainer
import net.neoforged.fml.common.Mod
import org.slf4j.Logger
import org.slf4j.LoggerFactory


@Mod(MekanismEmpoweredCoreAPI.MOD_ID)
class MekanismEmpoweredCore(modContainer: ModContainer, modEventBus: IEventBus) {
    companion object {
        @JvmField
        val LOGGER: Logger = LoggerFactory.getLogger(MekanismEmpoweredCoreAPI.MOD_ID)
    }

    init {
        GlobalLootModifierSerializers.REGISTRY.register(modEventBus)
    }

    object SerializationConstants {
        const val UPGRADES = "additional_upgrades"
    }
}
