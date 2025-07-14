package dev.lapis256.mekanism_empowered.data.provider

import dev.lapis256.mekanism_empowered.api.text.MekEmpAPILang
import dev.lapis256.mekanism_empowered.api.MekanismEmpoweredAPI
import dev.lapis256.mekanism_empowered.common.MekEmpLang
import dev.lapis256.mekanism_empowered.common.config.MekEmpConfig
import dev.lapis256.mekanism_empowered.common.init.MekEmpItems
import dev.lapis256.mekanism_empowered.common.config.MekEmpConfigTranslations
import dev.lapis256.mekanism_empowered.common.inventory.container.MekEmpWindowType
import dev.lapis256.mekanism_empowered.core.api.text.ILangEnglishHolder
import mekanism.api.text.ILangEntry
import net.minecraft.data.PackOutput
import net.neoforged.neoforge.common.data.LanguageProvider
import kotlin.reflect.KClass


class MekEmpLanguageProvider(output: PackOutput) : LanguageProvider(output, MekanismEmpoweredAPI.MOD_ID, "en_us") {
    override fun addTranslations() {
        add("${MekanismEmpoweredAPI.MOD_ID}.configuration.title", "${MekanismEmpoweredAPI.MOD_NAME} Config")
        for (config in MekEmpConfig.configValues) {
            val key = "${MekanismEmpoweredAPI.MOD_ID}.configuration.section.${MekanismEmpoweredAPI.MOD_NAME_CLEAN.lowercase()}.${config.name}.toml"
            add(key, config.translation)
            add("$key.title", "${MekanismEmpoweredAPI.MOD_NAME} - ${config.translation}")
        }

        for (type in arrayOf(MekEmpWindowType.INSERTER)) {
            for (path in type.savePaths) path.translationKey.let { key ->
                add(key, path.title())
                add("$key.tooltip", path.tooltip())
                path.button()?.let { add("$key.button", it) }
            }
        }

        for (entry in MekEmpConfigTranslations.entries) entry.translationKey.let { key ->
            add(key, entry.title())
            add("$key.tooltip", entry.tooltip())
            entry.button()?.let { add("$key.button", it) }
        }

        addLangEnum(MekEmpAPILang::class)
        addLangEnum(MekEmpLang::class)

        for ((item, englishName) in MekEmpItems.ENGLISH_NAME_MAP) {
            addItem(item, englishName)
        }
    }

    fun <E> addLangEnum(enum: KClass<out E>) where E : Enum<E>, E : ILangEntry, E : ILangEnglishHolder {
        for (entry in enum.java.enumConstants) {
            add(entry.translationKey, entry.english)
        }
    }
}
