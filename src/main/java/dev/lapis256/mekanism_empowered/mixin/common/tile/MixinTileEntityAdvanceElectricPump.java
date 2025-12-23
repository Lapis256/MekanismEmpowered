package dev.lapis256.mekanism_empowered.mixin.common.tile;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dev.lapis256.mekanism_empowered.mixin_impl.MixinImplTileEntityElectricPump;
import mekanism.common.tile.base.TileEntityMekanism;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;


@Pseudo
@Mixin(targets = "com.jerry.mekextras.common.tile.machine.TileEntityAdvancedElectricPump", remap = false)
public class MixinTileEntityAdvanceElectricPump extends TileEntityMekanism {
    public MixinTileEntityAdvanceElectricPump(Holder<Block> blockProvider, BlockPos pos, BlockState state) {
        super(blockProvider, pos, state);
    }

    @Definition(id = "upgradeComponent", field = "Lcom/jerry/mekextras/common/tile/machine/TileEntityAdvancedElectricPump;upgradeComponent:Lmekanism/common/tile/component/TileComponentUpgrade;")
    @Definition(id = "getUpgrades", method = "Lmekanism/common/tile/component/TileComponentUpgrade;getUpgrades(Lmekanism/api/Upgrade;)I")
    @Definition(id = "SPEED", field = "Lmekanism/api/Upgrade;SPEED:Lmekanism/api/Upgrade;")
    @Expression("? * (1 + this.upgradeComponent.getUpgrades(SPEED))")
    @ModifyExpressionValue(method = "recalculateUpgrades", at = @At("MIXINEXTRAS:EXPRESSION"))
    private int mekanismEmpowered$modifyOutputRate(int original) {
        return MixinImplTileEntityElectricPump.modifyOutputRate(this, original);
    }

    @ModifyArg(method = "getOutput", at = @At(value = "INVOKE", target = "Lmekanism/common/registration/impl/FluidRegistryObject;asStack(I)Lnet/neoforged/neoforge/fluids/FluidStack;"), index = 0)
    private int mekanismEmpowered$modifyHeavyWaterOutputAmount(int original) {
        return MixinImplTileEntityElectricPump.modifyWaterOutputAmount(this, original);
    }

    @ModifyArg(method = "getOutput", at = @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/fluids/FluidStack;<init>(Lnet/minecraft/world/level/material/Fluid;I)V", ordinal = 1), index = 1)
    private int mekanismEmpowered$modifyWaterOutputAmount(int original) {
        return MixinImplTileEntityElectricPump.modifyWaterOutputAmount(this, original);
    }
}
