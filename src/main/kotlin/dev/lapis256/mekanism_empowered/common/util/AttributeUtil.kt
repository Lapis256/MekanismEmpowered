package dev.lapis256.mekanism_empowered.common.util

import mekanism.api.tier.ITier
import mekanism.common.block.attribute.Attribute
import mekanism.common.block.attribute.AttributeTier
import net.minecraft.world.level.block.Block
import kotlin.reflect.KClass


object AttributeUtil {
    fun <A : Attribute> get(block: Block, type: KClass<A>): A? = Attribute.get(block, type.java)

    inline fun <reified T : ITier> getTier(block: Block): T? = get(block, AttributeTier::class)?.tier as? T
}
