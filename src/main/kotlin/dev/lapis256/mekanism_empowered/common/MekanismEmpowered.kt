package dev.lapis256.mekanism_empowered.common

import dev.lapis256.mekanism_empowered.api.MekanismEmpoweredAPI
import dev.lapis256.mekanism_empowered.common.config.MekEmpConfig
import dev.lapis256.mekanism_empowered.common.init.MekEmpCreativeTab
import dev.lapis256.mekanism_empowered.common.init.MekEmpDataComponents
import dev.lapis256.mekanism_empowered.common.init.MekEmpItems
import dev.lapis256.mekanism_empowered.common.init.MekEmpUpgrades
import dev.lapis256.mekanism_empowered.common.network.MekEmpPacketHandler
import dev.lapis256.mekanism_empowered.integration.Integrations
import mekanism.common.lib.Version
import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.ModContainer
import net.neoforged.fml.common.Mod
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent
import org.slf4j.Logger
import org.slf4j.LoggerFactory


@Mod(MekanismEmpoweredAPI.MOD_ID)
class MekanismEmpowered(modContainer: ModContainer, modEventBus: IEventBus) {
    init {
        MekEmpConfig.registerConfigs(modContainer)

        MekEmpUpgrades.registerUpgradeInfo()
        modEventBus.addListener { _: FMLCommonSetupEvent -> MekEmpUpgrades.registerSupportedUpgrades() }

        modEventBus.addListener(MekEmpConfig::onConfigLoad)

        MekEmpItems.REGISTRY.register(modEventBus)
        MekEmpCreativeTab.REGISTRY.register(modEventBus)
        MekEmpDataComponents.REGISTRY.register(modEventBus)

        Integrations.initCommon(modEventBus)
    }

    val versionNumber = Version(modContainer)
    val packetHandler = MekEmpPacketHandler(modEventBus, versionNumber)

    companion object {
        @JvmField
        val LOGGER: Logger = LoggerFactory.getLogger(MekanismEmpoweredAPI.MOD_ID)
    }
}
