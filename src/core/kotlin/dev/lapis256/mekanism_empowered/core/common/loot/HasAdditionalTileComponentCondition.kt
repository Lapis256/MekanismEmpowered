package dev.lapis256.mekanism_empowered.core.common.loot

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import dev.lapis256.mekanism_empowered.core.common.tile.component.IAdditionalTileComponent
import mekanism.common.tile.base.TileEntityMekanism
import net.minecraft.util.GsonHelper
import net.minecraft.world.level.storage.loot.LootContext
import net.minecraft.world.level.storage.loot.parameters.LootContextParams
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType


class HasAdditionalTileComponentCondition(val componentKey: String) : LootItemCondition {
    override fun getType(): LootItemConditionType = conditionType

    override fun test(ctx: LootContext) = ctx.getParamOrNull(LootContextParams.BLOCK_ENTITY)?.let { entity ->
        val tile = entity as? TileEntityMekanism ?: return false
        return tile.components.asSequence().filterIsInstance<IAdditionalTileComponent>().filter { it.componentKey == componentKey }.any()
    } ?: false

    companion object {
        val conditionType = LootItemConditionType(Serializer())

        fun builder(targetComponentKey: String) = Builder(targetComponentKey)
    }

    class Builder(private val targetComponentKey: String) : LootItemCondition.Builder {
        override fun build() = HasAdditionalTileComponentCondition(this.targetComponentKey)
    }

    private class Serializer : net.minecraft.world.level.storage.loot.Serializer<HasAdditionalTileComponentCondition> {
        companion object {
            const val KEY = "component_key"
        }

        override fun serialize(json: JsonObject, instance: HasAdditionalTileComponentCondition, ctx: JsonSerializationContext) {
            json.addProperty(KEY, instance.componentKey)
        }

        override fun deserialize(json: JsonObject, ctx: JsonDeserializationContext): HasAdditionalTileComponentCondition {
            return HasAdditionalTileComponentCondition(GsonHelper.getAsString(json, KEY))
        }
    }
}
