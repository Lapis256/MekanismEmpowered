package dev.lapis256.mekanism_empowered.data

import dev.lapis256.mekanism_empowered.api.MekanismEmpoweredAPI
import dev.lapis256.mekanism_empowered.data.provider.MekEmpGlobalLootModifierProvider
import dev.lapis256.mekanism_empowered.data.provider.MekEmpItemModelProvider
import dev.lapis256.mekanism_empowered.data.provider.MekEmpLanguageProvider
import dev.lapis256.mekanism_empowered.data.provider.MekEmpRecipeProvider
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.data.event.GatherDataEvent


@EventBusSubscriber(modid = MekanismEmpoweredAPI.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
object MekanismEmpoweredDataGenerator {
    @SubscribeEvent
    private fun onGatherData(event: GatherDataEvent) {
        val generator = event.generator
        val output = generator.packOutput
        val existingFileHelper = event.existingFileHelper
        val lookupProvider = event.lookupProvider

        generator.addProvider(event.includeClient(), MekEmpLanguageProvider(output))
        generator.addProvider(event.includeClient(), MekEmpItemModelProvider(output, existingFileHelper))

        generator.addProvider(event.includeServer(), MekEmpRecipeProvider(output, lookupProvider))
        generator.addProvider(event.includeServer(), MekEmpGlobalLootModifierProvider(output, lookupProvider))
    }
}
