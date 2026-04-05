package dev.lapis256.mekanism_empowered.client

import dev.lapis256.mekanism_empowered.api.MekanismEmpoweredAPI
import dev.lapis256.mekanism_empowered.integration.Integrations
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.ModContainer
import net.neoforged.fml.common.Mod
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent
import net.neoforged.neoforge.client.gui.ConfigurationScreen
import net.neoforged.neoforge.client.gui.IConfigScreenFactory


@Mod(value = MekanismEmpoweredAPI.MOD_ID, dist = [Dist.CLIENT])
class MekanismEmpoweredClient(modContainer: ModContainer, modEventBus: IEventBus) {
    init {
        modEventBus.addListener(::clientSetup)

        modContainer.registerExtensionPoint(
            IConfigScreenFactory::class.java,
            IConfigScreenFactory { mc, parent -> ConfigurationScreen(mc, parent) })

        Integrations.initClient(modEventBus)
    }

    private fun clientSetup(event: FMLClientSetupEvent) {
    }
}
