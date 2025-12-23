package dev.lapis256.mekanism_empowered.core.mixin_impl

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.UnboundedMapCodec
import dev.lapis256.mekanism_empowered.core.api.MekEmpCoreSerializationConstants
import dev.lapis256.mekanism_empowered.core.api.upgrade.AdditionalUpgradeLoader
import dev.lapis256.mekanism_empowered.core.common.MekanismEmpoweredCore
import dev.lapis256.mekanism_empowered.core.extension.getCompoundOrNull
import dev.lapis256.mekanism_empowered.core.extension.getIntOrNull
import dev.lapis256.mekanism_empowered.core.extension.getTypedListOrNull
import mekanism.api.SerializationConstants
import mekanism.api.Upgrade
import mekanism.api.text.EnumColor
import mekanism.api.text.ILangEntry
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
import net.minecraft.util.ExtraCodecs
import net.minecraft.util.StringRepresentable
import java.util.*


class MixinImplUpgrade(constructor: (String, Int, String, ILangEntry, ILangEntry, Int, EnumColor) -> Upgrade) {
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

    private val loader = AdditionalUpgradeLoader(constructor)

    private var _codec: Codec<Upgrade>? = null
    private var codec: Codec<Upgrade>
        get() = _codec ?: error("Codec not initialized")
        set(value) = _codec
            ?.let { error("Codec is already initialized") }
            ?: run { _codec = value }

    private val additionalCodec: UnboundedMapCodec<Upgrade, Int> by lazy {
        Codec.unboundedMap(codec, ExtraCodecs.POSITIVE_INT)
    }

    /**
     * Registers the added upgrades to [Upgrade].
     *
     * 追加されたアップグレードを [Upgrade] に登録します。
     */
    fun initAdditionalUpgrades(builtInUpgrades: Array<Upgrade>): Array<Upgrade> {
        vanillaLastOrdinal = builtInUpgrades.size
        return loader.initAdditionalEnumEntry(builtInUpgrades)
            .also {
                additionalLastOrdinal = it.size - 1
                codec = StringRepresentable.fromEnum { it }
            }
    }

    /**
     * Removes elements corresponding to upgrades added from [SerializationConstants.UPGRADES].
     * This is to avoid conflicts with upgrades added by other mods, such as when this mod is installed later.
     * Although there is a possibility that already installed upgrades may be lost, priority is given to upgrades protected by this mod.
     *
     * If other mod developers are reading this message, please consider saving to your own tag like [saveAdditionalMap] / [buildAdditionalMap].
     *
     *
     * [SerializationConstants.UPGRADES] から追加されたアップグレードに該当する要素を削除します。
     * これは、この Mod が後からインストールされた場合などに、他の Mod によって追加されたアップグレードとの競合を避けるためです。
     * 既にインストールされているアップグレードが失われる可能性がありますが、この Mod によって保護されているアップグレードを優先します。
     *
     * 他のMod開発者の方がこのメッセージを読んでいる場合は、ぜひ [saveAdditionalMap] / [buildAdditionalMap] のように独自のタグに保存することを検討してください。
     */
    private fun cleanupAdditionalUpgrade(nbtTags: CompoundTag) {
        nbtTags.getTypedListOrNull<CompoundTag>(SerializationConstants.UPGRADES)?.removeIf {
            val type = it.getIntOrNull(SerializationConstants.TYPE) ?: return@removeIf false
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

        additionalCodec.parse(NbtOps.INSTANCE, compound)
            .ifSuccess(upgrades::putAll)
            .ifError { e -> MekanismEmpoweredCore.LOGGER.error("Failed to parse additional upgrades: {}", e) }

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
                additionalCodec.encodeStart(NbtOps.INSTANCE, additional.associate { it.key to it.value })
                    .ifSuccess { tag -> nbtTags.put(MekEmpCoreSerializationConstants.UPGRADES, tag) }
                    .ifError { e -> MekanismEmpoweredCore.LOGGER.error("Failed to save additional upgrades: {}", e) }
                return@let vanilla.toSet()
            }

//    fun buildAdditionalMap(upgrades: MutableMap<Upgrade, Int>?, nbtTags: CompoundTag?): MutableMap<Upgrade, Int>? {
//        nbtTags ?: return null
//
//        if (!nbtTags.contains(MekanismEmpoweredCore.SerializationConstants.UPGRADES, Tag.TAG_COMPOUND.toInt())) {
//            return null
//        }
//
//        val upgrades = upgrades ?: EnumMap(Upgrade::class.java)
//
//        additionalCodec.parse(
//            NbtOps.INSTANCE,
//            nbtTags.getCompound(MekanismEmpoweredCore.SerializationConstants.UPGRADES)
//        )
//            .ifSuccess(upgrades::putAll)
//            .ifError { e -> MekanismEmpoweredCore.LOGGER.error("Failed to parse additional upgrades: {}", e) }
//
//        return upgrades
//    }
//
//    fun filterUpgrades(original: Set<Map.Entry<Upgrade, Int>>): Set<Map.Entry<Upgrade, Int>> {
//        return original
//            .filter { e -> !additionalOrdinals.contains(e.key.ordinal) }
//            .toSet()
//    }
//
//    fun saveAdditionalMap(upgrades: Map<Upgrade, Int>, nbtTags: CompoundTag) {
//        val additionalUpgrades = upgrades
//            .filter { e -> additionalOrdinals.contains(e.key.ordinal) }
//
//        additionalCodec.encodeStart(NbtOps.INSTANCE, additionalUpgrades)
//            .ifSuccess { tag -> nbtTags.put(MekanismEmpoweredCore.SerializationConstants.UPGRADES, tag) }
//            .ifError { e -> MekanismEmpoweredCore.LOGGER.error("Failed to save additional upgrades: {}", e) }
//    }
}
