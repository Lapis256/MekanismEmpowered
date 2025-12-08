package dev.lapis256.mekanism_empowered.core.common.init

import dev.lapis256.mekanism_empowered.core.api.MekanismEmpoweredCoreAPI
import dev.lapis256.mekanism_empowered.core.common.loot.HasAdditionalTileComponentCondition
import net.minecraft.core.registries.Registries
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType
import net.minecraftforge.registries.DeferredRegister
import thedarkcolour.kotlinforforge.forge.registerObject


object LootConditionTypes {
    val REGISTRY = DeferredRegister.create(Registries.LOOT_CONDITION_TYPE, MekanismEmpoweredCoreAPI.MOD_ID)!!

    val HAS_ADDITIONAL_TILE_COMPONENT by register("has_additional_tile_component", HasAdditionalTileComponentCondition.conditionType)

    private fun register(name: String, type: LootItemConditionType) = REGISTRY.registerObject(name) { type }
}
