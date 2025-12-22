package dev.lapis256.mekanism_empowered.core.common.loot

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import mekanism.common.tile.base.TileEntityMekanism
import net.minecraft.world.level.storage.loot.LootContext
import net.minecraft.world.level.storage.loot.parameters.LootContextParams
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType


class HasTileComponentCondition(val componentKey: String) : LootItemCondition {
    override fun getType(): LootItemConditionType = conditionType

    override fun test(ctx: LootContext) = ctx.getParamOrNull(LootContextParams.BLOCK_ENTITY)?.let { entity ->
        val tile = entity as? TileEntityMekanism ?: return false
        return tile.components.any { it.componentKey == componentKey }
    } ?: false

    companion object {
        val CODEC: MapCodec<HasTileComponentCondition> = RecordCodecBuilder.mapCodec {
            it.group(
                Codec.STRING.fieldOf("component_key").forGetter(HasTileComponentCondition::componentKey)
            ).apply(it, ::HasTileComponentCondition)
        }

        val conditionType = LootItemConditionType(CODEC)

        fun builder(targetComponentKey: String) = Builder(targetComponentKey)
    }

    class Builder(private val targetComponentKey: String) : LootItemCondition.Builder {
        override fun build() = HasTileComponentCondition(this.targetComponentKey)
    }
}
