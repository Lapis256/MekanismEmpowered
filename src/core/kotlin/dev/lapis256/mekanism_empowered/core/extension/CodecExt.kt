package dev.lapis256.mekanism_empowered.core.extension

import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult


fun <S, A> Codec<A>.flatXmapCatching(
    decode: (A) -> DataResult<S>,
    encode: (S) -> DataResult<A>
): Codec<S> = flatXmap({ d ->
    runCatching { decode(d) }.toDataResult()
}, { s ->
    runCatching { encode(s) }.toDataResult()
})
