package dev.lapis256.mekanism_empowered.mixin.common.tile;

import com.llamalad7.mixinextras.sugar.Local;
import dev.lapis256.mekanism_empowered.mixin_impl.MixinImplTileEntityMekanism;
import mekanism.api.Upgrade;
import mekanism.api.chemical.IChemicalTank;
import mekanism.api.chemical.IMekanismChemicalHandler;
import mekanism.api.energy.IMekanismStrictEnergyHandler;
import mekanism.api.fluid.IExtendedFluidTank;
import mekanism.api.fluid.IMekanismFluidHandler;
import mekanism.common.inventory.container.MekanismContainer;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.tile.interfaces.IUpgradeTile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(value = TileEntityMekanism.class, remap = false)
public abstract class MixinTileEntityMekanism implements IMekanismFluidHandler, IMekanismChemicalHandler, IMekanismStrictEnergyHandler, IUpgradeTile {
    @Inject(method = "recalculateUpgrades", at = @At("HEAD"))
    private void mekanismEmpowered$modifyRecalculationTarget(Upgrade upgrade, CallbackInfo ci) {
        MixinImplTileEntityMekanism.recalculateTankCapacity(this, upgrade);
    }

    @Inject(method = "addContainerTrackers", at = @At(value = "INVOKE", target = "Lmekanism/common/inventory/container/sync/SyncableFluidStack;create(Lmekanism/api/fluid/IExtendedFluidTank;Z)Lmekanism/common/inventory/container/sync/SyncableFluidStack;"))
    private void mekanismEmpowered$modifyRecalculationTarget(MekanismContainer container, CallbackInfo ci, @Local IExtendedFluidTank tank) {
        MixinImplTileEntityMekanism.addFluidContainerTrackers(this, container, tank);
    }

    @Inject(method = "addContainerTrackers", at = @At(value = "INVOKE", target = "Lmekanism/common/inventory/container/sync/chemical/SyncableChemicalStack;create(Lmekanism/api/chemical/IChemicalTank;Z)Lmekanism/common/inventory/container/sync/chemical/SyncableChemicalStack;"))
    private void mekanismEmpowered$modifyRecalculationTarget(MekanismContainer container, CallbackInfo ci, @Local IChemicalTank tank) {
        MixinImplTileEntityMekanism.addChemicalContainerTrackers(this, container, tank);
    }
}
