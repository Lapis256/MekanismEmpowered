@file:Suppress("unused")

import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.provider.Provider


enum class Order {
    NONE, BEFORE, AFTER;
}

enum class Side {
    CLIENT, SERVER, BOTH;
}

enum class DisplayTest {
    MATCH_VERSION, IGNORE_SERVER_VERSION, IGNORE_ALL_VERSION, NONE;
}

enum class DependencyType {
    REQUIRED, OPTIONAL, INCOMPATIBLE, DISCOURAGED;
}

sealed class VersionRange(val versionRange: String) {
    override fun toString(): String = versionRange
}

class Equal(version: String) : VersionRange("[$version]")
class GreaterThan(version: String) : VersionRange("($version,)")
class GreaterThanOrEqual(version: String) : VersionRange("[$version,)")
class LessThan(version: String) : VersionRange("(,$version)")
class LessThanOrEqual(version: String) : VersionRange("(,$version]")
class NotEqual(version: String) : VersionRange("(,$version),($version,)")

class RangeExclusive(min: String, max: String) : VersionRange("($min,$max)")
class RangeInclusive(min: String, max: String) : VersionRange("[$min,$max]")
class RangeInclusiveMin(min: String, max: String) : VersionRange("[$min,$max)")
class RangeInclusiveMax(min: String, max: String) : VersionRange("($min,$max]")

class Or(vararg ranges: VersionRange) : VersionRange(ranges.joinToString(",") { it.versionRange })

fun String.eq() = Equal(this)
fun String.gt() = GreaterThan(this)
fun String.gte() = GreaterThanOrEqual(this)
fun String.lt() = LessThan(this)
fun String.lte() = LessThanOrEqual(this)
fun String.neq() = NotEqual(this)

infix operator fun String.rangeTo(other: String) = RangeInclusive(this, other)
infix operator fun String.rangeUntil(other: String) = RangeInclusiveMin(this, other)
infix fun String.rangeToExclusive(other: String) = RangeExclusive(this, other)
infix fun String.rangeToMax(other: String) = RangeInclusiveMax(this, other)

data class ModDep(
    val id: String,
    val versionRange: VersionRange,
    val type: DependencyType = DependencyType.REQUIRED,
    val ordering: Order = Order.NONE,
    val side: Side = Side.BOTH,
    val reason: String? = null
) {
    companion object {
        fun optional(
            id: String,
            versionRange: VersionRange,
            ordering: Order = Order.NONE,
            side: Side = Side.BOTH,
            reason: String? = null
        ) = ModDep(id, versionRange, DependencyType.OPTIONAL, ordering, side, reason)

        fun incompatible(
            id: String,
            versionRange: VersionRange,
            reason: String
        ) = ModDep(id, versionRange, DependencyType.INCOMPATIBLE, Order.NONE, Side.BOTH, reason)
    }
}

fun buildDeps(
    vararg deps: ModDep,
    modId: String = Constants.Mod.ID,
): String {
    return deps.joinToString(separator = "\n") { (id, versionRange, type, ordering, side, reason) ->
        """
            [[dependencies.$modId]]
            modId = "$id"
            versionRange = "$versionRange"
            type = "$type"
            ordering = "$ordering"
            side = "$side"
        """.trimIndent() + (reason?.let { "\nreason = \"$it\"" } ?: "")
    }
}

fun extractVersionSegments(versionString: String, numberOfSegments: Int = 1) =
    versionString.split(".").take(numberOfSegments).joinToString(".")

fun extractVersionSegments(version: Provider<String>, numberOfSegments: Int = 1) =
    extractVersionSegments(version.get(), numberOfSegments)

fun DependencyHandler.variantOf(dependency: Provider<MinimalExternalModuleDependency>, classifier: String): Provider<MinimalExternalModuleDependency> =
    variantOf(dependency) { classifier(classifier) }
