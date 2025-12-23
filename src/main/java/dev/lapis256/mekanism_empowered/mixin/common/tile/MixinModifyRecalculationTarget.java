package dev.lapis256.mekanism_empowered.mixin.common.tile;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import dev.lapis256.mekanism_empowered.mixin_impl.MixinImplModifyRecalculationTarget;
import mekanism.api.Upgrade;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.tile.factory.TileEntityFactory;
import mekanism.common.tile.machine.TileEntityChemicalInfuser;
import mekanism.common.tile.machine.TileEntityChemicalWasher;
import mekanism.common.tile.machine.TileEntityDigitalMiner;
import mekanism.common.tile.machine.TileEntityElectrolyticSeparator;
import mekanism.common.tile.machine.TileEntityFormulaicAssemblicator;
import mekanism.common.tile.machine.TileEntityIsotopicCentrifuge;
import mekanism.common.tile.machine.TileEntityPigmentMixer;
import mekanism.common.tile.machine.TileEntityRotaryCondensentrator;
import mekanism.common.tile.prefab.TileEntityProgressMachine;
import mekanism.common.tile.qio.TileEntityQIOFilterHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;


class MixinModifyRecalculationTarget {
    @Pseudo
    @Mixin(
        value = {
            TileEntityMekanism.class,
            TileEntityFactory.class,
            TileEntityChemicalInfuser.class,
            TileEntityChemicalWasher.class,
            TileEntityElectrolyticSeparator.class,
            TileEntityIsotopicCentrifuge.class,
            TileEntityPigmentMixer.class,
            TileEntityRotaryCondensentrator.class,
            TileEntityFormulaicAssemblicator.class,
            TileEntityQIOFilterHandler.class,
            TileEntityProgressMachine.class,
            TileEntityDigitalMiner.class

            // No current plans to increase Chemical usage.
//            TileEntityItemStackChemicalToItemStackFactory.class,
//            TileEntityChemicalDissolutionChamber.class,
//            TileEntityAdvancedElectricMachine.class,

        },
        targets = {
            "com.jerry.mekextras.common.tile.factory.TileEntityExtraFactory",
            "com.jerry.mekextras.common.tile.factory.TileEntityItemStackChemicalToItemStackExtraFactory",
            "com.jerry.mekextras.common.tile.machine.TileEntityAdvancedElectricPump",

            "com.jerry.mekaf.common.tile.factory.TileEntityAdvancedFactoryBase",
            "com.jerry.mekmm.common.tile.factory.TileEntityMoreMachineFactory",
            "com.jerry.meklm.common.tile.machine.TileEntityLargeChemicalInfuser",
            "com.jerry.meklm.common.tile.machine.TileEntityLargeElectrolyticSeparator",
            "com.jerry.meklm.common.tile.machine.TileEntityLargeRotaryCondensentrator",
            "com.jerry.meklm.common.tile.machine.TileEntityLargeSolarNeutronActivator"
        },
        remap = false
    )
    public static class Speed {
        @Definition(id = "upgrade", local = @Local(type = Upgrade.class))
        @Definition(id = "SPEED", field = "Lmekanism/api/Upgrade;SPEED:Lmekanism/api/Upgrade;")
        @Expression("upgrade == SPEED")
        @ModifyExpressionValue(method = "recalculateUpgrades", at = @At("MIXINEXTRAS:EXPRESSION"))
        private boolean mekanismEmpowered$modifyRecalculationTarget(boolean original, @Local(argsOnly = true) Upgrade upgrade) {
            return MixinImplModifyRecalculationTarget.modifySpeed(original, upgrade);
        }
    }

    @Mixin(value = TileEntityMekanism.class, remap = false)
    public static class Energy {
        @Definition(id = "upgrade", local = @Local(type = Upgrade.class))
        @Definition(id = "ENERGY", field = "Lmekanism/api/Upgrade;ENERGY:Lmekanism/api/Upgrade;")
        @Expression("upgrade == ENERGY")
        @ModifyExpressionValue(method = "recalculateUpgrades", at = @At("MIXINEXTRAS:EXPRESSION"))
        private boolean mekanismEmpowered$modifyRecalculationTarget(boolean original, @Local(argsOnly = true) Upgrade upgrade) {
            return MixinImplModifyRecalculationTarget.modifyEnergy(original, upgrade);
        }
    }
}
