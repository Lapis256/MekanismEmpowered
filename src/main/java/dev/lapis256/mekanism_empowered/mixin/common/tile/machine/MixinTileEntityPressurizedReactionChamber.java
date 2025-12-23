package dev.lapis256.mekanism_empowered.mixin.common.tile.machine;

import dev.lapis256.mekanism_empowered.mixin_impl.MixinImplTileMachineKt;
import mekanism.common.tile.machine.TileEntityPressurizedReactionChamber;
import mekanism.common.tile.prefab.TileEntityConfigurableMachine;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Pseudo
@Mixin(
    value = TileEntityPressurizedReactionChamber.class,
    targets = {
        "com.jerry.mekaf.common.tile.factory.TileEntityPressurizedReactingFactory"
    },
    remap = false
)
public abstract class MixinTileEntityPressurizedReactionChamber extends TileEntityConfigurableMachine {
    protected MixinTileEntityPressurizedReactionChamber(Holder<Block> blockProvider, BlockPos pos, BlockState state) {
        super(blockProvider, pos, state);
    }

    @Inject(method = "onCachedRecipeChanged", at = @At(value = "FIELD", target = "Lmekanism/api/Upgrade;SPEED:Lmekanism/api/Upgrade;", opcode = Opcodes.GETSTATIC))
    private void mekanismEmpowered$recalculateAdditionalUpgrades(CallbackInfo ci) {
        MixinImplTileMachineKt.prcRecalculateAdditionalUpgrades(this);
    }
}
