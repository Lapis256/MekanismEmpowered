package dev.lapis256.mekanism_empowered.mixin.common.tile.machine;


import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dev.lapis256.mekanism_empowered.mixin_impl.MixinImplTileMachineKt;
import mekanism.api.providers.IBlockProvider;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.tile.machine.TileEntityChemicalInfuser;
import mekanism.common.tile.machine.TileEntityChemicalWasher;
import mekanism.common.tile.machine.TileEntityElectrolyticSeparator;
import mekanism.common.tile.machine.TileEntityIsotopicCentrifuge;
import mekanism.common.tile.machine.TileEntityPigmentMixer;
import mekanism.common.tile.machine.TileEntityRotaryCondensentrator;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;


@Pseudo
@Mixin(
    value = {
        TileEntityChemicalInfuser.class,
        TileEntityChemicalWasher.class,
        TileEntityElectrolyticSeparator.class,
        TileEntityIsotopicCentrifuge.class,
        TileEntityPigmentMixer.class,
        TileEntityRotaryCondensentrator.class,
    },
    targets = {
        "com.jerry.mekaf.common.tile.base.TileEntityAdvancedFactoryBase",
    },
    remap = false
)
public abstract class MixinModifyRecalculationBaselineMaxOperations extends TileEntityMekanism {
    public MixinModifyRecalculationBaselineMaxOperations(IBlockProvider blockProvider, BlockPos pos, BlockState state) {
        super(blockProvider, pos, state);
    }

    @ModifyExpressionValue(method = "recalculateUpgrades", at = @At(value = "INVOKE", target = "Ljava/lang/Math;pow(DD)D"))
    private double mekanismEmpowered$modifyRecalculationBaselineMaxOperations(double original) {
        return MixinImplTileMachineKt.modifyRecalculationBaselineMaxOperations(this, original);
    }
}
