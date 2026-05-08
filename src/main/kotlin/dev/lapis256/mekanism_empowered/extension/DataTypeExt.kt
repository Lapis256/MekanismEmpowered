package dev.lapis256.mekanism_empowered.extension

import dev.lapis256.mekanism_empowered.core.extension.canInput
import mekanism.common.tile.component.config.DataType


inline val DataType.isAutoInsertTarget
    get() = canInput || this == DataType.ENERGY || this == DataType.EXTRA
