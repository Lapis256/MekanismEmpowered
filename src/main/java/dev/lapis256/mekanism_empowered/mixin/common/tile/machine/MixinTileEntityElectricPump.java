package dev.lapis256.mekanism_empowered.mixin.common.tile.machine;

import dev.lapis256.mekanism_empowered.mixin_impl.MixinImplTileEntityElectricPump;
import mekanism.api.providers.IBlockProvider;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.tile.machine.TileEntityElectricPump;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;


@Mixin(value = TileEntityElectricPump.class, remap = false)
public abstract class MixinTileEntityElectricPump extends TileEntityMekanism {
    public MixinTileEntityElectricPump(IBlockProvider blockProvider, BlockPos pos, BlockState state) {
        super(blockProvider, pos, state);
    }

    @ModifyArg(method = "getOutput", at = @At(value = "INVOKE", target = "Lmekanism/common/registration/impl/FluidRegistryObject;getFluidStack(I)Lnet/minecraftforge/fluids/FluidStack;"), index = 0)
    private int mekanismEmpowered$modifyHeavyWaterOutputAmount(int original) {
        return MixinImplTileEntityElectricPump.modifyWaterOutputAmount(this, original);
    }
}
