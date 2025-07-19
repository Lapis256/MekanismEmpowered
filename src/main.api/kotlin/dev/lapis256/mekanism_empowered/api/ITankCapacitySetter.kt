package dev.lapis256.mekanism_empowered.api


interface ITankCapacitySetter<T> {
    fun `mekanism_empowered$setCapacity`(capacity: T)

    fun `mekanism_empowered$getInitialCapacity`(): T

    interface Fluid : ITankCapacitySetter<Int>
    interface Chemical : ITankCapacitySetter<Long>
}
