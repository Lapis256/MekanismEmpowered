import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderConvertible


@Suppress("unused")
enum class Order {
    NONE, BEFORE, AFTER;
}

@Suppress("unused")
enum class Side {
    CLIENT, SERVER, BOTH;
}

@Suppress("unused")
data class ModDep(
    val id: String,
    val version: String,
    val mandatory: Boolean = true,
    val ordering: Order = Order.NONE,
    val side: Side = Side.BOTH
) {
    init {
        if (version.isEmpty()) {
            throw IllegalArgumentException("Version cannot be empty")
        }
    }
}

@Suppress("unused")
fun buildDeps(
    vararg deps: ModDep,
    modId: String = Constants.Mod.ID,
): String {
    return deps.joinToString(separator = "\n") { (id, version, mandatory, ordering, side) ->
        """
            [[dependencies.$modId]]
            modId = "$id"
            versionRange = "[$version,)"
            mandatory = $mandatory
            ordering = "$ordering"
            side = "$side"
        """.trimIndent()
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

@Suppress("unused")
fun DependencyHandler.variantOf(dependency: ProviderConvertible<MinimalExternalModuleDependency>, classifier: String) =
    variantOf(dependency) { classifier(classifier) }
