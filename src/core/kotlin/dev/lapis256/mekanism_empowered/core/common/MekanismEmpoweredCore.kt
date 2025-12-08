package dev.lapis256.mekanism_empowered.core.common

import dev.lapis256.mekanism_empowered.core.api.MekanismEmpoweredCoreAPI
import dev.lapis256.mekanism_empowered.core.common.init.GlobalLootModifierSerializers
import dev.lapis256.mekanism_empowered.core.common.init.LootConditionTypes
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory


@Mod(MekanismEmpoweredCoreAPI.MOD_ID)
class MekanismEmpoweredCore(context: FMLJavaModLoadingContext) {
    companion object {
        @JvmField
        val LOGGER: Logger = LoggerFactory.getLogger(MekanismEmpoweredCoreAPI.MOD_ID)
    }

    init {
        GlobalLootModifierSerializers.REGISTRY.register(context.modEventBus)
        LootConditionTypes.REGISTRY.register(context.modEventBus)
    }

    object SerializationConstants {
        const val UPGRADES = "additional_upgrades"
    }
}
