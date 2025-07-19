package dev.lapis256.mekanism_empowered.integration

import com.jerry.mekaf.common.content.blocktype.AdvancedFactoryType
import com.jerry.mekaf.common.registries.AFBlockTypes
import com.jerry.mekmm.common.content.blocktype.MMFactoryType
import com.jerry.mekmm.common.registries.MMBlockTypes
import dev.lapis256.mekanism_empowered.common.init.MekEmpUpgrades.ITEM_INPUT_MACHINE_UPGRADES
import dev.lapis256.mekanism_empowered.common.init.MekEmpUpgrades.ITEM_IN_OUT_MACHINE_UPGRADES
import dev.lapis256.mekanism_empowered.common.init.MekEmpUpgrades.ITEM_OUTPUT_MACHINE_UPGRADES
import dev.lapis256.mekanism_empowered.common.init.MekEmpUpgrades.MACHINE_UPGRADES
import dev.lapis256.mekanism_empowered.core.common.util.AdditionalUpgradeUtil
import mekanism.api.Upgrade
import mekanism.common.tier.FactoryTier


object MekMM : IIntegration {
    override val modId = "mekmm"

    override fun initCommon() {
        AdditionalUpgradeUtil.addSupported(MMBlockTypes.RECYCLER, *ITEM_IN_OUT_MACHINE_UPGRADES)
        AdditionalUpgradeUtil.addSupported(MMBlockTypes.PLANTING_STATION, *ITEM_IN_OUT_MACHINE_UPGRADES)
        AdditionalUpgradeUtil.addSupported(MMBlockTypes.CNC_STAMPER, *ITEM_IN_OUT_MACHINE_UPGRADES)
        AdditionalUpgradeUtil.addSupported(MMBlockTypes.CNC_LATHE, *ITEM_IN_OUT_MACHINE_UPGRADES)
        AdditionalUpgradeUtil.addSupported(MMBlockTypes.CNC_ROLLING_MILL, *ITEM_IN_OUT_MACHINE_UPGRADES)
        AdditionalUpgradeUtil.addSupported(MMBlockTypes.REPLICATOR, *ITEM_IN_OUT_MACHINE_UPGRADES)

        registerMoreFactoryUpgrades(MMFactoryType.RECYCLING, *ITEM_IN_OUT_MACHINE_UPGRADES)
        registerMoreFactoryUpgrades(MMFactoryType.PLANTING_STATION, *ITEM_IN_OUT_MACHINE_UPGRADES)
        registerMoreFactoryUpgrades(MMFactoryType.CNC_STAMPING, *ITEM_IN_OUT_MACHINE_UPGRADES)
        registerMoreFactoryUpgrades(MMFactoryType.CNC_LATHING, *ITEM_IN_OUT_MACHINE_UPGRADES)
        registerMoreFactoryUpgrades(MMFactoryType.CNC_ROLLING_MILL, *ITEM_IN_OUT_MACHINE_UPGRADES)
        registerMoreFactoryUpgrades(MMFactoryType.REPLICATING, *ITEM_IN_OUT_MACHINE_UPGRADES)

        registerAdvancedFactoryUpgrades(AdvancedFactoryType.OXIDIZING, *ITEM_INPUT_MACHINE_UPGRADES)
        registerAdvancedFactoryUpgrades(AdvancedFactoryType.CHEMICAL_INFUSING, *MACHINE_UPGRADES)
        registerAdvancedFactoryUpgrades(AdvancedFactoryType.DISSOLVING, *ITEM_INPUT_MACHINE_UPGRADES)
        registerAdvancedFactoryUpgrades(AdvancedFactoryType.WASHING, *MACHINE_UPGRADES)
        registerAdvancedFactoryUpgrades(AdvancedFactoryType.CRYSTALLIZING, *ITEM_OUTPUT_MACHINE_UPGRADES)
        registerAdvancedFactoryUpgrades(AdvancedFactoryType.PRESSURISED_REACTING, *ITEM_IN_OUT_MACHINE_UPGRADES)
        registerAdvancedFactoryUpgrades(AdvancedFactoryType.CENTRIFUGING, *MACHINE_UPGRADES)
    }

    private fun registerAdvancedFactoryUpgrades(type: AdvancedFactoryType, vararg upgrades: Upgrade) {
        AdditionalUpgradeUtil.addSupported(AFBlockTypes.getAdvancedFactory(FactoryTier.BASIC, type), *upgrades)
        AdditionalUpgradeUtil.addSupported(AFBlockTypes.getAdvancedFactory(FactoryTier.ADVANCED, type), *upgrades)
        AdditionalUpgradeUtil.addSupported(AFBlockTypes.getAdvancedFactory(FactoryTier.ELITE, type), *upgrades)
        AdditionalUpgradeUtil.addSupported(AFBlockTypes.getAdvancedFactory(FactoryTier.ULTIMATE, type), *upgrades)
    }

    private fun registerMoreFactoryUpgrades(type: MMFactoryType, vararg upgrades: Upgrade) {
        AdditionalUpgradeUtil.addSupported(MMBlockTypes.getMMFactory(FactoryTier.BASIC, type), *upgrades)
        AdditionalUpgradeUtil.addSupported(MMBlockTypes.getMMFactory(FactoryTier.ADVANCED, type), *upgrades)
        AdditionalUpgradeUtil.addSupported(MMBlockTypes.getMMFactory(FactoryTier.ELITE, type), *upgrades)
        AdditionalUpgradeUtil.addSupported(MMBlockTypes.getMMFactory(FactoryTier.ULTIMATE, type), *upgrades)
    }

    override fun initClient() {
    }
}
