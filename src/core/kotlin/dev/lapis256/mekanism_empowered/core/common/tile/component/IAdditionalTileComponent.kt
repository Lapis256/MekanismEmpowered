package dev.lapis256.mekanism_empowered.core.common.tile.component

import mekanism.common.tile.base.TileEntityMekanism
import net.minecraft.nbt.CompoundTag


interface IAdditionalTileComponent {
    fun loadComponentNBT(tile: TileEntityMekanism, dataMap: CompoundTag)

    val componentKey: String
}
