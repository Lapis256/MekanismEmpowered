package dev.lapis256.mekanism_empowered.integration.mods

import dev.lapis256.mekanism_empowered.common.factory.*
import dev.lapis256.mekanism_empowered.common.init.MekEmpUpgrades.ITEM_IN_OUT_MACHINE_UPGRADES
import dev.lapis256.mekanism_empowered.core.common.util.AdditionalUpgradeUtil.addDeferredSupported
import dev.lapis256.mekanism_empowered.integration.ModIntegration
import fr.iglee42.evolvedmekanism.registries.EMBlockTypes
import fr.iglee42.evolvedmekanism.registries.EMFactoryType
import fr.iglee42.evolvedmekanism.tiers.EMFactoryTier
import net.neoforged.bus.api.IEventBus


internal object EvoMek : ModIntegration {
    override val modId = "evolvedmekanism"

    object FactoryTypeKeys {
        val ALLOYING = FactoryTypeKey.of(modId, "alloying")
    }

    private val factoryResolver by lazy {
        FactoryBlockResolver(
            name = "$modId:self",
            types = mekanismFactoryTypeSuppliers + (FactoryTypeKeys.ALLOYING to { EMFactoryType.ALLOYING }),
            tiers = { FACTORY_TIERS.asIterable() },
            resolver = { tier, type -> EMBlockTypes.getFactory(tier, type) },
        )
    }

    override fun initCommon(modEventBus: IEventBus) {
        FactoryBlockResolverRegistry.register(factoryResolver)

        registerSupportedUpgrades()
    }

    private fun registerSupportedUpgrades() {
        addDeferredSupportedForFactory(FactoryTypeKeys.ALLOYING, *ITEM_IN_OUT_MACHINE_UPGRADES)

        addDeferredSupported({ EMBlockTypes.ALLOYER }, *ITEM_IN_OUT_MACHINE_UPGRADES)
        addDeferredSupported({ EMBlockTypes.CHEMIXER }, *ITEM_IN_OUT_MACHINE_UPGRADES)
        addDeferredSupported({ EMBlockTypes.MELTER }, *ITEM_IN_OUT_MACHINE_UPGRADES)
        addDeferredSupported({ EMBlockTypes.SOLIDIFIER }, *ITEM_IN_OUT_MACHINE_UPGRADES)
    }

    val FACTORY_TIERS by lazy {
        arrayOf(EMFactoryTier.OVERCLOCKED, EMFactoryTier.QUANTUM, EMFactoryTier.DENSE, EMFactoryTier.MULTIVERSAL, EMFactoryTier.CREATIVE)
    }
}
