package dev.lapis256.mekanism_empowered.mixin.client.gui;

import mekanism.client.gui.GuiMekanismTile;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.tile.interfaces.ISideConfiguration;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;


@Mixin(value = GuiMekanismTile.class)
public abstract class MixinGuiMekanismTile<TILE extends TileEntityMekanism & ISideConfiguration> extends MixinGuiMekanism {
    protected MixinGuiMekanismTile(Component title) {
        super(title);
    }

    @Shadow
    @Final
    protected TILE tile;
}
