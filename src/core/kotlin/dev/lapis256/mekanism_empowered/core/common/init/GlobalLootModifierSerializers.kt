package dev.lapis256.mekanism_empowered.core.common.init

import com.mojang.serialization.Codec
import dev.lapis256.mekanism_empowered.core.api.MekanismEmpoweredCoreAPI
import dev.lapis256.mekanism_empowered.core.common.loot_modifier.AdditionalCopyNBT
import net.minecraftforge.common.loot.IGlobalLootModifier
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import thedarkcolour.kotlinforforge.forge.registerObject


object GlobalLootModifierSerializers {
    val REGISTRY = DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, MekanismEmpoweredCoreAPI.MOD_ID)!!

    val ADDITIONAL_COPY_NBT by register("additional_copy_nbt", AdditionalCopyNBT.CODEC)

    private fun <T : Codec<out IGlobalLootModifier>> register(name: String, codec: T) =
        REGISTRY.registerObject(name) { codec }
}
