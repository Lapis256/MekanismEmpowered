package dev.lapis256.mekanism_empowered.common.init

import com.jerry.mekanism_extras.common.tile.machine.TileEntityAdvancedElectricPump
import dev.lapis256.mekanism_empowered.api.MekEmpUpgrade
import dev.lapis256.mekanism_empowered.core.common.upgrade.UpgradeInfoHandler
import dev.lapis256.mekanism_empowered.core.common.util.AdditionalUpgradeUtil
import dev.lapis256.mekanism_empowered.core.extension.getInstalledOrDefault
import dev.lapis256.mekanism_empowered.integration.Integrations
import dev.lapis256.mekanism_empowered.integration.provider.FactoryUpgradeIntegration
import mekanism.api.Upgrade
import mekanism.common.content.blocktype.FactoryType
import mekanism.common.registries.MekanismBlockTypes
import mekanism.common.tile.interfaces.IUpgradeTile
import mekanism.common.tile.machine.TileEntityElectricPump
import mekanism.common.util.EnumUtils
import mekanism.common.util.UpgradeUtils
import net.minecraft.network.chat.Component
import net.minecraftforge.fml.ModList


object MekEmpUpgrades {
    val SPEED_AND_ENERGY_UPGRADES = arrayOf(MekEmpUpgrade.EMPOWERED_SPEED, MekEmpUpgrade.EMPOWERED_ENERGY)
    val MACHINE_UPGRADES = arrayOf(*SPEED_AND_ENERGY_UPGRADES, MekEmpUpgrade.IO_CAPACITY, MekEmpUpgrade.AUTO_INSERTER)
    val ITEM_INPUT_MACHINE_UPGRADES = arrayOf(*MACHINE_UPGRADES, MekEmpUpgrade.FAST_ITEM_INSERT)
    val ITEM_OUTPUT_MACHINE_UPGRADES = arrayOf(*MACHINE_UPGRADES, MekEmpUpgrade.FAST_ITEM_EJECT)
    val ITEM_IN_OUT_MACHINE_UPGRADES = arrayOf(*MACHINE_UPGRADES, MekEmpUpgrade.FAST_ITEM_INSERT, MekEmpUpgrade.FAST_ITEM_EJECT)

    fun registerUpgradeInfo() {
        val empoweredSpeedUpgradePumpInfo = { tile: IUpgradeTile ->
            listOf(Component.literal("Effect: +" + tile.getInstalledOrDefault(MekEmpUpgrade.EMPOWERED_SPEED) * 100 + "%"))
        }

        UpgradeInfoHandler.register(MekEmpUpgrade.EMPOWERED_SPEED, UpgradeUtils::getExpScaledInfo)
            .registerOverrideForTiles(TileEntityElectricPump::class) { it, _ -> empoweredSpeedUpgradePumpInfo.invoke(it) }
            .conditionallyRegisterOverride(ModList.get().isLoaded("mekanism_extras")) {
                registerOverrideForTiles(TileEntityAdvancedElectricPump::class) { it, _ -> empoweredSpeedUpgradePumpInfo.invoke(it) }
            }

        UpgradeInfoHandler.register(MekEmpUpgrade.EMPOWERED_ENERGY, UpgradeUtils::getMultScaledInfo)

        UpgradeInfoHandler.register(MekEmpUpgrade.IO_CAPACITY) { it, _ ->
            listOf(Component.literal("Effect: +" + it.getInstalledOrDefault(MekEmpUpgrade.IO_CAPACITY) * 3_200 + "%"))
        }
    }

