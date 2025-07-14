package dev.lapis256.mekanism_empowered.mixin_impl

import com.jerry.mekextras.api.ExtraUpgrade
import dev.lapis256.mekanism_empowered.api.MekEmpUpgrade
import dev.lapis256.mekanism_empowered.integration.MekExt
import mekanism.common.tile.base.TileEntityMekanism
import mekanism.common.tile.component.TileComponentUpgrade


object MixinImplMachineEnergyContainer {
    @JvmStatic
    fun TileEntityMekanism.modifyUpdateMaxEnergyTarget(original: Boolean) =
        original || supportsUpgrade(MekEmpUpgrade.EMPOWERED_ENERGY) || (MekExt.loaded && supportsUpgrade(ExtraUpgrade.CREATIVE))

    @JvmStatic
    fun TileComponentUpgrade.modifyUpdateEnergyPerTickTarget(original: Boolean) =
        original || supports(MekEmpUpgrade.EMPOWERED_ENERGY) || supports(MekEmpUpgrade.EMPOWERED_SPEED)
}
