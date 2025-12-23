package dev.lapis256.mekanism_empowered.core.mixin.common;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import dev.lapis256.mekanism_empowered.core.mixin_impl.MixinImplUpgrade;
import mekanism.api.Upgrade;
import mekanism.api.text.EnumColor;
import mekanism.api.text.ILangEntry;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.Set;


@Mixin(value = Upgrade.class, remap = false)
public class MixinUpgrade {
    @Shadow
    @Final
    @Mutable
    private static Upgrade[] $VALUES;

    @Unique
    private static MixinImplUpgrade mekanismEmpoweredCore$impl;

    @Invoker(value = "<init>")
    public static Upgrade mekanismEmpoweredCore$createUpgradeInstance(String internalName, int internalId, String name, ILangEntry langKey, ILangEntry descLangKey, int maxStack, EnumColor color) {
        throw new AssertionError("Mixin failed to apply, this should never be called");
    }

    @Inject(method = "<clinit>", at = @At(value = "INVOKE", target = "Lmekanism/api/Upgrade;values()[Lmekanism/api/Upgrade;", ordinal = 0))
    private static void mekanismEmpoweredCore$initAdditionalUpgrades(CallbackInfo ci) {
        mekanismEmpoweredCore$impl = new MixinImplUpgrade(MixinUpgrade::mekanismEmpoweredCore$createUpgradeInstance);
        $VALUES = mekanismEmpoweredCore$impl.initAdditionalUpgrades($VALUES);
    }

    @ModifyVariable(method = "buildMap", at = @At(value = "STORE", ordinal = 0), name = "upgrades")
    private static Map<Upgrade, Integer> mekanismEmpoweredCore$buildAdditionalMap(@Nullable Map<Upgrade, Integer> upgrades, @Nullable CompoundTag nbtTags) {
        return mekanismEmpoweredCore$impl.buildAdditionalMap(upgrades, nbtTags);
    }

    @ModifyExpressionValue(method = "saveMap", at = @At(value = "INVOKE", target = "Ljava/util/Map;entrySet()Ljava/util/Set;"))
    private static Set<Map.Entry<Upgrade, Integer>> mekanismEmpoweredCore$filterUpgrades(Set<Map.Entry<Upgrade, Integer>> upgrades, @Local(argsOnly = true) CompoundTag nbtTags) {
        return mekanismEmpoweredCore$impl.saveAdditionalMap(upgrades, nbtTags);
    }
}
