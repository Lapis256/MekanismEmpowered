package dev.lapis256.mekanism_empowered.mixin.client.gui;

import mekanism.client.gui.GuiMekanism;
import mekanism.client.gui.element.GuiElement;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(value = GuiMekanism.class)
public abstract class MixinGuiMekanism extends Screen {
    protected MixinGuiMekanism(Component title) {
        super(title);
    }

    @Shadow
    protected abstract <T extends GuiElement> T addRenderableWidget(T element);

    @Inject(method = "containerTick", at = @At("TAIL"))
    protected void mekanismEmpowered$containerTick(CallbackInfo ci) {
    }
}
