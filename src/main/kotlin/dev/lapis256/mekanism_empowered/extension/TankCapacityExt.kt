package dev.lapis256.mekanism_empowered.extension

import dev.lapis256.mekanism_empowered.api.ITankCapacitySetter
import mekanism.api.chemical.BasicChemicalTank
import mekanism.common.capabilities.fluid.BasicFluidTank


fun BasicFluidTank.setCapacity(capacity: Int) = (this as ITankCapacitySetter.Fluid).`mekanism_empowered$setCapacity`(capacity)

val BasicFluidTank.initialCapacity get() = (this as ITankCapacitySetter.Fluid).`mekanism_empowered$getInitialCapacity`()

fun BasicChemicalTank.setCapacity(capacity: Long) = (this as ITankCapacitySetter.Chemical).`mekanism_empowered$setCapacity`(capacity)

val BasicChemicalTank.initialCapacity get() = (this as ITankCapacitySetter.Chemical).`mekanism_empowered$getInitialCapacity`()
