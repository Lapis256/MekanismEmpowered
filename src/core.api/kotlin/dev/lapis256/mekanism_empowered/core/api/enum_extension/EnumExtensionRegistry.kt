package dev.lapis256.mekanism_empowered.core.api.enum_extension

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.reflect.KClass


object EnumExtensionRegistry {
    private val enumExtensions = ConcurrentHashMap<KClass<*>, CopyOnWriteArrayList<EnumEntryDelegate<*>>>()

    fun <ENUM : Enum<ENUM>> registerEnumEntry(enumClass: KClass<ENUM>, entry: EnumEntryDelegate<ENUM>) {
        enumExtensions.computeIfAbsent(enumClass) { CopyOnWriteArrayList() }.add(entry)
    }

    fun <ENUM : Enum<ENUM>> getEnumEntry(enumClass: KClass<ENUM>): List<EnumEntryDelegate<ENUM>> {
        return enumExtensions[enumClass]?.filterIsInstance<EnumEntryDelegate<ENUM>>() ?: emptyList()
    }
}
