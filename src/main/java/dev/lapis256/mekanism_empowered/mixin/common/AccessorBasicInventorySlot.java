package dev.lapis256.mekanism_empowered.mixin.common;

import mekanism.common.inventory.container.slot.ContainerSlotType;
import mekanism.common.inventory.slot.BasicInventorySlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;


@Mixin(value = BasicInventorySlot.class, remap = false)
public interface AccessorBasicInventorySlot {
    @Accessor
    ContainerSlotType getSlotType();
}