    fun registerSupportedUpgrades() {
        val qioUpgrades = arrayOf(MekEmpUpgrade.EMPOWERED_SPEED, MekEmpUpgrade.IO_CAPACITY)

        AdditionalUpgradeUtil.addSupported(MekanismBlockTypes.ENRICHMENT_CHAMBER, *ITEM_IN_OUT_MACHINE_UPGRADES)
        AdditionalUpgradeUtil.addSupported(MekanismBlockTypes.CRUSHER, *ITEM_IN_OUT_MACHINE_UPGRADES)
        AdditionalUpgradeUtil.addSupported(MekanismBlockTypes.ENERGIZED_SMELTER, *ITEM_IN_OUT_MACHINE_UPGRADES)
        AdditionalUpgradeUtil.addSupported(MekanismBlockTypes.PRECISION_SAWMILL, *ITEM_IN_OUT_MACHINE_UPGRADES)
        AdditionalUpgradeUtil.addSupported(MekanismBlockTypes.OSMIUM_COMPRESSOR, *ITEM_IN_OUT_MACHINE_UPGRADES)
        AdditionalUpgradeUtil.addSupported(MekanismBlockTypes.COMBINER, *ITEM_IN_OUT_MACHINE_UPGRADES)
        AdditionalUpgradeUtil.addSupported(MekanismBlockTypes.METALLURGIC_INFUSER, *ITEM_IN_OUT_MACHINE_UPGRADES)
        AdditionalUpgradeUtil.addSupported(MekanismBlockTypes.PURIFICATION_CHAMBER, *ITEM_IN_OUT_MACHINE_UPGRADES)
        AdditionalUpgradeUtil.addSupported(MekanismBlockTypes.CHEMICAL_INJECTION_CHAMBER, *ITEM_IN_OUT_MACHINE_UPGRADES)

        addSupportedFactoryUpgrades(FactoryType.ENRICHING, *ITEM_IN_OUT_MACHINE_UPGRADES)
        addSupportedFactoryUpgrades(FactoryType.CRUSHING, *ITEM_IN_OUT_MACHINE_UPGRADES)
        addSupportedFactoryUpgrades(FactoryType.SMELTING, *ITEM_IN_OUT_MACHINE_UPGRADES)
        addSupportedFactoryUpgrades(FactoryType.SAWING, *ITEM_IN_OUT_MACHINE_UPGRADES)
        addSupportedFactoryUpgrades(FactoryType.COMPRESSING, *ITEM_IN_OUT_MACHINE_UPGRADES)
        addSupportedFactoryUpgrades(FactoryType.COMBINING, *ITEM_IN_OUT_MACHINE_UPGRADES)
        addSupportedFactoryUpgrades(FactoryType.INFUSING, *ITEM_IN_OUT_MACHINE_UPGRADES)
        addSupportedFactoryUpgrades(FactoryType.PURIFYING, *ITEM_IN_OUT_MACHINE_UPGRADES)
        addSupportedFactoryUpgrades(FactoryType.INJECTING, *ITEM_IN_OUT_MACHINE_UPGRADES)

        AdditionalUpgradeUtil.addSupported(MekanismBlockTypes.PRESSURIZED_REACTION_CHAMBER, *ITEM_IN_OUT_MACHINE_UPGRADES)
        AdditionalUpgradeUtil.addSupported(MekanismBlockTypes.FORMULAIC_ASSEMBLICATOR, *ITEM_IN_OUT_MACHINE_UPGRADES)
        AdditionalUpgradeUtil.addSupported(MekanismBlockTypes.NUTRITIONAL_LIQUIFIER, *ITEM_IN_OUT_MACHINE_UPGRADES)
        AdditionalUpgradeUtil.addSupported(MekanismBlockTypes.PAINTING_MACHINE, *ITEM_IN_OUT_MACHINE_UPGRADES)
        AdditionalUpgradeUtil.addSupported(MekanismBlockTypes.ANTIPROTONIC_NUCLEOSYNTHESIZER, *ITEM_IN_OUT_MACHINE_UPGRADES)

        AdditionalUpgradeUtil.addSupported(MekanismBlockTypes.CHEMICAL_OXIDIZER, *ITEM_INPUT_MACHINE_UPGRADES)
        AdditionalUpgradeUtil.addSupported(MekanismBlockTypes.CHEMICAL_DISSOLUTION_CHAMBER, *ITEM_INPUT_MACHINE_UPGRADES)
        AdditionalUpgradeUtil.addSupported(MekanismBlockTypes.PIGMENT_EXTRACTOR, *ITEM_INPUT_MACHINE_UPGRADES)

        AdditionalUpgradeUtil.addSupported(MekanismBlockTypes.CHEMICAL_CRYSTALLIZER, *ITEM_OUTPUT_MACHINE_UPGRADES)

        AdditionalUpgradeUtil.addSupported(MekanismBlockTypes.CHEMICAL_INFUSER, *MACHINE_UPGRADES)
        AdditionalUpgradeUtil.addSupported(MekanismBlockTypes.CHEMICAL_WASHER, *MACHINE_UPGRADES)
        AdditionalUpgradeUtil.addSupported(MekanismBlockTypes.ROTARY_CONDENSENTRATOR, *MACHINE_UPGRADES)
        AdditionalUpgradeUtil.addSupported(MekanismBlockTypes.ELECTROLYTIC_SEPARATOR, *MACHINE_UPGRADES)
        AdditionalUpgradeUtil.addSupported(MekanismBlockTypes.ISOTOPIC_CENTRIFUGE, *MACHINE_UPGRADES)
        AdditionalUpgradeUtil.addSupported(MekanismBlockTypes.PIGMENT_MIXER, *MACHINE_UPGRADES)

        AdditionalUpgradeUtil.addSupported(MekanismBlockTypes.QIO_IMPORTER, *qioUpgrades, MekEmpUpgrade.FAST_ITEM_INSERT)
        AdditionalUpgradeUtil.addSupported(MekanismBlockTypes.QIO_EXPORTER, *qioUpgrades, MekEmpUpgrade.FAST_ITEM_EJECT)

        AdditionalUpgradeUtil.addSupported(MekanismBlockTypes.DIGITAL_MINER, *SPEED_AND_ENERGY_UPGRADES)
        AdditionalUpgradeUtil.addSupported(MekanismBlockTypes.ELECTRIC_PUMP, *SPEED_AND_ENERGY_UPGRADES)
    }

    fun addSupportedFactoryUpgrades(type: FactoryType, vararg upgrades: Upgrade) {
        for (tier in EnumUtils.FACTORY_TIERS) {
            AdditionalUpgradeUtil.addSupported(MekanismBlockTypes.getFactory(tier, type), *upgrades)
        }

        Integrations.getProviders<FactoryUpgradeIntegration>().forEach {
            it.addSupportedFactoryUpgrades(type, *upgrades)
        }
    }
}
