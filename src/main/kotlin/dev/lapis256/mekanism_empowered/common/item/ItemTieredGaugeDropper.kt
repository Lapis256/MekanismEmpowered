package dev.lapis256.mekanism_empowered.common.item

import dev.lapis256.mekanism_empowered.common.init.MekEmpItems
import mekanism.api.Action
import mekanism.api.AutomationType
import mekanism.api.chemical.IMekanismChemicalHandler
import mekanism.api.fluid.IMekanismFluidHandler
import mekanism.api.functions.ConstantPredicates
import mekanism.common.attachments.containers.chemical.AttachedChemicals
import mekanism.common.attachments.containers.chemical.ChemicalTanksBuilder
import mekanism.common.attachments.containers.chemical.ComponentBackedChemicalTank
import mekanism.common.attachments.containers.creator.BaseContainerCreator
import mekanism.common.attachments.containers.creator.IBasicContainerCreator
import mekanism.common.attachments.containers.fluid.AttachedFluids
import mekanism.common.attachments.containers.fluid.ComponentBackedFluidTank
import mekanism.common.attachments.containers.fluid.FluidTanksBuilder
import mekanism.common.capabilities.Capabilities
import mekanism.common.item.ItemGaugeDropper
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.ClickAction
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.fluids.capability.IFluidHandler
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem


class ItemTieredGaugeDropper(val tier: MekEmpItems.GaugeDropperTier, properties: Properties) : ItemGaugeDropper(properties) {
    companion object {
        fun getChemicalTankCreator(tier: MekEmpItems.GaugeDropperTier): BaseContainerCreator<AttachedChemicals, ComponentBackedChemicalTank> =
            ChemicalTanksBuilder.builder().addTank(IBasicContainerCreator { type, attachedTo, containerIndex ->
                ComponentBackedChemicalTank(
                    attachedTo,
                    containerIndex,
                    ConstantPredicates.alwaysTrueBi(),
                    ConstantPredicates.alwaysTrueBi(),
                    ConstantPredicates.alwaysTrue(),
                    tier.rateSupplier.get(),
                    tier.capacitySupplier.get(),
                    null
                )
            }).build()

        fun getFluidTankCreator(tier: MekEmpItems.GaugeDropperTier): BaseContainerCreator<AttachedFluids, ComponentBackedFluidTank> =
            FluidTanksBuilder.builder().addTank(IBasicContainerCreator { type, attachedTo, containerIndex ->
                ComponentBackedFluidTank(
                    attachedTo,
                    containerIndex,
                    ConstantPredicates.alwaysTrueBi(),
                    ConstantPredicates.alwaysTrueBi(),
                    ConstantPredicates.alwaysTrue(),
                    tier.rateSupplier.get(),
                    tier.capacitySupplier.get(),
                )
            }).build()
    }

    fun transferFluid(fromStack: ItemStack, toStack: ItemStack): Boolean {
        val fromHandler = Capabilities.FLUID.getCapability(fromStack) ?: return false
        val toHandler = Capabilities.FLUID.getCapability(toStack) ?: return false

        return if (fromHandler is IMekanismFluidHandler && toHandler is IMekanismFluidHandler) {
            transferFluidMek(fromHandler, toHandler)
        } else {
            transferFluidStandard(fromHandler, toHandler)
        }
    }

    fun transferFluidStandard(fromHandler: IFluidHandlerItem, toHandler: IFluidHandlerItem): Boolean {
        for (fromTankIndex in 0 until fromHandler.tanks) {
            val fluidStack = fromHandler.getFluidInTank(fromTankIndex)
            if (fluidStack.isEmpty) {
                continue
            }

            val simulated = toHandler.fill(fluidStack.copy(), IFluidHandler.FluidAction.SIMULATE)
            if (simulated <= 0) {
                continue
            }

            val drained = fromHandler.drain(simulated, IFluidHandler.FluidAction.EXECUTE)
            if (!drained.isEmpty) {
                toHandler.fill(drained, IFluidHandler.FluidAction.EXECUTE)
                return true
            }
        }

        return false
    }

    fun transferFluidMek(fromHandler: IMekanismFluidHandler, toHandler: IMekanismFluidHandler): Boolean {
        for (fromTank in fromHandler.getFluidTanks(null)) {
            val fluid = fromTank.fluid
            if (fluid.isEmpty) {
                continue
            }

            @Suppress("DuplicatedCode")
            for (toTank in toHandler.getFluidTanks(null)) {
                val remaining = toTank.insert(fluid, Action.SIMULATE, AutomationType.MANUAL)
                val inserted = fluid.amount - remaining.amount
                if (inserted <= 0) {
                    continue
                }
                val extracted = fromTank.extract(inserted, Action.EXECUTE, AutomationType.MANUAL)
                if (!extracted.isEmpty) {
                    toTank.insert(extracted, Action.EXECUTE, AutomationType.MANUAL)
                    return true
                }
            }
        }
        return false
    }

    fun transferChemical(fromStack: ItemStack, toStack: ItemStack): Boolean {
        val fromHandler = Capabilities.CHEMICAL.getCapability(fromStack) as? IMekanismChemicalHandler ?: return false
        val toHandler = Capabilities.CHEMICAL.getCapability(toStack) as? IMekanismChemicalHandler ?: return false

        for (fromTank in fromHandler.getChemicalTanks(null)) {
            val chemical = fromTank.stack
            if (chemical.isEmpty) {
                continue
            }

            @Suppress("DuplicatedCode")
            for (toTank in toHandler.getChemicalTanks(null)) {
                val remaining = toTank.insert(chemical, Action.SIMULATE, AutomationType.MANUAL)
                val inserted = chemical.amount - remaining.amount
                if (inserted <= 0) {
                    continue
                }
                val extracted = fromTank.extract(inserted, Action.EXECUTE, AutomationType.MANUAL)
                if (!extracted.isEmpty) {
                    toTank.insert(extracted, Action.EXECUTE, AutomationType.MANUAL)
                    return true
                }
            }
        }
        return false
    }

    override fun overrideStackedOnOther(stack: ItemStack, slot: Slot, action: ClickAction, player: Player): Boolean {
        val other = slot.item

        return when (action) {
            ClickAction.PRIMARY -> transferFluid(other, stack) || transferChemical(other, stack)
            ClickAction.SECONDARY -> transferFluid(stack, other) || transferChemical(stack, other)
        }
    }
}
