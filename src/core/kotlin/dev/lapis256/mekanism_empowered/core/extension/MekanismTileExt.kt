package dev.lapis256.mekanism_empowered.core.extension

import mekanism.api.Upgrade
import mekanism.common.tile.interfaces.IUpgradeTile
import mekanism.common.util.MekanismUtils


fun IUpgradeTile.getInstalled(upgrade: Upgrade) = this.component?.getUpgrades(upgrade)?.takeIf { it > 0 }

fun IUpgradeTile.getInstalledOrDefault(upgrade: Upgrade) = this.component?.getUpgrades(upgrade) ?: 0

fun IUpgradeTile.isUpgradeInstalled(upgrade: Upgrade) = this.component?.isUpgradeInstalled(upgrade) == true

fun IUpgradeTile.isSpeedMaxed() = getInstalledOrDefault(Upgrade.SPEED) >= Upgrade.SPEED.max

fun IUpgradeTile.isEnergyMaxed() = getInstalledOrDefault(Upgrade.ENERGY) >= Upgrade.ENERGY.max

fun IUpgradeTile.fractionUpgrades(upgrade: Upgrade) = MekanismUtils.fractionUpgrades(this, upgrade)
