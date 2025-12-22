package dev.lapis256.mekanism_empowered.data.provider

import dev.lapis256.mekanism_empowered.api.MekEmpSerializationConstants
import dev.lapis256.mekanism_empowered.api.MekanismEmpoweredAPI
import dev.lapis256.mekanism_empowered.common.init.MekEmpDataComponents
import dev.lapis256.mekanism_empowered.core.common.loot.HasTileComponentCondition
import dev.lapis256.mekanism_empowered.core.common.loot_modifier.AdditionalCopyComponents
import net.minecraft.core.HolderLookup
import net.minecraft.data.PackOutput
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction
import net.minecraft.world.level.storage.loot.predicates.AnyOfCondition
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider
import java.util.concurrent.CompletableFuture


class MekEmpGlobalLootModifierProvider(output: PackOutput, registries: CompletableFuture<HolderLookup.Provider>) :
    GlobalLootModifierProvider(output, registries, MekanismEmpoweredAPI.MOD_ID) {

    override fun start() {
        copyAdditionalComponents()
    }

    private fun copyAdditionalComponents() {
        val componentKeys = listOf(MekEmpSerializationConstants.COMPONENT_INSERTER_CONFIG)

        this.add(
            "additional_copy_components",
            AdditionalCopyComponents(
                conditions = listOf(
                    AnyOfCondition.anyOf(*componentKeys.map(HasTileComponentCondition::builder).toTypedArray()).build()
                ),
                source = CopyComponentsFunction.Source.BLOCK_ENTITY,
                include = listOf(MekEmpDataComponents.INSERTER_CONFIG.get()),
                exclude = null
            )
        )
    }
}
