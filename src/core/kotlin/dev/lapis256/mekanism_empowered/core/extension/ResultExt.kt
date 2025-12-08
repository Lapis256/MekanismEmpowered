package dev.lapis256.mekanism_empowered.core.extension

import com.mojang.serialization.DataResult


fun <R> Result<DataResult<R>>.toDataResult(): DataResult<R> = this.fold(
    onSuccess = { it },
    onFailure = { DataResult.error { it.message ?: "Unknown error" } }
)
