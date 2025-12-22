package dev.lapis256.mekanism_empowered.core.common.init

import dev.lapis256.mekanism_empowered.core.api.MekanismEmpoweredCoreAPI
import dev.lapis256.mekanism_empowered.core.common.loot.HasTileComponentCondition
import net.minecraft.core.registries.Registries
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister
import thedarkcolour.kotlinforforge.neoforge.forge.getValue
import java.util.function.Supplier


object LootConditionTypes {
    val REGISTRY: DeferredRegister<LootItemConditionType> = DeferredRegister.create(Registries.LOOT_CONDITION_TYPE, MekanismEmpoweredCoreAPI.MOD_ID)

    val HAS_TILE_COMPONENT by register("has_tile_component", HasTileComponentCondition.conditionType)

    private fun register(name: String, type: LootItemConditionType): DeferredHolder<LootItemConditionType, LootItemConditionType> =
        REGISTRY.register(name, Supplier { type })
}
