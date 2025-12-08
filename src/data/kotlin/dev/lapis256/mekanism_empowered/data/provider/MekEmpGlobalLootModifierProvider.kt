package dev.lapis256.mekanism_empowered.data.provider

import dev.lapis256.mekanism_empowered.api.MekEmpSerializationConstants
import dev.lapis256.mekanism_empowered.api.MekanismEmpoweredAPI
import dev.lapis256.mekanism_empowered.core.common.loot.HasAdditionalTileComponentCondition
import dev.lapis256.mekanism_empowered.core.common.loot_modifier.AdditionalCopyNBT
import net.minecraft.data.PackOutput
import net.minecraft.world.level.storage.loot.functions.CopyNbtFunction.CopyOperation
import net.minecraft.world.level.storage.loot.functions.CopyNbtFunction.MergeStrategy
import net.minecraft.world.level.storage.loot.predicates.AnyOfCondition
import net.minecraft.world.level.storage.loot.providers.nbt.ContextNbtProvider
import net.minecraftforge.common.data.GlobalLootModifierProvider


class MekEmpGlobalLootModifierProvider(output: PackOutput) : GlobalLootModifierProvider(output, MekanismEmpoweredAPI.MOD_ID) {

    override fun start() {
        copyAdditionalComponentNBT()
    }

    private fun copyAdditionalComponentNBT() {
        val componentKeys = listOf(MekEmpSerializationConstants.COMPONENT_INSERTER_CONFIG)

        this.add(
            "additional_copy_nbt",
            AdditionalCopyNBT(
                arrayOf(
                    AnyOfCondition.anyOf(*componentKeys.map(HasAdditionalTileComponentCondition::builder).toTypedArray()).build()
                ),
                ContextNbtProvider.BLOCK_ENTITY,
                componentKeys.map { CopyOperation(it, "mekData.$it", MergeStrategy.REPLACE) }
            )
        )
    }
}
