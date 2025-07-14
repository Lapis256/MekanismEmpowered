package dev.lapis256.mekanism_empowered.mixin.common;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import dev.lapis256.mekanism_empowered.mixin_impl.MixinImplMekanismUtils;
import mekanism.common.tile.interfaces.IUpgradeTile;
import mekanism.common.util.MekanismUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(value = MekanismUtils.class)
public class MixinMekanismUtils {
    @ModifyReturnValue(method = "getTicksD", at = @At(value = "RETURN"))
    private static double mekanismEmpowered$modifyTicks(double original, @Local(argsOnly = true) IUpgradeTile tile) {
        return MixinImplMekanismUtils.modifyTicks(tile, original);
    }

    @ModifyArg(method = "getEnergyPerTick", at = @At(value = "INVOKE", target = "Lmekanism/api/math/MathUtils;ceilToLong(D)J"))
    private static double mekanismEmpowered$modifyEnergyPerTick(double original, @Local(argsOnly = true) IUpgradeTile tile) {
        return MixinImplMekanismUtils.modifyEnergyPerTick(tile, original);
    }

    @ModifyArg(method = "getMaxEnergy(Lmekanism/common/tile/interfaces/IUpgradeTile;J)J", at = @At(value = "INVOKE", target = "Lmekanism/api/math/MathUtils;clampToLong(D)J"))
    private static double mekanismEmpowered$modifyMaxEnergy(double original, @Local(argsOnly = true) IUpgradeTile tile) {
        return MixinImplMekanismUtils.modifyMaxEnergy(tile, original);
    }

    @Inject(method = "getMaxEnergy(Lmekanism/common/tile/interfaces/IUpgradeTile;J)J", at = @At(value = "HEAD"), cancellable = true)
    private static void mekanismEmpowered$modifyMaxEnergyHead(IUpgradeTile tile, long multiplier, CallbackInfoReturnable<Long> cir) {
        MixinImplMekanismUtils.modifyMaxEnergyWithMekExt(tile, cir);
    }
}
