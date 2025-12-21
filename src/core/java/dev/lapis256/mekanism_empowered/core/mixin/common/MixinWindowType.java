package dev.lapis256.mekanism_empowered.core.mixin.common;

import dev.lapis256.mekanism_empowered.core.mixin_impl.MixinImplWindowType;
import mekanism.common.inventory.container.SelectedWindowData;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(value = SelectedWindowData.WindowType.class, remap = false)
public class MixinWindowType {
    @Shadow
    @Final
    @Mutable
    @Dynamic
    private static SelectedWindowData.WindowType[] $VALUES;

    @Invoker(value = "<init>")
    public static SelectedWindowData.WindowType mekanismEmpoweredCore$createWindowTypeInstance(String internalName, int internalId, @Nullable String saveName, byte maxData) {
        throw new AssertionError("Mixin failed to apply, this should never be called");
    }

    @Inject(method = "<clinit>", at = @At(value = "TAIL"))
    private static void mekanismEmpoweredCore$initAdditionalWindowTypes(CallbackInfo ci) {
        MixinImplWindowType mekanismEmpoweredCore$impl = new MixinImplWindowType(MixinWindowType::mekanismEmpoweredCore$createWindowTypeInstance);
        $VALUES = mekanismEmpoweredCore$impl.initAdditionalWindowTypes($VALUES);
    }
}
