package dev.lapis256.mekanism_empowered.core.common.util

import net.minecraft.nbt.CollectionTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.StreamTagVisitor
import net.minecraft.nbt.Tag
import net.minecraft.nbt.TagType
import net.minecraft.nbt.TagVisitor
import java.io.DataOutput


/**
 * A typed wrapper for [ListTag] that provides type-safe access to its elements.
 *
 * [ListTag] の型付きラッパーで、その要素への型安全なアクセスを提供します。
 */
class TypedListTag<TAG : Tag>(private val inner: ListTag) : CollectionTag<TAG>() {
    @Suppress("UNCHECKED_CAST")
    override fun set(index: Int, tag: TAG) = inner.set(index, tag) as TAG

    @Suppress("UNCHECKED_CAST")
    override fun removeAt(index: Int) = inner.removeAt(index) as TAG

    @Suppress("UNCHECKED_CAST")
    override fun get(index: Int) = inner[index] as TAG

    override fun add(index: Int, tag: TAG) = inner.add(index, tag)
    override fun copy() = TypedListTag<TAG>(inner.copy())
    override fun setTag(index: Int, tag: Tag) = inner.setTag(index, tag)
    override fun addTag(index: Int, tag: Tag) = inner.addTag(index, tag)
    override fun isEmpty() = inner.isEmpty()
    override fun getElementType() = inner.elementType
    override val size get() = inner.size
    override fun write(output: DataOutput) = inner.write(output)
    override fun getId() = inner.id
    override fun getType(): TagType<*> = inner.type
    override fun clear() = inner.clear()
    override fun equals(other: Any?) = inner == other
    override fun hashCode() = inner.hashCode()
    override fun sizeInBytes() = inner.sizeInBytes()
    override fun accept(visitor: TagVisitor) = inner.accept(visitor)
    override fun accept(visitor: StreamTagVisitor): StreamTagVisitor.ValueResult = inner.accept(visitor)
    override fun toString() = "TypedListTag($inner)"
}
