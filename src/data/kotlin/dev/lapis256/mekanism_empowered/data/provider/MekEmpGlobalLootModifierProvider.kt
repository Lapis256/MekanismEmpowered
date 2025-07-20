package dev.lapis256.mekanism_empowered.data.provider

import dev.lapis256.mekanism_empowered.api.MekEmpUpgrade
import dev.lapis256.mekanism_empowered.api.MekanismEmpoweredAPI
import dev.lapis256.mekanism_empowered.common.init.MekEmpDataComponents
import dev.lapis256.mekanism_empowered.core.common.loot_modifier.AdditionalCopyComponents
import mekanism.common.block.attribute.AttributeUpgradeSupport
import mekanism.common.block.prefab.BlockBase
import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.data.PackOutput
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.storage.loot.LootTable
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction
import net.minecraft.world.level.storage.loot.predicates.AnyOfCondition
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider
import net.neoforged.neoforge.common.loot.LootTableIdCondition
import java.util.concurrent.CompletableFuture


class MekEmpGlobalLootModifierProvider(output: PackOutput, registries: CompletableFuture<HolderLookup.Provider>) :
    GlobalLootModifierProvider(output, registries, MekanismEmpoweredAPI.MOD_ID) {

    override fun start() {
        this.add(
            "additional_copy_components", AdditionalCopyComponents(
                listOf(
                    AnyOfCondition.anyOf(
                        *BuiltInRegistries.BLOCK
                            .asSequence()
                            .filterIsInstance<BlockBase<*>>()
                            .filter {
                                val upgrades = it.type.get(AttributeUpgradeSupport::class.java) ?: return@filter false
                                return@filter upgrades.supportedUpgrades().contains(MekEmpUpgrade.AUTO_INSERTER)
                            }
                            .map(BlockBehaviour::getLootTable)
                            .map(ResourceKey<LootTable>::location)
                            .map(LootTableIdCondition::builder)
                            .toList()
                            .toTypedArray()
                    ).build()
                ), CopyComponentsFunction.Source.BLOCK_ENTITY, listOf(MekEmpDataComponents.INSERTER_CONFIG.get()), null
            )
        )
    }
}
