package dev.lapis256.mekanism_empowered.extension

import dev.lapis256.mekanism_empowered.mixin.common.tile.AccessorTileEntityMekanism
import mekanism.common.tile.base.TileEntityMekanism


private val TileEntityMekanism.accessor get() = this as? AccessorTileEntityMekanism

val TileEntityMekanism.itemHandlerManager get() =
    accessor?.itemHandlerManager

val TileEntityMekanism.chemicalHandlerManager get() =
    accessor?.chemicalHandlerManager

val TileEntityMekanism.fluidHandlerManager get() =
    accessor?.fluidHandlerManager
