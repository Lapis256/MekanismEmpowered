package dev.lapis256.mekanism_empowered.integration.mods

import com.jerry.mekaf.common.content.blocktype.AdvancedFactoryType
import com.jerry.mekextras.common.integration.mekaf.registries.ExtraAdvancedFactoryBlockTypes
import com.jerry.mekextras.common.integration.mekmm.registries.ExtraMoreMachineBlockTypes
import com.jerry.mekextras.common.registries.ExtraBlockTypes
import com.jerry.mekextras.common.util.ExtraEnumUtils
import com.jerry.mekmm.common.content.blocktype.MoreMachineFactoryType
import dev.lapis256.mekanism_empowered.common.init.MekEmpUpgrades.ITEM_INPUT_MACHINE_UPGRADES
import dev.lapis256.mekanism_empowered.common.init.MekEmpUpgrades.ITEM_IN_OUT_MACHINE_UPGRADES
import dev.lapis256.mekanism_empowered.common.init.MekEmpUpgrades.ITEM_OUTPUT_MACHINE_UPGRADES
import dev.lapis256.mekanism_empowered.common.init.MekEmpUpgrades.MACHINE_UPGRADES
import dev.lapis256.mekanism_empowered.common.init.MekEmpUpgrades.SPEED_AND_ENERGY_UPGRADES
import dev.lapis256.mekanism_empowered.core.common.util.AdditionalUpgradeUtil
import dev.lapis256.mekanism_empowered.integration.IntegrationProviderRegistry
import dev.lapis256.mekanism_empowered.integration.ModIntegration
import dev.lapis256.mekanism_empowered.integration.provider.FactoryUpgradeIntegration
import mekanism.api.Upgrade


internal object MekExt : ModIntegration {
    override val modId = "mekanism_extras"

    override fun initCommon() {
        AdditionalUpgradeUtil.addSupported(ExtraBlockTypes.ADVANCED_ELECTRIC_PUMP, *SPEED_AND_ENERGY_UPGRADES)


        addSupportedFactoryUpgrades(AdvancedFactoryType.PRESSURISED_REACTING, *ITEM_IN_OUT_MACHINE_UPGRADES)
        addSupportedFactoryUpgrades(AdvancedFactoryType.LIQUIFYING, *ITEM_IN_OUT_MACHINE_UPGRADES)

        addSupportedFactoryUpgrades(AdvancedFactoryType.OXIDIZING, *ITEM_INPUT_MACHINE_UPGRADES)
        addSupportedFactoryUpgrades(AdvancedFactoryType.DISSOLVING, *ITEM_INPUT_MACHINE_UPGRADES)

        addSupportedFactoryUpgrades(AdvancedFactoryType.CRYSTALLIZING, *ITEM_OUTPUT_MACHINE_UPGRADES)

        addSupportedFactoryUpgrades(AdvancedFactoryType.CHEMICAL_INFUSING, *MACHINE_UPGRADES)
        addSupportedFactoryUpgrades(AdvancedFactoryType.WASHING, *MACHINE_UPGRADES)
        addSupportedFactoryUpgrades(AdvancedFactoryType.CENTRIFUGING, *MACHINE_UPGRADES)

        addSupportedFactoryUpgrades(MoreMachineFactoryType.RECYCLING, *ITEM_IN_OUT_MACHINE_UPGRADES)
        addSupportedFactoryUpgrades(MoreMachineFactoryType.PLANTING_STATION, *ITEM_IN_OUT_MACHINE_UPGRADES)
        addSupportedFactoryUpgrades(MoreMachineFactoryType.CNC_STAMPING, *ITEM_IN_OUT_MACHINE_UPGRADES)
        addSupportedFactoryUpgrades(MoreMachineFactoryType.CNC_LATHING, *ITEM_IN_OUT_MACHINE_UPGRADES)
        addSupportedFactoryUpgrades(MoreMachineFactoryType.CNC_ROLLING_MILL, *ITEM_IN_OUT_MACHINE_UPGRADES)
        addSupportedFactoryUpgrades(MoreMachineFactoryType.REPLICATING, *ITEM_IN_OUT_MACHINE_UPGRADES)
    }

    override fun initProvider(registry: IntegrationProviderRegistry) {
        registry.registerProvider(FactoryUpgradeIntegration { type, upgrades ->
            for (tier in ExtraEnumUtils.EXTRA_FACTORY_TIERS) {
                val blockType = ExtraBlockTypes.getAdvancedFactory(tier, type) ?: continue
                AdditionalUpgradeUtil.addSupported(blockType, *upgrades)
            }
        })
    }

    private fun addSupportedFactoryUpgrades(type: AdvancedFactoryType, vararg upgrades: Upgrade) {
        for (tier in ExtraEnumUtils.EXTRA_FACTORY_TIERS) {
            val blockType = ExtraAdvancedFactoryBlockTypes.getExtraAdvancedFactory(tier, type) ?: continue
            AdditionalUpgradeUtil.addSupported(blockType, *upgrades)
        }
    }

    private fun addSupportedFactoryUpgrades(type: MoreMachineFactoryType, vararg upgrades: Upgrade) {
        for (tier in ExtraEnumUtils.EXTRA_FACTORY_TIERS) {
            val blockType = ExtraMoreMachineBlockTypes.getExtraMoreMachineFactory(tier, type) ?: continue
            AdditionalUpgradeUtil.addSupported(blockType, *upgrades)
        }
    }
}
