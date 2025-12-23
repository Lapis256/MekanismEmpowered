package dev.lapis256.mekanism_empowered.mixin.common.tile.machine;

import dev.lapis256.mekanism_empowered.mixin_impl.MixinImplTileEntityElectricPump;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.tile.machine.TileEntityElectricPump;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;


@Mixin(value = TileEntityElectricPump.class, remap = false)
public class MixinTileEntityElectricPump extends TileEntityMekanism {
    public MixinTileEntityElectricPump(Holder<Block> blockProvider, BlockPos pos, BlockState state) {
        super(blockProvider, pos, state);
    }

    @ModifyArg(method = "getOutput", at = @At(value = "INVOKE", target = "Lmekanism/common/registration/impl/FluidRegistryObject;asStack(I)Lnet/neoforged/neoforge/fluids/FluidStack;"), index = 0)
    private int mekanismEmpowered$modifyHeavyWaterOutputAmount(int original) {
        return MixinImplTileEntityElectricPump.modifyWaterOutputAmount(this, original);
    }
}
