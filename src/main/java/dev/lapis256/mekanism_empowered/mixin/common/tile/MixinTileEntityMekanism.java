package dev.lapis256.mekanism_empowered.mixin.common.tile;

import dev.lapis256.mekanism_empowered.mixin_impl.MixinImplTileEntityMekanism;
import mekanism.common.tile.base.TileEntityMekanism;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(value = TileEntityMekanism.class, remap = false)
public class MixinTileEntityMekanism {
    @Shadow
    @Final
    private Holder<Block> blockProvider;

    /**
     * Fallback for block types not already patched through AdditionalUpgradeUtil.addSupported.
     */
    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lmekanism/common/tile/base/TileEntityMekanism;supportsUpgrades()Z"))
    private void mekanismEmpowered$init(CallbackInfo ci) {
        MixinImplTileEntityMekanism.applyFallbackSupportedUpgrades((TileEntityMekanism) (Object) this, blockProvider.value());
    }
}
