import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.provider.Provider


@Suppress("unused")
enum class Order {
    NONE, BEFORE, AFTER;
}

@Suppress("unused")
enum class Side {
    CLIENT, SERVER, BOTH;
}

@Suppress("unused")
enum class DisplayTest {
    MATCH_VERSION, IGNORE_SERVER_VERSION, IGNORE_ALL_VERSION, NONE;
}

@Suppress("unused")
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

@Suppress("unused")
data class ModDep(
    val id: String,
    val versionRange: VersionRange,
    val type: DependencyType = DependencyType.REQUIRED,
    val ordering: Order = Order.NONE,
    val side: Side = Side.BOTH,
    val reason: String? = null
)

@Suppress("unused")
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

@Suppress("unused")
fun extractVersionSegments(versionString: String, numberOfSegments: Int = 1) =
    versionString.split(".").take(numberOfSegments).joinToString(".")

@Suppress("unused")
fun extractVersionSegments(version: Provider<String>, numberOfSegments: Int = 1) =
    extractVersionSegments(version.get(), numberOfSegments)

@Suppress("unused")
fun DependencyHandler.variantOf(dependency: Provider<MinimalExternalModuleDependency>, classifier: String) =
    variantOf(dependency) { classifier(classifier) }
