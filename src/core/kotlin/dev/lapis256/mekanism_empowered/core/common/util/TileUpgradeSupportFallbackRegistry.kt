package dev.lapis256.mekanism_empowered.core.common.util

import mekanism.api.Upgrade
import mekanism.common.tile.base.TileEntityMekanism


typealias FallbackSupportedUpgradeProvider = (tile: TileEntityMekanism) -> Set<Upgrade>
typealias FallbackUnsupportedUpgradePredicate = (tile: TileEntityMekanism, upgrade: Upgrade) -> Boolean

/**
 * Registry for fallback upgrade support rules that need a TileEntity instance.
 *
 * Prefer [AdditionalUpgradeUtil.addSupported] when the target BlockType can be adjusted directly.
 * Use this fallback only for support rules that cannot be represented on the BlockType itself, or
 * when the decision depends on the concrete TileEntity. Supported providers add upgrades, while
 * unsupported predicates remove upgrades from the final fallback result.
 *
 * Provider and predicate failures are ignored for that entry so the remaining rules can continue.
 */
object TileUpgradeSupportFallbackRegistry {
    private val supportedUpgradeProviders: MutableSet<FallbackSupportedUpgradeProvider> = mutableSetOf()
    private val unsupportedUpgradePredicates: MutableSet<FallbackUnsupportedUpgradePredicate> = mutableSetOf()

    fun registerSupportedUpgradeProvider(provider: FallbackSupportedUpgradeProvider) {
        supportedUpgradeProviders += provider
    }

    fun registerUnsupportedUpgradePredicate(predicate: FallbackUnsupportedUpgradePredicate) {
        unsupportedUpgradePredicates += predicate
    }

    @JvmStatic
    fun collectSupportedUpgrades(tile: TileEntityMekanism): List<Upgrade> =
        supportedUpgradeProviders.flatMap {
            runCatching { it(tile) }.getOrDefault(emptySet())
        }

    @JvmStatic
    fun isUnsupportedUpgrade(tile: TileEntityMekanism, upgrade: Upgrade): Boolean =
        unsupportedUpgradePredicates.any {
            runCatching { it(tile, upgrade) }.getOrDefault(false)
        }
}
