package dev.lapis256.mekanism_empowered.integration.mods

import com.jerry.mekaf.common.content.blocktype.AdvancedFactoryType
import com.jerry.mekaf.common.registries.AdvancedFactoryBlockTypes
import com.jerry.meklm.common.registries.LargeMachineBlockTypes
import com.jerry.meklm.common.tile.INotNeedConfig
import com.jerry.mekmm.common.content.blocktype.MoreMachineFactoryType
import com.jerry.mekmm.common.registries.MoreMachineBlockTypes
import com.jerry.mekmm.common.tile.prefab.TileEntityMoreMachineGenerator
import com.jerry.mekmm.common.util.MoreMachineUtils
import dev.lapis256.mekanism_empowered.api.MekEmpUpgrade
import dev.lapis256.mekanism_empowered.common.init.MekEmpUpgrades.ITEM_INPUT_MACHINE_UPGRADES
import dev.lapis256.mekanism_empowered.common.init.MekEmpUpgrades.ITEM_IN_OUT_MACHINE_UPGRADES
import dev.lapis256.mekanism_empowered.common.init.MekEmpUpgrades.ITEM_OUTPUT_MACHINE_UPGRADES
import dev.lapis256.mekanism_empowered.common.init.MekEmpUpgrades.MACHINE_UPGRADES
import dev.lapis256.mekanism_empowered.common.init.MekEmpUpgrades.SPEED_AND_ENERGY_UPGRADES
import dev.lapis256.mekanism_empowered.common.factory.FactoryBlockResolver
import dev.lapis256.mekanism_empowered.common.factory.FactoryBlockResolverRegistry
import dev.lapis256.mekanism_empowered.common.factory.FactoryTypeKey
import dev.lapis256.mekanism_empowered.common.factory.addDeferredSupportedForFactory
import dev.lapis256.mekanism_empowered.core.common.util.AdditionalUpgradeUtil.addDeferredSupported
import dev.lapis256.mekanism_empowered.core.common.util.TileUpgradeSupportFallbackRegistry
import dev.lapis256.mekanism_empowered.integration.ModIntegration
import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent


internal object MekMM : ModIntegration {
    override val modId = "mekmm"

    object FactoryTypeKeys {
        val PRESSURISED_REACTING = FactoryTypeKey.of(modId, "pressurised_reacting")
        val LIQUIFYING = FactoryTypeKey.of(modId, "liquifying")
        val OXIDIZING = FactoryTypeKey.of(modId, "oxidizing")
        val DISSOLVING = FactoryTypeKey.of(modId, "dissolving")
        val CRYSTALLIZING = FactoryTypeKey.of(modId, "crystallizing")
        val CHEMICAL_INFUSING = FactoryTypeKey.of(modId, "chemical_infusing")
        val WASHING = FactoryTypeKey.of(modId, "washing")
        val CENTRIFUGING = FactoryTypeKey.of(modId, "centrifuging")
        val RECYCLING = FactoryTypeKey.of(modId, "recycling")
        val PLANTING_STATION = FactoryTypeKey.of(modId, "planting_station")
        val CNC_STAMPING = FactoryTypeKey.of(modId, "cnc_stamping")
        val CNC_LATHING = FactoryTypeKey.of(modId, "cnc_lathing")
        val CNC_ROLLING_MILL = FactoryTypeKey.of(modId, "cnc_rolling_mill")
        val REPLICATING = FactoryTypeKey.of(modId, "replicating")
    }

    val advancedFactoryTypes by lazy {
        mapOf(
            FactoryTypeKeys.PRESSURISED_REACTING to { AdvancedFactoryType.PRESSURISED_REACTING },
            FactoryTypeKeys.LIQUIFYING to { AdvancedFactoryType.LIQUIFYING },
            FactoryTypeKeys.OXIDIZING to { AdvancedFactoryType.OXIDIZING },
            FactoryTypeKeys.DISSOLVING to { AdvancedFactoryType.DISSOLVING },
            FactoryTypeKeys.CRYSTALLIZING to { AdvancedFactoryType.CRYSTALLIZING },
            FactoryTypeKeys.CHEMICAL_INFUSING to { AdvancedFactoryType.CHEMICAL_INFUSING },
            FactoryTypeKeys.WASHING to { AdvancedFactoryType.WASHING },
            FactoryTypeKeys.CENTRIFUGING to { AdvancedFactoryType.CENTRIFUGING },
        )
    }

    private val advancedFactoryResolver by lazy {
        FactoryBlockResolver(
            name = "$modId:advanced",
            types = advancedFactoryTypes,
            tiers = { MoreMachineUtils.getFactoryTier().asIterable() },
            resolver = { tier, type -> AdvancedFactoryBlockTypes.getAdvancedFactory(tier, type) },
        )
    }

    val moreMachineFactoryTypes by lazy {
        mapOf(
            FactoryTypeKeys.RECYCLING to { MoreMachineFactoryType.RECYCLING },
            FactoryTypeKeys.PLANTING_STATION to { MoreMachineFactoryType.PLANTING_STATION },
            FactoryTypeKeys.CNC_STAMPING to { MoreMachineFactoryType.CNC_STAMPING },
            FactoryTypeKeys.CNC_LATHING to { MoreMachineFactoryType.CNC_LATHING },
            FactoryTypeKeys.CNC_ROLLING_MILL to { MoreMachineFactoryType.CNC_ROLLING_MILL },
            FactoryTypeKeys.REPLICATING to { MoreMachineFactoryType.REPLICATING },
        )
    }

