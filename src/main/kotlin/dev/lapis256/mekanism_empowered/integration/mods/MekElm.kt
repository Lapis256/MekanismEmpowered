package dev.lapis256.mekanism_empowered.integration.mods

import com.fxd927.mekanismelements.common.registries.MSBlockTypes
import dev.lapis256.mekanism_empowered.common.init.MekEmpUpgrades.ITEM_INPUT_MACHINE_UPGRADES
import dev.lapis256.mekanism_empowered.common.init.MekEmpUpgrades.MACHINE_UPGRADES
import dev.lapis256.mekanism_empowered.core.common.util.AdditionalUpgradeUtil
import dev.lapis256.mekanism_empowered.integration.ModIntegration
import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent


object MekElm : ModIntegration {
    override val modId = "mekanismelements"

    override fun initCommon(modEventBus: IEventBus) {
        modEventBus.addListener { _: FMLCommonSetupEvent ->
            AdditionalUpgradeUtil.addSupported(MSBlockTypes.ADSORPTION_SEPARATOR, *MACHINE_UPGRADES)
            AdditionalUpgradeUtil.addSupported(MSBlockTypes.RADIATION_IRRADIATOR, *ITEM_INPUT_MACHINE_UPGRADES)
        }
    }
}
