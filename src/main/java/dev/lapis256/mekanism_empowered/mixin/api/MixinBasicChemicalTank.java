package dev.lapis256.mekanism_empowered.mixin.api;

import dev.lapis256.mekanism_empowered.api.ITankCapacitySetter;
import mekanism.api.chemical.BasicChemicalTank;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(value = BasicChemicalTank.class, remap = false)
public class MixinBasicChemicalTank implements ITankCapacitySetter.Chemical {
    @Shadow
    @Final
    @Mutable
    private long capacity;

    @Unique
    private long mekanismEmpowered$initialCapacity;

    @Override
    public void mekanism_empowered$setCapacity(Long capacity) {
        this.capacity = capacity;
    }

    @Inject(method = "<init>*", at = @At("RETURN"))
    private void mekanism_empowered$init(CallbackInfo ci) {
        this.mekanismEmpowered$initialCapacity = capacity;
    }

    @Override
    public Long mekanism_empowered$getInitialCapacity() {
        return this.mekanismEmpowered$initialCapacity;
    }
}
