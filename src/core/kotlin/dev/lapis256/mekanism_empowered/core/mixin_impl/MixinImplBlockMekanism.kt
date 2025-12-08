package dev.lapis256.mekanism_empowered.core.mixin_impl

import dev.lapis256.mekanism_empowered.core.common.tile.component.IAdditionalTileComponent
import mekanism.common.tile.base.TileEntityMekanism
import net.minecraft.nbt.CompoundTag


fun setPlacedByRestoreNBT(tile: TileEntityMekanism, dataMap: CompoundTag) {
    tile
        .components
        .filterIsInstance<IAdditionalTileComponent>()
        .forEach { component -> component.loadComponentNBT(tile, dataMap) }
}
