package dev.lapis256.mekanism_empowered.integration.mods

import com.fxd927.mekanismelements.common.registries.MSBlockTypes
import dev.lapis256.mekanism_empowered.common.init.MekEmpUpgrades.ITEM_INPUT_MACHINE_UPGRADES
import dev.lapis256.mekanism_empowered.common.init.MekEmpUpgrades.MACHINE_UPGRADES
import dev.lapis256.mekanism_empowered.core.common.util.AdditionalUpgradeUtil.addDeferredSupported
import dev.lapis256.mekanism_empowered.integration.ModIntegration
import net.neoforged.bus.api.IEventBus


object MekElm : ModIntegration {
    override val modId = "mekanismelements"

    override fun initCommon(modEventBus: IEventBus) {
        addDeferredSupported({ MSBlockTypes.ADSORPTION_SEPARATOR }, *MACHINE_UPGRADES)
        addDeferredSupported({ MSBlockTypes.RADIATION_IRRADIATOR }, *ITEM_INPUT_MACHINE_UPGRADES)
    }
}
