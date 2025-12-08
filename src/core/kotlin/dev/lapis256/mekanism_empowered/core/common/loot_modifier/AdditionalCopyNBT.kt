package dev.lapis256.mekanism_empowered.core.common.loot_modifier

import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.Dynamic
import com.mojang.serialization.JsonOps
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.lapis256.mekanism_empowered.core.extension.flatXmapCatching
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.storage.loot.LootContext
import net.minecraft.world.level.storage.loot.functions.CopyNbtFunction.CopyOperation
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition
import net.minecraft.world.level.storage.loot.providers.nbt.NbtProvider
import net.minecraftforge.common.loot.IGlobalLootModifier
import net.minecraftforge.common.loot.LootModifier
import net.minecraftforge.common.loot.LootModifierManager


class AdditionalCopyNBT(
    conditions: Array<LootItemCondition>,
    private val source: NbtProvider,
    private val operations: List<CopyOperation>
) : LootModifier(conditions) {

    companion object {
        val SOURCE_CODEC: Codec<NbtProvider> =
            Codec.PASSTHROUGH.flatXmapCatching({ d ->
                val json = IGlobalLootModifier.getJson<Any>(d)
                DataResult.success(LootModifierManager.GSON_INSTANCE.fromJson(json, NbtProvider::class.java))
            }, { s ->
                val element = LootModifierManager.GSON_INSTANCE.toJsonTree(s)
                DataResult.success(Dynamic(JsonOps.INSTANCE, element))
            })

        val OPERATIONS_CODEC: Codec<CopyOperation> =
            Codec.PASSTHROUGH.flatXmapCatching({ d ->
                val json = IGlobalLootModifier.getJson<Any>(d)
                DataResult.success(CopyOperation.fromJson(json.asJsonObject))
            }, { o ->
                val element = LootModifierManager.GSON_INSTANCE.toJsonTree(o.toJson())
                DataResult.success(Dynamic(JsonOps.INSTANCE, element))
            })

        val CODEC: Codec<AdditionalCopyNBT> = RecordCodecBuilder.create { instance ->
            codecStart(instance).and(
                instance.group(
                    SOURCE_CODEC.fieldOf("source").forGetter(AdditionalCopyNBT::source),
                    OPERATIONS_CODEC.listOf().fieldOf("ops").forGetter(AdditionalCopyNBT::operations),
                )
            ).apply(instance, ::AdditionalCopyNBT)
        }
    }

    override fun codec() = CODEC

    override fun doApply(generatedLoot: ObjectArrayList<ItemStack>, context: LootContext): ObjectArrayList<ItemStack> {
        val tag = this.source.get(context) ?: return generatedLoot
        generatedLoot.forEach { loot ->
            operations.forEach { it.apply(loot::getOrCreateTag, tag) }
        }
        return generatedLoot
    }
}
