package dev.lapis256.mekanism_empowered.mixin_impl

import dev.lapis256.mekanism_empowered.api.MekEmpUpgrade
import dev.lapis256.mekanism_empowered.core.extension.getInstalled
import dev.lapis256.mekanism_empowered.core.extension.isSpeedMaxed
import mekanism.common.tile.base.TileEntityMekanism
import kotlin.math.pow


fun TileEntityMekanism.prcRecalculateAdditionalUpgrades() {
    recalculateUpgrades(MekEmpUpgrade.EMPOWERED_SPEED)
}

fun TileEntityMekanism.modifyRecalculationBaselineMaxOperations(original: Double): Double {
    if (!isSpeedMaxed()) {
        return original
    }
    val speed = getInstalled(MekEmpUpgrade.EMPOWERED_SPEED) ?: return original
    return original + 2 * 2.0.pow(speed.toDouble())
}
