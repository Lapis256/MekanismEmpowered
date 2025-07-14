package dev.lapis256.mekanism_empowered.mixin.common;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dev.lapis256.mekanism_empowered.common.item.ItemTieredGaugeDropper;
import mekanism.common.network.to_server.PacketDropperUse;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;


@Mixin(value = PacketDropperUse.class, remap = false)
public class MixinPacketDropperUse {
    @Shadow
    @Final
    @Mutable
    private PacketDropperUse.DropperAction action;

    @ModifyExpressionValue(method = "handle", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getItem()Lnet/minecraft/world/item/Item;"))
    private Item onHandle(Item original) {
        if (action == PacketDropperUse.DropperAction.DUMP_TANK && original instanceof ItemTieredGaugeDropper) {
            action = PacketDropperUse.DropperAction.FILL_DROPPER;
        }

        return original;
    }
}
