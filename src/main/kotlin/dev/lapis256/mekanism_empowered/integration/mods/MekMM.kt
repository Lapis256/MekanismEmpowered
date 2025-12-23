package dev.lapis256.mekanism_empowered.integration.mods

import com.jerry.mekaf.common.content.blocktype.AdvancedFactoryType
import com.jerry.mekaf.common.registries.AdvancedFactoryBlockTypes
import com.jerry.mekmm.common.content.blocktype.MoreMachineFactoryType
import com.jerry.mekmm.common.registries.MoreMachineBlockTypes
import com.jerry.meklm.common.registries.LargeMachineBlockTypes
import dev.lapis256.mekanism_empowered.api.MekEmpUpgrade
import dev.lapis256.mekanism_empowered.common.init.MekEmpUpgrades.ITEM_INPUT_MACHINE_UPGRADES
import dev.lapis256.mekanism_empowered.common.init.MekEmpUpgrades.ITEM_IN_OUT_MACHINE_UPGRADES
import dev.lapis256.mekanism_empowered.common.init.MekEmpUpgrades.ITEM_OUTPUT_MACHINE_UPGRADES
import dev.lapis256.mekanism_empowered.common.init.MekEmpUpgrades.MACHINE_UPGRADES
import dev.lapis256.mekanism_empowered.common.init.MekEmpUpgrades.SPEED_AND_ENERGY_UPGRADES
import dev.lapis256.mekanism_empowered.core.common.util.AdditionalUpgradeUtil
import dev.lapis256.mekanism_empowered.integration.ModIntegration
import mekanism.api.Upgrade
import mekanism.common.util.EnumUtils


internal object MekMM : ModIntegration {
    override val modId = "mekmm"

    override fun initCommon() {
        registerFactoryUpgrades(AdvancedFactoryType.PRESSURISED_REACTING, *ITEM_IN_OUT_MACHINE_UPGRADES)
        registerFactoryUpgrades(AdvancedFactoryType.LIQUIFYING, *ITEM_IN_OUT_MACHINE_UPGRADES)

        registerFactoryUpgrades(AdvancedFactoryType.OXIDIZING, *ITEM_INPUT_MACHINE_UPGRADES)
        registerFactoryUpgrades(AdvancedFactoryType.DISSOLVING, *ITEM_INPUT_MACHINE_UPGRADES)

        registerFactoryUpgrades(AdvancedFactoryType.CRYSTALLIZING, *ITEM_OUTPUT_MACHINE_UPGRADES)

        registerFactoryUpgrades(AdvancedFactoryType.CHEMICAL_INFUSING, *MACHINE_UPGRADES)
        registerFactoryUpgrades(AdvancedFactoryType.WASHING, *MACHINE_UPGRADES)
        registerFactoryUpgrades(AdvancedFactoryType.CENTRIFUGING, *MACHINE_UPGRADES)

        AdditionalUpgradeUtil.addSupported(MoreMachineBlockTypes.RECYCLER, *ITEM_IN_OUT_MACHINE_UPGRADES)
        AdditionalUpgradeUtil.addSupported(MoreMachineBlockTypes.PLANTING_STATION, *ITEM_IN_OUT_MACHINE_UPGRADES)
        AdditionalUpgradeUtil.addSupported(MoreMachineBlockTypes.CNC_STAMPER, *ITEM_IN_OUT_MACHINE_UPGRADES)
        AdditionalUpgradeUtil.addSupported(MoreMachineBlockTypes.CNC_LATHE, *ITEM_IN_OUT_MACHINE_UPGRADES)
        AdditionalUpgradeUtil.addSupported(MoreMachineBlockTypes.CNC_ROLLING_MILL, *ITEM_IN_OUT_MACHINE_UPGRADES)
        AdditionalUpgradeUtil.addSupported(MoreMachineBlockTypes.REPLICATOR, *ITEM_IN_OUT_MACHINE_UPGRADES)
        AdditionalUpgradeUtil.addSupported(MoreMachineBlockTypes.FLUID_REPLICATOR, *ITEM_IN_OUT_MACHINE_UPGRADES)
        AdditionalUpgradeUtil.addSupported(MoreMachineBlockTypes.AMBIENT_GAS_COLLECTOR, *ITEM_IN_OUT_MACHINE_UPGRADES)

        registerFactoryUpgrades(MoreMachineFactoryType.RECYCLING, *ITEM_IN_OUT_MACHINE_UPGRADES)
        registerFactoryUpgrades(MoreMachineFactoryType.PLANTING_STATION, *ITEM_IN_OUT_MACHINE_UPGRADES)
        registerFactoryUpgrades(MoreMachineFactoryType.CNC_STAMPING, *ITEM_IN_OUT_MACHINE_UPGRADES)
        registerFactoryUpgrades(MoreMachineFactoryType.CNC_LATHING, *ITEM_IN_OUT_MACHINE_UPGRADES)
        registerFactoryUpgrades(MoreMachineFactoryType.CNC_ROLLING_MILL, *ITEM_IN_OUT_MACHINE_UPGRADES)
        registerFactoryUpgrades(MoreMachineFactoryType.REPLICATING, *ITEM_IN_OUT_MACHINE_UPGRADES)

        AdditionalUpgradeUtil.addSupported(LargeMachineBlockTypes.LARGE_ROTARY_CONDENSENTRATOR, *SPEED_AND_ENERGY_UPGRADES)
        AdditionalUpgradeUtil.addSupported(LargeMachineBlockTypes.LARGE_CHEMICAL_INFUSER, *SPEED_AND_ENERGY_UPGRADES)
        AdditionalUpgradeUtil.addSupported(LargeMachineBlockTypes.LARGE_ELECTROLYTIC_SEPARATOR, *SPEED_AND_ENERGY_UPGRADES)
        AdditionalUpgradeUtil.addSupported(LargeMachineBlockTypes.LARGE_SOLAR_NEUTRON_ACTIVATOR, MekEmpUpgrade.EMPOWERED_SPEED)
    }

    private fun registerFactoryUpgrades(type: AdvancedFactoryType, vararg upgrades: Upgrade) {
        for (tier in EnumUtils.FACTORY_TIERS) {
            val blockType = AdvancedFactoryBlockTypes.getAdvancedFactory(tier, type) ?: continue
            AdditionalUpgradeUtil.addSupported(blockType, *upgrades)
        }
    }

    private fun registerFactoryUpgrades(type: MoreMachineFactoryType, vararg upgrades: Upgrade) {
        for (tier in EnumUtils.FACTORY_TIERS) {
            val blockType = MoreMachineBlockTypes.getMoreMachineFactory(tier, type)
            AdditionalUpgradeUtil.addSupported(blockType, *upgrades)
        }
    }
}
