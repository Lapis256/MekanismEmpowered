package dev.lapis256.mekanism_empowered.mixin.api;

import dev.lapis256.mekanism_empowered.api.ITankCapacitySetter;
import mekanism.common.capabilities.fluid.BasicFluidTank;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(value = BasicFluidTank.class, remap = false)
public class MixinBasicFluidTank implements ITankCapacitySetter.Fluid {
    @Shadow
    @Final
    @Mutable
    private int capacity;

    @Unique
    private int mekanismEmpowered$initialCapacity;

    @Override
    public void mekanism_empowered$setCapacity(@NotNull Integer capacity) {
        this.capacity = capacity;
    }

    @Inject(method = "<init>*", at = @At("RETURN"))
    private void mekanism_empowered$init(CallbackInfo ci) {
        this.mekanismEmpowered$initialCapacity = capacity;
    }

    @Override
    public @NotNull Integer mekanism_empowered$getInitialCapacity() {
        return mekanismEmpowered$initialCapacity;
    }
}
