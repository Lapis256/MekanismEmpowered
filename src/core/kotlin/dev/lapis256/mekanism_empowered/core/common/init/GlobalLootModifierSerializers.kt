package dev.lapis256.mekanism_empowered.core.common.init

import java.util.function.Supplier
import com.mojang.serialization.MapCodec
import dev.lapis256.mekanism_empowered.core.api.MekanismEmpoweredCoreAPI
import dev.lapis256.mekanism_empowered.core.common.loot_modifier.AdditionalCopyComponents
import net.neoforged.neoforge.common.loot.IGlobalLootModifier
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister
import net.neoforged.neoforge.registries.NeoForgeRegistries
import thedarkcolour.kotlinforforge.neoforge.forge.getValue


object GlobalLootModifierSerializers {
    val REGISTRY: DeferredRegister<MapCodec<out IGlobalLootModifier>> =
        DeferredRegister.create(NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, MekanismEmpoweredCoreAPI.MOD_ID)

    val ADDITIONAL_COPY_COMPONENTS: MapCodec<AdditionalCopyComponents> by register("additional_copy_components", AdditionalCopyComponents.CODEC)

    private fun <T : MapCodec<out IGlobalLootModifier>> register(name: String, codec: T): DeferredHolder<MapCodec<out IGlobalLootModifier>, T> {
        return REGISTRY.register(name, Supplier { codec })
    }
}
