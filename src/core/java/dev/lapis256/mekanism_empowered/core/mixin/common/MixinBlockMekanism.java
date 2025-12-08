package dev.lapis256.mekanism_empowered.core.mixin.common;

import com.llamalad7.mixinextras.sugar.Local;
import dev.lapis256.mekanism_empowered.core.mixin_impl.MixinImplBlockMekanismKt;
import mekanism.common.block.BlockMekanism;
import mekanism.common.tile.base.TileEntityMekanism;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(BlockMekanism.class)
public class MixinBlockMekanism {
    @Inject(method = "setPlacedBy(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;)V", at = @At(value = "TAIL"))
    private void mekanismEmpowered$setPlacedBy(CallbackInfo ci, @Local TileEntityMekanism tile, @Local CompoundTag dataMap) {
        MixinImplBlockMekanismKt.setPlacedByRestoreNBT(tile, dataMap);
    }
}
