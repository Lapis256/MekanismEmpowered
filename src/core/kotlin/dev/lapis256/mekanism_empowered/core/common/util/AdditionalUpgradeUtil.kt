package dev.lapis256.mekanism_empowered.core.common.util

import dev.lapis256.mekanism_empowered.core.common.MekanismEmpoweredCore
import dev.lapis256.mekanism_empowered.core.mixin_impl.ducks.hasUpgradeSupportOverride
import mekanism.api.Upgrade
import mekanism.common.block.attribute.AttributeUpgradeSupport
import mekanism.common.content.blocktype.BlockType
import java.util.function.Supplier


object AdditionalUpgradeUtil {
    /**
     * Adds additional supported upgrades to the given block type.
     *
     * This method appends the specified upgrades to the block's existing list
     * of supported upgrades via the [AttributeUpgradeSupport] attribute.
     *
     * @param blockType The block type to modify.
     * @param upgrades  The upgrades to add.
     */
    @JvmStatic
    fun addSupported(blockType: BlockType, vararg upgrades: Upgrade) {
        val attribute = blockType.get(AttributeUpgradeSupport::class.java) ?: return
        val supportedUpgrades = arrayOf(*attribute.supportedUpgrades.toTypedArray(), *upgrades)
        blockType.add(AttributeUpgradeSupport.create(*supportedUpgrades))
        blockType.hasUpgradeSupportOverride = true
    }

    private val deferredSupportedUpgradeRegistrations = mutableListOf<Pair<Supplier<List<BlockType>>, Array<out Upgrade>>>()

    /**
     * Adds additional supported upgrades to the supplied block type later.
     *
     * This method stores the specified upgrades until deferred upgrade registrations are applied.
     *
     * @param supplier The supplier of the block type.
     * @param upgrades The upgrades to add.
     */
    @JvmStatic
    fun addDeferredSupported(supplier: Supplier<BlockType?>, vararg upgrades: Upgrade) {
        addDeferredSupportedForAll({ listOfNotNull(supplier.get()) }, *upgrades)
    }

    /**
     * Adds additional supported upgrades to all supplied block types later.
     *
     * Use this when a single logical target, such as a factory type, resolves to multiple
     * [BlockType] instances after integration block registries are ready.
     *
     * @param supplier The supplier of block types to modify.
     * @param upgrades The upgrades to add.
     */
    @JvmStatic
    fun addDeferredSupportedForAll(supplier: Supplier<List<BlockType>>, vararg upgrades: Upgrade) {
        deferredSupportedUpgradeRegistrations.add(supplier to upgrades)
    }

    internal fun applyDeferredSupportedUpgrades() {
        deferredSupportedUpgradeRegistrations.forEach { (supplier, upgrades) ->
            val type = runCatching { supplier.get() }
                .onFailure { MekanismEmpoweredCore.LOGGER.error("Failed to get deferred block types", it) }
                .getOrNull() ?: return@forEach

            type.forEach {
                addSupported(it, *upgrades)
            }
        }
    }
}
