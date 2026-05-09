package dev.lapis256.mekanism_empowered.common.factory

import dev.lapis256.mekanism_empowered.common.MekanismEmpowered
import dev.lapis256.mekanism_empowered.core.common.util.AdditionalUpgradeUtil.addDeferredSupportedForAll
import mekanism.api.Upgrade
import mekanism.common.content.blocktype.BlockType
import mekanism.common.content.blocktype.FactoryType
import mekanism.common.registries.MekanismBlockTypes
import mekanism.common.util.EnumUtils
import net.minecraft.resources.ResourceLocation


@JvmInline
internal value class FactoryTypeKey(val id: ResourceLocation) {
    override fun toString() = id.toString()

    companion object {
        fun of(namespace: String, path: String) =
            FactoryTypeKey(ResourceLocation.fromNamespaceAndPath(namespace, path))
    }
}

internal object MekanismFactoryTypeKeys {
    val SMELTING = FactoryTypeKey.of("mekanism", "smelting")
    val ENRICHING = FactoryTypeKey.of("mekanism", "enriching")
    val CRUSHING = FactoryTypeKey.of("mekanism", "crushing")
    val COMPRESSING = FactoryTypeKey.of("mekanism", "compressing")
    val COMBINING = FactoryTypeKey.of("mekanism", "combining")
    val PURIFYING = FactoryTypeKey.of("mekanism", "purifying")
    val INJECTING = FactoryTypeKey.of("mekanism", "injecting")
    val INFUSING = FactoryTypeKey.of("mekanism", "infusing")
    val SAWING = FactoryTypeKey.of("mekanism", "sawing")
}

internal val mekanismFactoryTypeSuppliers: Map<FactoryTypeKey, () -> FactoryType> = mapOf(
    MekanismFactoryTypeKeys.SMELTING to { FactoryType.SMELTING },
    MekanismFactoryTypeKeys.ENRICHING to { FactoryType.ENRICHING },
    MekanismFactoryTypeKeys.CRUSHING to { FactoryType.CRUSHING },
    MekanismFactoryTypeKeys.COMPRESSING to { FactoryType.COMPRESSING },
    MekanismFactoryTypeKeys.COMBINING to { FactoryType.COMBINING },
    MekanismFactoryTypeKeys.PURIFYING to { FactoryType.PURIFYING },
    MekanismFactoryTypeKeys.INJECTING to { FactoryType.INJECTING },
    MekanismFactoryTypeKeys.INFUSING to { FactoryType.INFUSING },
    MekanismFactoryTypeKeys.SAWING to { FactoryType.SAWING },
)

internal class FactoryBlockResolver<TIER, TYPE>(
    private val name: String,
    private val types: Map<FactoryTypeKey, () -> TYPE>,
    private val tiers: () -> Iterable<TIER>,
    private val resolver: (TIER, TYPE) -> BlockType?,
) {
    fun resolve(key: FactoryTypeKey): List<BlockType> {
        val provider = types[key] ?: return emptyList()
        val type = runOrLog("Failed to get factory type for $key in $name", provider) ?: return emptyList()
        val tiers = runOrLog("Failed to get factory tiers for $key in $name", tiers) ?: return emptyList()

        return tiers.mapNotNull { tier ->
            runOrLog("Failed to resolve factory block for $key and tier $tier in $name") {
                resolver(tier, type)
            }
        }
    }

    private inline fun <T> runOrLog(message: String, block: () -> T): T? =
        runCatching(block)
            .onFailure { MekanismEmpowered.LOGGER.error(message, it) }
            .getOrNull()
}

private val mekanismFactoryResolver = FactoryBlockResolver(
    name = "mekanism",
    types = mekanismFactoryTypeSuppliers,
    tiers = { EnumUtils.FACTORY_TIERS.asIterable() },
    resolver = { tier, type -> MekanismBlockTypes.getFactory(tier, type) },
)

internal object FactoryBlockResolverRegistry {
    private val resolvers = mutableSetOf<FactoryBlockResolver<*, *>>()

    init {
        register(mekanismFactoryResolver)
    }

    fun register(resolver: FactoryBlockResolver<*, *>) {
        if (resolver !in resolvers) {
            resolvers += resolver
        }
    }

    fun resolve(key: FactoryTypeKey): List<BlockType> =
        resolvers.flatMap { it.resolve(key) }
}

/**
 * Adds supported upgrades later to every factory block resolved from the given factory type key.
 *
 * A factory type key can resolve to multiple [BlockType] instances across tiers and integrations,
 * so the block resolution is deferred until integration block registries are ready.
 */
internal fun addDeferredSupportedForFactory(key: FactoryTypeKey, vararg upgrades: Upgrade) {
    addDeferredSupportedForAll({ FactoryBlockResolverRegistry.resolve(key) }, *upgrades)
}
