package dev.lapis256.mekanism_empowered.mixin_impl

import com.jerry.mekextras.api.ExtraUpgrade
import dev.lapis256.mekanism_empowered.api.MekEmpUpgrade
import dev.lapis256.mekanism_empowered.common.config.MekEmpGeneralConfig
import dev.lapis256.mekanism_empowered.core.extension.fractionUpgrades
import dev.lapis256.mekanism_empowered.core.extension.getInstalledOrDefault
import dev.lapis256.mekanism_empowered.core.extension.isEnergyMaxed
import dev.lapis256.mekanism_empowered.core.extension.isSpeedMaxed
import dev.lapis256.mekanism_empowered.core.extension.isUpgradeInstalled
import dev.lapis256.mekanism_empowered.integration.MekExt
import mekanism.common.tile.interfaces.IUpgradeTile
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow


object MixinImplMekanismUtils {
    private val multiplier: Double
        get() = MekEmpGeneralConfig.maxUpgradeMultiplier.toDouble()

    @JvmStatic
    fun IUpgradeTile.modifyTicks(original: Double): Double {
        if (!isSpeedMaxed()) {
            return original
        }
        return original * multiplier.pow(-fractionUpgrades(MekEmpUpgrade.EMPOWERED_SPEED))
    }

    @JvmStatic
    fun IUpgradeTile.modifyEnergyPerTick(original: Double): Double {
        val speed = getInstalledOrDefault(MekEmpUpgrade.EMPOWERED_SPEED)
        if (!isSpeedMaxed() || speed <= 0) {
            return original
        }
        val energy = getInstalledOrDefault(MekEmpUpgrade.EMPOWERED_ENERGY)
        return  original * multiplier.pow((2 * speed - min(energy, max(speed, 8))) / 8.toDouble())
    }

    @JvmStatic
    fun IUpgradeTile.modifyMaxEnergy(original: Double): Double {
        if (!isEnergyMaxed()) {
            return original
        }
        return original * multiplier.pow(2 * fractionUpgrades(MekEmpUpgrade.EMPOWERED_ENERGY))
    }

    @JvmStatic
    fun IUpgradeTile.modifyMaxEnergyWithMekExt(cir: CallbackInfoReturnable<Long>) {
        if (MekExt.loaded && isUpgradeInstalled(ExtraUpgrade.CREATIVE)) {
            cir.returnValue = Long.MAX_VALUE
        }
    }
}
