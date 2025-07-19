package dev.lapis256.mekanism_empowered.common.init

import dev.lapis256.mekanism_empowered.api.MekEmpUpgrade
import dev.lapis256.mekanism_empowered.api.MekanismEmpoweredAPI
import dev.lapis256.mekanism_empowered.common.config.MekEmpTierConfig
import dev.lapis256.mekanism_empowered.common.item.ItemTieredGaugeDropper
import dev.lapis256.mekanism_empowered.core.common.upgrade.UpgradeItemRegistry
import dev.lapis256.mekanism_empowered.core.extension.toTitleCase
import mekanism.api.Upgrade
import mekanism.api.tier.BaseTier
import mekanism.common.attachments.containers.ContainerType
import mekanism.common.config.value.CachedIntValue
import mekanism.common.item.ItemUpgrade
import mekanism.common.registration.impl.ItemDeferredRegister
import mekanism.common.registration.impl.ItemRegistryObject
import net.minecraft.world.item.Item
import java.util.function.Supplier


@Suppress("UnstableApiUsage")
object MekEmpItems {
    val REGISTRY = ItemDeferredRegister(MekanismEmpoweredAPI.MOD_ID)
    val ENGLISH_NAME_MAP = mutableMapOf<ItemRegistryObject<*>, String>()

    val EMPOWERED_SPEED = registerUpgrade(MekEmpUpgrade.EMPOWERED_SPEED)
    val EMPOWERED_ENERGY = registerUpgrade(MekEmpUpgrade.EMPOWERED_ENERGY)
    val FAST_ITEM_EJECT = registerUpgrade(MekEmpUpgrade.FAST_ITEM_EJECT)
    val FAST_ITEM_INSERT = registerUpgrade(MekEmpUpgrade.FAST_ITEM_INSERT)
    val AUTO_INSERTER = registerUpgrade(MekEmpUpgrade.AUTO_INSERTER)
    val IO_CAPACITY = registerUpgrade(MekEmpUpgrade.IO_CAPACITY, "I/O Capacity")
    val TANK_CAPACITY = registerUpgrade(MekEmpUpgrade.TANK_CAPACITY)
    val ENERGY_CAPACITY = registerUpgrade(MekEmpUpgrade.ENERGY_CAPACITY)

    enum class GaugeDropperTier(val base: BaseTier, val rateSupplier: Supplier<CachedIntValue>, val capacitySupplier: Supplier<CachedIntValue>) {
        BASIC(BaseTier.BASIC, { MekEmpTierConfig.GaugeDropper.basicRate }, { MekEmpTierConfig.GaugeDropper.basicCapacity }),
        ADVANCED(BaseTier.ADVANCED, { MekEmpTierConfig.GaugeDropper.advancedRate }, { MekEmpTierConfig.GaugeDropper.advancedCapacity }),
        ELITE(BaseTier.ELITE, { MekEmpTierConfig.GaugeDropper.eliteRate }, { MekEmpTierConfig.GaugeDropper.eliteCapacity }),
        ULTIMATE(BaseTier.ULTIMATE, { MekEmpTierConfig.GaugeDropper.ultimateRate }, { MekEmpTierConfig.GaugeDropper.ultimateCapacity });
    }

    val BASIC_GAUGE_DROPPER = registerTiredGaugeDropper(GaugeDropperTier.BASIC)
    val ADVANCED_GAUGE_DROPPER = registerTiredGaugeDropper(GaugeDropperTier.ADVANCED)
    val ELITE_GAUGE_DROPPER = registerTiredGaugeDropper(GaugeDropperTier.ELITE)
    val ULTIMATE_GAUGE_DROPPER = registerTiredGaugeDropper(GaugeDropperTier.ULTIMATE)

    private fun registerTiredGaugeDropper(tier: GaugeDropperTier): ItemRegistryObject<ItemTieredGaugeDropper> {
        val path = "${tier.base.lowerName}_gauge_dropper"

        return registerItem(path, path.toTitleCase()) { properties -> ItemTieredGaugeDropper(tier, properties) }
            .addAttachedContainerCapabilities(
                ContainerType.CHEMICAL,
                { ItemTieredGaugeDropper.getChemicalTankCreator(tier) },
                MekEmpTierConfig
            )
            .addAttachedContainerCapabilities(
                ContainerType.FLUID,
                { ItemTieredGaugeDropper.getFluidTankCreator(tier) },
                MekEmpTierConfig
            )
    }

    private fun registerUpgrade(upgrade: Upgrade, englishName: String? = null): ItemRegistryObject<ItemUpgrade> =
        registerItem("upgrade_${upgrade.serializedName}", "${englishName ?: upgrade.serializedName.toTitleCase()} Upgrade") {
            ItemUpgrade(upgrade, it)
        }.also { UpgradeItemRegistry.register(upgrade, it) }

    private fun <ITEM : Item> registerItem(path: String, englishName: String, supplier: (Item.Properties) -> ITEM): ItemRegistryObject<ITEM> =
        REGISTRY.registerItem(path, supplier).also { ENGLISH_NAME_MAP[it] = englishName }
}
