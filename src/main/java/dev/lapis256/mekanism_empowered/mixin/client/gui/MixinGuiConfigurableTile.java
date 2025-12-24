package dev.lapis256.mekanism_empowered.mixin.client.gui;

import dev.lapis256.mekanism_empowered.mixin_impl.MixinImplGuiConfigurableTile;
import mekanism.client.gui.GuiConfigurableTile;
import mekanism.client.gui.IGuiWrapper;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.tile.interfaces.ISideConfiguration;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(value = GuiConfigurableTile.class, remap = false)
public abstract class MixinGuiConfigurableTile<TILE extends TileEntityMekanism & ISideConfiguration> extends MixinGuiMekanismTile<TILE> {
    protected MixinGuiConfigurableTile(Component title) {
        super(title);
    }

    @Unique
    private MixinImplGuiConfigurableTile<?, ?> mekanismEmpowered$impl;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void mekanismEmpowered$init(CallbackInfo ci) {
        mekanismEmpowered$impl = new MixinImplGuiConfigurableTile<>((IGuiWrapper) this, tile, this::addRenderableWidget, this::removeWidget);
    }

    @Override
    protected void mekanismEmpowered$containerTick(CallbackInfo ci) {
        mekanismEmpowered$impl.containerTick();
    }

    @Inject(method = "addGuiElements", at = @At("TAIL"))
    private void mekanismEmpowered$addSideConfigTab(CallbackInfo ci) {
        mekanismEmpowered$impl.addGuiElements();
    }
}
