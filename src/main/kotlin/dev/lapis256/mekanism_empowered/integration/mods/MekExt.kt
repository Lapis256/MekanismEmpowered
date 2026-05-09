package dev.lapis256.mekanism_empowered.integration.mods

import com.jerry.mekextras.common.integration.mekaf.registries.ExtraAdvancedFactoryBlockTypes
import com.jerry.mekextras.common.integration.mekmm.registries.ExtraMoreMachineBlockTypes
import com.jerry.mekextras.common.registries.ExtraBlockTypes
import com.jerry.mekextras.common.util.ExtraEnumUtils
import dev.lapis256.mekanism_empowered.common.factory.FactoryBlockResolver
import dev.lapis256.mekanism_empowered.common.factory.FactoryBlockResolverRegistry
import dev.lapis256.mekanism_empowered.common.factory.mekanismFactoryTypeSuppliers
import dev.lapis256.mekanism_empowered.common.init.MekEmpUpgrades.SPEED_AND_ENERGY_UPGRADES
import dev.lapis256.mekanism_empowered.core.common.util.AdditionalUpgradeUtil.addDeferredSupported
import dev.lapis256.mekanism_empowered.integration.ModIntegration
import net.neoforged.bus.api.IEventBus


internal object MekExt : ModIntegration {
    override val modId = "mekanism_extras"

    private val extraFactoryResolver by lazy {
        FactoryBlockResolver(
            name = "$modId:mekanism",
            types = mekanismFactoryTypeSuppliers,
            tiers = { ExtraEnumUtils.EXTRA_FACTORY_TIERS.asIterable() },
            resolver = { tier, type -> ExtraBlockTypes.getAdvancedFactory(tier, type) },
        )
    }

    private val extraAdvancedFactoryResolver by lazy {
        FactoryBlockResolver(
            name = "$modId:advanced",
            types = MekMM.advancedFactoryTypes,
            tiers = { ExtraEnumUtils.EXTRA_FACTORY_TIERS.asIterable() },
            resolver = { tier, type -> ExtraAdvancedFactoryBlockTypes.getExtraAdvancedFactory(tier, type) },
        )
    }

    private val extraMoreMachineFactoryResolver by lazy {
        FactoryBlockResolver(
            name = "$modId:more_machine",
            types = MekMM.moreMachineFactoryTypes,
            tiers = { ExtraEnumUtils.EXTRA_FACTORY_TIERS.asIterable() },
            resolver = { tier, type -> ExtraMoreMachineBlockTypes.getExtraMoreMachineFactory(tier, type) },
        )
    }

    override fun initCommon(modEventBus: IEventBus) {
        FactoryBlockResolverRegistry.register(extraFactoryResolver)

        if (MekMM.isLoaded) {
            FactoryBlockResolverRegistry.register(extraAdvancedFactoryResolver)
            FactoryBlockResolverRegistry.register(extraMoreMachineFactoryResolver)
        }

        addDeferredSupported({ ExtraBlockTypes.ADVANCED_ELECTRIC_PUMP }, *SPEED_AND_ENERGY_UPGRADES)
    }
}
