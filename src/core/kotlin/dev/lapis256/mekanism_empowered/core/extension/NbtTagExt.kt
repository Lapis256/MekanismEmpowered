@file:Suppress("Unused")

package dev.lapis256.mekanism_empowered.core.extension

import dev.lapis256.mekanism_empowered.core.common.util.TypedListTag
import net.minecraft.nbt.*


/**
 * DO NOT USE THIS MAP.
 *
 * このマップを使わないでください。
 */
val TYPE_ID_MAP_INTERNAL_DO_NOT_USE = mapOf(
    ByteTag::class to Tag.TAG_BYTE,
    ShortTag::class to Tag.TAG_SHORT,
    IntTag::class to Tag.TAG_INT,
    LongTag::class to Tag.TAG_LONG,
    FloatTag::class to Tag.TAG_FLOAT,
    DoubleTag::class to Tag.TAG_DOUBLE,
    ByteArrayTag::class to Tag.TAG_BYTE_ARRAY,
    StringTag::class to Tag.TAG_STRING,
    ListTag::class to Tag.TAG_LIST,
    CompoundTag::class to Tag.TAG_COMPOUND,
    IntArrayTag::class to Tag.TAG_INT_ARRAY,
    LongArrayTag::class to Tag.TAG_LONG_ARRAY,
    NumericTag::class to Tag.TAG_ANY_NUMERIC
)

/**
 * DO NOT USE THIS FUNCTION.
 *
 * この関数を使わないでください。
 */
inline fun <reified TAG : Tag> getIdInternalDoNotUse() =
    TYPE_ID_MAP_INTERNAL_DO_NOT_USE[TAG::class] ?: error("Unsupported tag type: ${TAG::class.simpleName}")


/**
 * [CompoundTag.contains] with type specified as Byte
 *
 * タイプを Byte で指定する [CompoundTag.contains]
 */
fun CompoundTag.contains(key: String, type: Byte) = contains(key, type.toInt())

/**
 * [CompoundTag.getList] with type specified as Byte
 *
 * タイプを Byte で指定する [CompoundTag.getList]
 */
fun CompoundTag.getList(key: String, type: Byte): ListTag = getList(key, type.toInt())

/**
 * Removes and returns the tag associated with the given key, or null if it does not exist.
 *
 * キーに関連付けられたタグを削除して返します。存在しない場合は null を返します。
 */
fun CompoundTag.pop(key: String): Tag? = get(key)?.also { remove(key) }

/**
 * Retrieves the tag associated with the given key and casts it to the specified type or returns null if the key does not exist or the tag is of a different type.
 *
 * 指定されたキーの [Tag] を取得し、指定されたタイプにキャストします。キーが存在しない場合や [Tag] のタイプが異なる場合は null を返します。
 */
private inline fun <reified TAG : Tag> CompoundTag.getTypedOrNull(key: String) = get(key) as? TAG

/*
 * Getters for various tag types that return null if the key does not exist or if the [Tag] is of a different type.
 *
 * さまざまなタグタイプのゲッターで、キーが存在しない場合や [Tag] のタイプが異なる場合は null を返します。
 */
fun CompoundTag.getByteOrNull(key: String) = getTypedOrNull<NumericTag>(key)?.asByte
fun CompoundTag.getShortOrNull(key: String) = getTypedOrNull<NumericTag>(key)?.asShort
fun CompoundTag.getIntOrNull(key: String) = getTypedOrNull<NumericTag>(key)?.asInt
fun CompoundTag.getLongOrNull(key: String) = getTypedOrNull<NumericTag>(key)?.asLong
fun CompoundTag.getFloatOrNull(key: String) = getTypedOrNull<NumericTag>(key)?.asFloat
fun CompoundTag.getDoubleOrNull(key: String) = getTypedOrNull<NumericTag>(key)?.asDouble
fun CompoundTag.getStringOrNull(key: String) = getTypedOrNull<StringTag>(key)?.asString
fun CompoundTag.getByteArrayOrNull(key: String) = getTypedOrNull<ByteArrayTag>(key)?.asByteArray
fun CompoundTag.getIntArrayOrNull(key: String) = getTypedOrNull<IntArrayTag>(key)?.asIntArray
fun CompoundTag.getLongArrayOrNull(key: String) = getTypedOrNull<LongArrayTag>(key)?.asLongArray
fun CompoundTag.getCompoundOrNull(key: String) = getTypedOrNull<CompoundTag>(key)
fun CompoundTag.getListOrNull(key: String) = getTypedOrNull<ListTag>(key)
fun CompoundTag.getListOrNull(key: String, type: Byte) = getTypedOrNull<ListTag>(key)?.takeIf { it.isEmpty() || it.elementType == type }

