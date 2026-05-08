package dev.lapis256.mekanism_empowered.integration.mods

import com.jerry.mekextras.common.util.ExtraEnumUtils
import dev.lapis256.mekanism_empowered.common.factory.FactoryBlockResolver
import dev.lapis256.mekanism_empowered.common.factory.FactoryBlockResolverRegistry
import dev.lapis256.mekanism_empowered.common.factory.MekanismFactoryTypeKeys
import dev.lapis256.mekanism_empowered.common.factory.mekanismFactoryTypeSuppliers
import dev.lapis256.mekanism_empowered.integration.ModIntegration
import fr.iglee42.evolvedmekanism.registries.EMFactoryType
import io.github.masyumero.emextras.common.content.blocktype.EMExtraFactoryType
import io.github.masyumero.emextras.common.integration.mekaf.registries.EMExtraAdvancedFactoryBlockTypes
import io.github.masyumero.emextras.common.integration.mekmm.registries.EMExtraMoreMachineBlockTypes
import io.github.masyumero.emextras.common.registry.EMExtraBlockTypes
import io.github.masyumero.emextras.common.util.EMExtraEnumUtils
import net.neoforged.bus.api.IEventBus

internal object EvoMekExt : ModIntegration {
    override val modId = "emextras"

    private val emExtraFactoryResolver by lazy {
        FactoryBlockResolver(
            name = "$modId:emextra",
            types = mapOf(
                MekanismFactoryTypeKeys.SMELTING to { EMExtraFactoryType.SMELTING },
                MekanismFactoryTypeKeys.ENRICHING to { EMExtraFactoryType.ENRICHING },
                MekanismFactoryTypeKeys.CRUSHING to { EMExtraFactoryType.CRUSHING },
                MekanismFactoryTypeKeys.COMPRESSING to { EMExtraFactoryType.COMPRESSING },
                MekanismFactoryTypeKeys.COMBINING to { EMExtraFactoryType.COMBINING },
                MekanismFactoryTypeKeys.PURIFYING to { EMExtraFactoryType.PURIFYING },
                MekanismFactoryTypeKeys.INJECTING to { EMExtraFactoryType.INJECTING },
                MekanismFactoryTypeKeys.INFUSING to { EMExtraFactoryType.INFUSING },
                MekanismFactoryTypeKeys.SAWING to { EMExtraFactoryType.SAWING },
                EvoMek.FactoryTypeKeys.ALLOYING to { EMExtraFactoryType.ALLOYING },
            ),
            tiers = { EMExtraEnumUtils.EMEXTRA_FACTORY_TIERS.asIterable() },
            resolver = { tier, type -> EMExtraBlockTypes.getEMExtraFactory(tier, type) },
        )
    }

    private val extraFactoryResolver by lazy {
        FactoryBlockResolver(
            name = "$modId:mekanism_extras",
            types = mekanismFactoryTypeSuppliers + (EvoMek.FactoryTypeKeys.ALLOYING to { EMFactoryType.ALLOYING }),
            tiers = { ExtraEnumUtils.EXTRA_FACTORY_TIERS.asIterable() },
            resolver = { tier, type -> EMExtraBlockTypes.getExtraFactory(tier, type) },
        )
    }

    private val emExtraAdvancedFactoryResolver by lazy {
        FactoryBlockResolver(
            name = "$modId:advanced",
            types = MekMM.advancedFactoryTypes,
            tiers = { EMExtraEnumUtils.EMEXTRA_FACTORY_TIERS.asIterable() },
            resolver = { tier, type -> EMExtraAdvancedFactoryBlockTypes.getEMExtraAdvancedFactory(tier, type) },
        )
    }

    private val emExtraMoreMachineFactoryResolver by lazy {
        FactoryBlockResolver(
            name = "$modId:more_machine",
            types = MekMM.moreMachineFactoryTypes,
            tiers = { EMExtraEnumUtils.EMEXTRA_FACTORY_TIERS.asIterable() },
            resolver = { tier, type -> EMExtraMoreMachineBlockTypes.getEMExtraMoreMachineFactory(tier, type) },
        )
    }

    override fun initCommon(modEventBus: IEventBus) {
        FactoryBlockResolverRegistry.register(emExtraFactoryResolver)
        FactoryBlockResolverRegistry.register(extraFactoryResolver)

        if (MekMM.isLoaded) {
            FactoryBlockResolverRegistry.register(emExtraAdvancedFactoryResolver)
            FactoryBlockResolverRegistry.register(emExtraMoreMachineFactoryResolver)
        }
    }
}