    private val moreMachineFactoryResolver by lazy {
        FactoryBlockResolver(
            name = "$modId:more_machine",
            types = moreMachineFactoryTypes,
            tiers = { MoreMachineUtils.getFactoryTier().asIterable() },
            resolver = { tier, type -> MoreMachineBlockTypes.getMoreMachineFactory(tier, type) },
        )
    }

    override fun initCommon(modEventBus: IEventBus) {
        FactoryBlockResolverRegistry.register(advancedFactoryResolver)
        FactoryBlockResolverRegistry.register(moreMachineFactoryResolver)

        val unsupported = setOf(
            MekEmpUpgrade.AUTO_INSERTER,
            MekEmpUpgrade.IO_CAPACITY,
            MekEmpUpgrade.FAST_ITEM_INSERT,
            MekEmpUpgrade.FAST_ITEM_EJECT
        )

        TileUpgradeSupportFallbackRegistry.registerUnsupportedUpgradePredicate { tile, upgrade ->
            (tile is INotNeedConfig || tile is TileEntityMoreMachineGenerator) && unsupported.contains(upgrade)
        }

        modEventBus.addListener { _: FMLCommonSetupEvent -> registerSupportedUpgrades() }
    }

    private fun registerSupportedUpgrades() {
        addDeferredSupportedForFactory(FactoryTypeKeys.PRESSURISED_REACTING, *ITEM_IN_OUT_MACHINE_UPGRADES)
        addDeferredSupportedForFactory(FactoryTypeKeys.LIQUIFYING, *ITEM_IN_OUT_MACHINE_UPGRADES)

        addDeferredSupportedForFactory(FactoryTypeKeys.OXIDIZING, *ITEM_INPUT_MACHINE_UPGRADES)
        addDeferredSupportedForFactory(FactoryTypeKeys.DISSOLVING, *ITEM_INPUT_MACHINE_UPGRADES)

        addDeferredSupportedForFactory(FactoryTypeKeys.CRYSTALLIZING, *ITEM_OUTPUT_MACHINE_UPGRADES)

        addDeferredSupportedForFactory(FactoryTypeKeys.CHEMICAL_INFUSING, *MACHINE_UPGRADES)
        addDeferredSupportedForFactory(FactoryTypeKeys.WASHING, *MACHINE_UPGRADES)
        addDeferredSupportedForFactory(FactoryTypeKeys.CENTRIFUGING, *MACHINE_UPGRADES)

        addDeferredSupported({ MoreMachineBlockTypes.RECYCLER }, *ITEM_IN_OUT_MACHINE_UPGRADES)
        addDeferredSupported({ MoreMachineBlockTypes.PLANTING_STATION }, *ITEM_IN_OUT_MACHINE_UPGRADES)
        addDeferredSupported({ MoreMachineBlockTypes.CNC_STAMPER }, *ITEM_IN_OUT_MACHINE_UPGRADES)
        addDeferredSupported({ MoreMachineBlockTypes.CNC_LATHE }, *ITEM_IN_OUT_MACHINE_UPGRADES)
        addDeferredSupported({ MoreMachineBlockTypes.CNC_ROLLING_MILL }, *ITEM_IN_OUT_MACHINE_UPGRADES)
        addDeferredSupported({ MoreMachineBlockTypes.REPLICATOR }, *ITEM_IN_OUT_MACHINE_UPGRADES)
        addDeferredSupported({ MoreMachineBlockTypes.FLUID_REPLICATOR }, *ITEM_IN_OUT_MACHINE_UPGRADES)
        addDeferredSupported({ MoreMachineBlockTypes.AMBIENT_GAS_COLLECTOR }, *ITEM_IN_OUT_MACHINE_UPGRADES)

        addDeferredSupportedForFactory(FactoryTypeKeys.RECYCLING, *ITEM_IN_OUT_MACHINE_UPGRADES)
        addDeferredSupportedForFactory(FactoryTypeKeys.PLANTING_STATION, *ITEM_IN_OUT_MACHINE_UPGRADES)
        addDeferredSupportedForFactory(FactoryTypeKeys.CNC_STAMPING, *ITEM_IN_OUT_MACHINE_UPGRADES)
        addDeferredSupportedForFactory(FactoryTypeKeys.CNC_LATHING, *ITEM_IN_OUT_MACHINE_UPGRADES)
        addDeferredSupportedForFactory(FactoryTypeKeys.CNC_ROLLING_MILL, *ITEM_IN_OUT_MACHINE_UPGRADES)
        addDeferredSupportedForFactory(FactoryTypeKeys.REPLICATING, *ITEM_IN_OUT_MACHINE_UPGRADES)

        addDeferredSupported({ LargeMachineBlockTypes.LARGE_ROTARY_CONDENSENTRATOR }, *SPEED_AND_ENERGY_UPGRADES)
        addDeferredSupported({ LargeMachineBlockTypes.LARGE_CHEMICAL_INFUSER }, *SPEED_AND_ENERGY_UPGRADES)
        addDeferredSupported({ LargeMachineBlockTypes.LARGE_ELECTROLYTIC_SEPARATOR }, *SPEED_AND_ENERGY_UPGRADES)
        addDeferredSupported({ LargeMachineBlockTypes.LARGE_SOLAR_NEUTRON_ACTIVATOR }, MekEmpUpgrade.EMPOWERED_SPEED)
    }
}