/**
 * Retrieves the [ListTag] associated with the given key and wraps it in a [TypedListTag] of the tag type specified by the generics.
 * Returns null if the key does not exist or if the elements of the [ListTag] are of a different type.
 *
 * 指定されたキーの [ListTag] を取得し、ジェネリクスで指定したタグタイプの [TypedListTag] でラップして返します。
 * キーが存在しない場合や [ListTag] の要素のタイプが異なる場合は null を返します。
 */
inline fun <reified TAG : Tag> CompoundTag.getTypedListOrNull(key: String) =
    getListOrNull(key, getIdInternalDoNotUse<TAG>())?.let { TypedListTag<TAG>(it) }


/**
 * Retrieves the [Tag] at the specified index. Returns null if the index is out of bounds.
 *
 * 指定されたインデックスにある [Tag] を取得します。インデックスが範囲外の場合は null を返します。
 */
fun ListTag.getOrNull(index: Int) = takeIf { index in 0..<size }?.get(index)

/**
 * Removes and returns the [Tag] at the specified index. Returns null if the index is out of bounds.
 *
 * 指定されたインデックスにある [Tag] を削除して返します。インデックスが範囲外の場合は null を返します。
 */
fun ListTag.pop(index: Int) = getOrNull(index)?.also { removeAt(index) }


/**
 * Retrieves the [Tag] at the specified index and casts it to the specified type. Returns null if the index is out of bounds or if the [Tag] is of a different type.
 *
 * 指定されたインデックスにある [Tag] を取得し、指定されたタイプにキャストします。インデックスが範囲外の場合や [Tag] のタイプが異なる場合は null を返します。
 */
private inline fun <reified TAG : Tag> ListTag.getTypedOrNull(index: Int) = this.getOrNull(index) as? TAG

/*
 * Getters for various tag types that return null if the index is out of bounds or if the [Tag] is of a different type.
 *
 * さまざまなタグタイプのゲッターで、インデックスが範囲外の場合や [Tag] のタイプが異なる場合は null を返します。
 */
fun ListTag.getByteOrNull(index: Int) = getTypedOrNull<NumericTag>(index)?.asByte
fun ListTag.getShortOrNull(index: Int) = getTypedOrNull<NumericTag>(index)?.asShort
fun ListTag.getIntOrNull(index: Int) = getTypedOrNull<NumericTag>(index)?.asInt
fun ListTag.getLongOrNull(index: Int) = getTypedOrNull<NumericTag>(index)?.asLong
fun ListTag.getFloatOrNull(index: Int) = getTypedOrNull<NumericTag>(index)?.asFloat
fun ListTag.getDoubleOrNull(index: Int) = getTypedOrNull<NumericTag>(index)?.asDouble
fun ListTag.getStringOrNull(index: Int) = getTypedOrNull<StringTag>(index)?.asString
fun ListTag.getByteArrayOrNull(index: Int) = getTypedOrNull<ByteArrayTag>(index)?.asByteArray
fun ListTag.getIntArrayOrNull(index: Int) = getTypedOrNull<IntArrayTag>(index)?.asIntArray
fun ListTag.getLongArrayOrNull(index: Int) = getTypedOrNull<LongArrayTag>(index)?.asLongArray
fun ListTag.getCompoundOrNull(index: Int) = getTypedOrNull<CompoundTag>(index)
fun ListTag.getListOrNull(index: Int) = getTypedOrNull<ListTag>(index)
fun ListTag.getListOrNull(index: Int, type: Byte) = getTypedOrNull<ListTag>(index)?.takeIf { it.isEmpty() || it.elementType == type }

/**
 * Retrieves the [ListTag] at the specified index and wraps it in a [TypedListTag] of the tag type specified by the generics.
 *
 * 指定されたインデックスかつジェネリクスで指定したタグタイプの [TypedListTag] を取得します。インデックスが範囲外の場合や [Tag] のタイプが異なる場合は null を返します。
 */
inline fun <reified TAG : Tag> ListTag.getTypedListOrNull(index: Int) =
    getListOrNull(index, getIdInternalDoNotUse<TAG>())?.let { TypedListTag<TAG>(it) }
