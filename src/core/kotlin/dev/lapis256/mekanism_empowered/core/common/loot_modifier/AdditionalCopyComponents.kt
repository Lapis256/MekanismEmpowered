package dev.lapis256.mekanism_empowered.core.common.loot_modifier

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import net.minecraft.Util
import net.minecraft.core.component.DataComponentType
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.storage.loot.LootContext
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition
import net.neoforged.neoforge.common.loot.LootModifier
import java.util.*
import java.util.function.Predicate


class AdditionalCopyComponents(
    conditions: Array<LootItemCondition>,
    private val source: CopyComponentsFunction.Source,
    private val include: Optional<List<DataComponentType<*>>>,
    private val exclude: Optional<List<DataComponentType<*>>>
) : LootModifier(conditions) {

    constructor(
        conditions: List<LootItemCondition>,
        source: CopyComponentsFunction.Source,
        include: List<DataComponentType<*>>?,
        exclude: List<DataComponentType<*>>?
    ) : this(
        conditions.toTypedArray(), source, Optional.ofNullable(include), Optional.ofNullable(exclude)
    )

    private val predicate: Predicate<DataComponentType<*>> = Util.allOf(buildList(2) {
        include.ifPresent { add(it::contains) }
        exclude.ifPresent { add { c -> !it.contains(c) } }
    })

    companion object {
        val CODEC: MapCodec<AdditionalCopyComponents> = RecordCodecBuilder.mapCodec { instance ->
            codecStart(instance).and(
                instance.group(
                    CopyComponentsFunction.Source.CODEC.fieldOf("source").forGetter(AdditionalCopyComponents::source),
                    DataComponentType.CODEC.listOf().optionalFieldOf("include").forGetter(AdditionalCopyComponents::include),
                    DataComponentType.CODEC.listOf().optionalFieldOf("exclude").forGetter(AdditionalCopyComponents::exclude),
                )
            ).apply(instance, ::AdditionalCopyComponents)
        }
    }

    override fun codec(): MapCodec<AdditionalCopyComponents> = CODEC

    override fun doApply(generatedLoot: ObjectArrayList<ItemStack>, context: LootContext): ObjectArrayList<ItemStack> {
        val dataComponentMap = this.source.get(context)
        generatedLoot.forEach {
            it.applyComponents(dataComponentMap.filter(this.predicate))
        }
        return generatedLoot
    }
}
