package dev.lapis256.mekanism_empowered.mixin_impl

import dev.lapis256.mekanism_empowered.api.MekEmpUpgrade
import dev.lapis256.mekanism_empowered.core.extension.getInstalledOrDefault
import dev.lapis256.mekanism_empowered.extension.initialCapacity
import dev.lapis256.mekanism_empowered.extension.setCapacity
import mekanism.api.Action
import mekanism.api.Upgrade
import mekanism.api.chemical.BasicChemicalTank
import mekanism.api.chemical.IChemicalTank
import mekanism.api.chemical.IMekanismChemicalHandler
import mekanism.api.energy.IMekanismStrictEnergyHandler
import mekanism.api.fluid.IExtendedFluidTank
import mekanism.api.fluid.IMekanismFluidHandler
import mekanism.common.capabilities.fluid.BasicFluidTank
import mekanism.common.inventory.container.MekanismContainer
import mekanism.common.inventory.container.sync.SyncableInt
import mekanism.common.inventory.container.sync.SyncableLong
import mekanism.common.tile.interfaces.IUpgradeTile
import mekanism.common.util.EnumUtils


object MixinImplTileEntityMekanism {
    @JvmStatic
    fun <TILE> TILE.recalculateTankCapacity(upgrade: Upgrade) where TILE : IMekanismFluidHandler, TILE : IMekanismChemicalHandler, TILE : IMekanismStrictEnergyHandler, TILE : IUpgradeTile {
        when (upgrade) {
            MekEmpUpgrade.TANK_CAPACITY -> {
                EnumUtils.DIRECTIONS.flatMap(this::getFluidTanks).plus(this.getFluidTanks(null)).forEach {
                    if (it is BasicFluidTank) {
                        it.setCapacity(it.initialCapacity + 1024 * 8 * this.getInstalledOrDefault(MekEmpUpgrade.TANK_CAPACITY))
                        it.setStackSize(it.fluidAmount, Action.EXECUTE)
                    }
                }
                EnumUtils.DIRECTIONS.flatMap(this::getChemicalTanks).plus(this.getChemicalTanks(null)).forEach {
                    if (it is BasicChemicalTank) {
                        it.setCapacity(it.initialCapacity + 1024 * 8 * this.getInstalledOrDefault(MekEmpUpgrade.TANK_CAPACITY))
                        it.setStackSize(it.stored, Action.EXECUTE)
                    }
                }
            }
            else -> return
        }
    }

    @JvmStatic
    fun IUpgradeTile.addFluidContainerTrackers(container: MekanismContainer, tank: IExtendedFluidTank) {
        if (tank is BasicFluidTank && supportsUpgrade(MekEmpUpgrade.TANK_CAPACITY)) {
            container.track(SyncableInt.create(tank::getCapacity, tank::setCapacity))
        }
    }

    @JvmStatic
    fun IUpgradeTile.addChemicalContainerTrackers(container: MekanismContainer, tank: IChemicalTank) {
        if (tank is BasicChemicalTank && supportsUpgrade(MekEmpUpgrade.TANK_CAPACITY)) {
            container.track(SyncableLong.create(tank::getCapacity, tank::setCapacity))
        }
    }
}
