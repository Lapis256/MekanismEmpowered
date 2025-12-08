package dev.lapis256.mekanism_empowered.data

import dev.lapis256.mekanism_empowered.api.MekanismEmpoweredAPI
import dev.lapis256.mekanism_empowered.common.MekanismEmpowered
import dev.lapis256.mekanism_empowered.data.provider.MekEmpGlobalLootModifierProvider
import dev.lapis256.mekanism_empowered.data.provider.MekEmpItemModelProvider
import dev.lapis256.mekanism_empowered.data.provider.MekEmpLanguageProvider
import dev.lapis256.mekanism_empowered.data.provider.MekEmpRecipeProvider
import net.minecraftforge.data.event.GatherDataEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext


@Mod.EventBusSubscriber(modid = MekanismEmpoweredAPI.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
object MekanismEmpoweredDataGenerator {
    init {
        @Suppress("Removal", "Deprecation")
        val modBus = FMLJavaModLoadingContext.get().modEventBus
        modBus.register(MekanismEmpoweredDataGenerator)
    }

    @SubscribeEvent
    fun onGatherData(event: GatherDataEvent) {
        MekanismEmpowered.LOGGER.info("Gathering data...")

        val generator = event.generator
        val output = generator.packOutput
        val existingFileHelper = event.existingFileHelper

        generator.addProvider(event.includeClient(), MekEmpLanguageProvider(output))
        generator.addProvider(event.includeClient(), MekEmpItemModelProvider(output, existingFileHelper))

        generator.addProvider(event.includeServer(), MekEmpRecipeProvider(output))
        generator.addProvider(event.includeServer(), MekEmpGlobalLootModifierProvider(output))
    }
}
