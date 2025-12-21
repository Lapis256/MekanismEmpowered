package dev.lapis256.mekanism_empowered.core.mixin_impl

import dev.lapis256.mekanism_empowered.core.api.MekEmpCoreSerializationConstants
import dev.lapis256.mekanism_empowered.core.api.upgrade.AdditionalUpgradeLoader
import dev.lapis256.mekanism_empowered.core.extension.getCompoundOrNull
import dev.lapis256.mekanism_empowered.core.extension.getIntOrNull
import dev.lapis256.mekanism_empowered.core.extension.getTypedListOrNull
import dev.lapis256.mekanism_empowered.core.mixin.common.InvokeAPILang
import mekanism.api.NBTConstants
import mekanism.api.Upgrade
import mekanism.api.text.APILang
import mekanism.api.text.EnumColor
import mekanism.api.text.ILangEntry
import mekanism.common.util.EnumUtils
import net.minecraft.nbt.CompoundTag
import java.util.*


class MixinImplUpgrade(val constructor: (String, Int, String, APILang, APILang, Int, EnumColor) -> Upgrade) {
    private val upgradeMap by lazy { EnumUtils.UPGRADES.associateBy(Upgrade::getRawName) }

    private fun createDummy(key: ILangEntry) = InvokeAPILang.createDummy("MEKANISM_EMPOWERED_CORE_DUMMY_API_LANG", 999, key.translationKey)

    private var _vanillaLastOrdinal: Int? = null
    private var vanillaLastOrdinal: Int
        get() = _vanillaLastOrdinal ?: error("Vanilla last ordinal not set")
        set(value) = _vanillaLastOrdinal
            ?.let { error("Vanilla last ordinal is already set to $it, cannot set to $value") }
            ?: run { _vanillaLastOrdinal = value }

    private var _additionalLastOrdinal: Int? = null
    private var additionalLastOrdinal: Int
        get() = _additionalLastOrdinal ?: error("Additional last ordinal not set")
        set(value) = _additionalLastOrdinal
            ?.let { error("Additional last ordinal is already set to $it, cannot set to $value") }
            ?: run { _additionalLastOrdinal = value }

    private val additionalOrdinalRange: IntRange by lazy { (vanillaLastOrdinal + 1)..additionalLastOrdinal }

    private fun altConstructor(
        internalName: String,
        ordinal: Int,
        name: String,
        langKey: ILangEntry,
        descLangKey: ILangEntry,
        maxStack: Int,
        color: EnumColor
    ) =
        constructor(internalName, ordinal, name, createDummy(langKey), createDummy(descLangKey), maxStack, color)

    private val loader = AdditionalUpgradeLoader(::altConstructor)

    /**
     * Registers the added upgrades to [Upgrade].
     *
     * 追加されたアップグレードを [Upgrade] に登録します。
     */
    fun initAdditionalUpgrades(builtInUpgrades: Array<Upgrade>): Array<Upgrade> {
        vanillaLastOrdinal = builtInUpgrades.size
        return loader.initAdditionalEnumEntry(builtInUpgrades)
            .also { additionalLastOrdinal = it.size - 1 }
    }

    /**
     * Removes elements corresponding to upgrades added from [NBTConstants.UPGRADES].
     * This is to avoid conflicts with upgrades added by other mods, such as when this mod is installed later.
     * Although there is a possibility that already installed upgrades may be lost, priority is given to upgrades protected by this mod.
     *
     * If other mod developers are reading this message, please consider saving to your own tag like [saveAdditionalMap] / [buildAdditionalMap].
     *
     *
     * [NBTConstants.UPGRADES] から追加されたアップグレードに該当する要素を削除します。
     * これは、この Mod が後からインストールされた場合などに、他の Mod によって追加されたアップグレードとの競合を避けるためです。
     * 既にインストールされているアップグレードが失われる可能性がありますが、この Mod によって保護されているアップグレードを優先します。
     *
     * 他のMod開発者の方がこのメッセージを読んでいる場合は、ぜひ [saveAdditionalMap] / [buildAdditionalMap] のように独自のタグに保存することを検討してください。
     */
    private fun cleanupAdditionalUpgrade(nbtTags: CompoundTag) {
        nbtTags.getTypedListOrNull<CompoundTag>(NBTConstants.UPGRADES)?.removeIf {
            val type = it.getIntOrNull(NBTConstants.TYPE) ?: return@removeIf false
            return@removeIf type in additionalOrdinalRange
        }
    }

    /**
     * Loads additional upgrades from [MekEmpCoreSerializationConstants.UPGRADES].
     *
     * [MekEmpCoreSerializationConstants.UPGRADES] から追加のアップグレードを読み込みます。
     */
    fun buildAdditionalMap(upgrades: MutableMap<Upgrade, Int>?, nbtTags: CompoundTag?): MutableMap<Upgrade, Int>? {
        nbtTags ?: return null

        cleanupAdditionalUpgrade(nbtTags)

        val compound = nbtTags.getCompoundOrNull(MekEmpCoreSerializationConstants.UPGRADES)
            ?: nbtTags.getCompoundOrNull(MekEmpCoreSerializationConstants.UPGRADES_OLD)
            ?: return null

        val upgrades = upgrades ?: EnumMap(Upgrade::class.java)

        for (entry in compound.allKeys) {
            val upgrade = upgradeMap[entry] ?: continue
            val amount = compound.getInt(entry)
            if (amount > 0) {
                upgrades[upgrade] = amount
            }
        }

        return upgrades
    }

    /**
     * Saves additional upgrades to another tag.
     * Returns entries of upgrades removed from the original map.
     *
     * 追加のアップグレードを別のタグに保存します。
     * 元のマップから削除されたアップグレードのエントリを返します。
     */
    fun saveAdditionalMap(upgrades: Set<Map.Entry<Upgrade, Int>>, nbtTags: CompoundTag): Set<Map.Entry<Upgrade, Int>> =
        upgrades
            .partition { e -> e.key.ordinal in additionalOrdinalRange }
            .let { (additional, vanilla) ->
                nbtTags.put(MekEmpCoreSerializationConstants.UPGRADES, CompoundTag().apply {
                    for ((upgrade, amount) in additional) {
                        putInt(upgrade.rawName, amount)
                    }
                })
                return@let vanilla.toSet()
            }
}
