package dev.lapis256.mekanism_empowered.mixin.common.tile;

import mekanism.common.capabilities.resolver.manager.ChemicalHandlerManager;
import mekanism.common.capabilities.resolver.manager.FluidHandlerManager;
import mekanism.common.capabilities.resolver.manager.ItemHandlerManager;
import mekanism.common.tile.base.TileEntityMekanism;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;


@Mixin(value = TileEntityMekanism.class, remap = false)
public interface AccessorTileEntityMekanism {
    @Accessor
    @Nullable
    ItemHandlerManager getItemHandlerManager();

    @Accessor
    @Nullable
    ChemicalHandlerManager getChemicalHandlerManager();

    @Accessor
    @Nullable
    FluidHandlerManager getFluidHandlerManager();
}
