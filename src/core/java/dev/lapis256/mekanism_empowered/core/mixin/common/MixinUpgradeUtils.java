package dev.lapis256.mekanism_empowered.core.mixin.common;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import dev.lapis256.mekanism_empowered.core.common.upgrade.UpgradeItemRegistry;
import dev.lapis256.mekanism_empowered.core.mixin_impl.MixinImplUpgradeUtilsKt;
import mekanism.api.Upgrade;
import mekanism.common.util.UpgradeUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;


@Mixin(value = UpgradeUtils.class, remap = false)
public abstract class MixinUpgradeUtils {
    @Inject(method = "getStack(Lmekanism/api/Upgrade;I)Lnet/minecraft/world/item/ItemStack;", at = @At("HEAD"), cancellable = true)
    private static void mekanismEmpoweredCore$getAdditionalUpgradeItem(Upgrade upgrade, int count, CallbackInfoReturnable<ItemStack> cir) {
        var item = UpgradeItemRegistry.getItem(upgrade);
        if (item != null) {
            cir.setReturnValue(new ItemStack(item.get(), count));
        }
    }

    @ModifyExpressionValue(method = "getInfo", at = @At(value = "INVOKE", target = "Lmekanism/api/Upgrade$IUpgradeInfoHandler;getInfo(Lmekanism/api/Upgrade;)Ljava/util/List;"))
    private static List<Component> mekanismEmpoweredCore$getAdditionalUpgradeInfo(List<Component> original, @Local(argsOnly = true, name = "arg1") Upgrade upgrade, @Local(name = "upgradeInfoHandler") Upgrade.IUpgradeInfoHandler infoHandler) {
        return MixinImplUpgradeUtilsKt.modifyAdditionalUpgradeInfo(infoHandler, upgrade, original);
    }
}
