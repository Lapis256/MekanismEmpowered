package dev.lapis256.mekanism_empowered.integration.mods

import com.jerry.mekaf.common.content.blocktype.AdvancedFactoryType
import com.jerry.mekextras.common.util.ExtraEnumUtils
import com.jerry.mekmm.common.content.blocktype.MoreMachineFactoryType
import dev.lapis256.mekanism_empowered.common.init.MekEmpUpgrades.ITEM_INPUT_MACHINE_UPGRADES
import dev.lapis256.mekanism_empowered.common.init.MekEmpUpgrades.ITEM_IN_OUT_MACHINE_UPGRADES
import dev.lapis256.mekanism_empowered.common.init.MekEmpUpgrades.ITEM_OUTPUT_MACHINE_UPGRADES
import dev.lapis256.mekanism_empowered.common.init.MekEmpUpgrades.MACHINE_UPGRADES
import dev.lapis256.mekanism_empowered.core.common.util.AdditionalUpgradeUtil
import dev.lapis256.mekanism_empowered.integration.ModIntegration
import fr.iglee42.evolvedmekanism.registries.EMFactoryType
import io.github.masyumero.emextras.common.content.blocktype.EMExtraFactoryType
import io.github.masyumero.emextras.common.integration.mekaf.registries.EMExtraAdvancedFactoryBlockTypes
import io.github.masyumero.emextras.common.integration.mekmm.registries.EMExtraMoreMachineBlockTypes
import io.github.masyumero.emextras.common.registry.EMExtraBlockTypes
import io.github.masyumero.emextras.common.util.EMExtraEnumUtils
import mekanism.api.Upgrade
import mekanism.common.content.blocktype.FactoryType

internal object EvoMekExt : ModIntegration{
    override val modId = "emextras"

    override fun initCommon() {
        addSupportedFactoryUpgrades(EMExtraFactoryType.ALLOYING, *ITEM_IN_OUT_MACHINE_UPGRADES)

        addSupportedFactoryUpgrades(EMExtraFactoryType.ENRICHING, *ITEM_IN_OUT_MACHINE_UPGRADES)
        addSupportedFactoryUpgrades(EMExtraFactoryType.CRUSHING, *ITEM_IN_OUT_MACHINE_UPGRADES)
        addSupportedFactoryUpgrades(EMExtraFactoryType.SMELTING, *ITEM_IN_OUT_MACHINE_UPGRADES)
        addSupportedFactoryUpgrades(EMExtraFactoryType.SAWING, *ITEM_IN_OUT_MACHINE_UPGRADES)
        addSupportedFactoryUpgrades(EMExtraFactoryType.COMPRESSING, *ITEM_IN_OUT_MACHINE_UPGRADES)
        addSupportedFactoryUpgrades(EMExtraFactoryType.COMBINING, *ITEM_IN_OUT_MACHINE_UPGRADES)
        addSupportedFactoryUpgrades(EMExtraFactoryType.INFUSING, *ITEM_IN_OUT_MACHINE_UPGRADES)
        addSupportedFactoryUpgrades(EMExtraFactoryType.PURIFYING, *ITEM_IN_OUT_MACHINE_UPGRADES)
        addSupportedFactoryUpgrades(EMExtraFactoryType.INJECTING, *ITEM_IN_OUT_MACHINE_UPGRADES)
        addSupportedFactoryUpgrades(EMFactoryType.ALLOYING, *ITEM_IN_OUT_MACHINE_UPGRADES)

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

    private fun addSupportedFactoryUpgrades(type: EMExtraFactoryType, vararg upgrades: Upgrade) {
        for (tier in EMExtraEnumUtils.EMEXTRA_FACTORY_TIERS) {
            val blockType = EMExtraBlockTypes.getEMExtraFactory(tier, type) ?: continue
            AdditionalUpgradeUtil.addSupported(blockType, *upgrades)
        }
    }

    private fun addSupportedFactoryUpgrades(type: FactoryType, vararg upgrades: Upgrade) {
        for (tier in ExtraEnumUtils.EXTRA_FACTORY_TIERS) {
            val blockType = EMExtraBlockTypes.getExtraFactory(tier, type) ?: continue
            AdditionalUpgradeUtil.addSupported(blockType, *upgrades)
        }
    }

    private fun addSupportedFactoryUpgrades(type: AdvancedFactoryType, vararg upgrades: Upgrade) {
        for (tier in EMExtraEnumUtils.EMEXTRA_FACTORY_TIERS) {
            val blockType = EMExtraAdvancedFactoryBlockTypes.getEMExtraAdvancedFactory(tier, type) ?: continue
            AdditionalUpgradeUtil.addSupported(blockType, *upgrades)
        }
    }

    private fun addSupportedFactoryUpgrades(type: MoreMachineFactoryType, vararg upgrades: Upgrade) {
        for (tier in EMExtraEnumUtils.EMEXTRA_FACTORY_TIERS) {
            val blockType = EMExtraMoreMachineBlockTypes.getEMExtraMoreMachineFactory(tier, type) ?: continue
            AdditionalUpgradeUtil.addSupported(blockType, *upgrades)
        }
    }
}
