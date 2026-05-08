package dev.lapis256.mekanism_empowered.core.mixin.common;

import dev.lapis256.mekanism_empowered.core.mixin_impl.ducks.UpgradeSupportOverrideTracker;
import mekanism.common.content.blocktype.BlockType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;


@Mixin(value = BlockType.class, remap = false)
public class MixinBlockType implements UpgradeSupportOverrideTracker {
    @Unique
    private boolean mekanismEmpowered$hasUpgradeSupportOverride = false;

    @Override
    public boolean mekanismEmpowered$hasUpgradeSupportOverride() {
        return mekanismEmpowered$hasUpgradeSupportOverride;
    }

    @Override
    public void mekanismEmpowered$setUpgradeSupportOverride(boolean overridden) {
        mekanismEmpowered$hasUpgradeSupportOverride = overridden;
    }
}
