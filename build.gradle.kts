import net.darkhax.curseforgegradle.TaskPublishCurseForge
import net.darkhax.curseforgegradle.UploadArtifact
import net.neoforged.moddevgradle.internal.RunGameTask
import org.apache.tools.ant.filters.ReplaceTokens
import org.slf4j.event.Level
import java.text.SimpleDateFormat
import java.util.*
import net.darkhax.curseforgegradle.Constants as CFGConstants


plugins {
    id("java")
    id("java-library")
    id("idea")
    id("maven-publish")

    id("localRuntime")

    alias(libs.plugins.kotlin)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.moddev)
    alias(libs.plugins.curseForge)
    alias(libs.plugins.modrinth)
}

val modId = Constants.Mod.ID
val mcVersion: String = libs.versions.minecraft.get()
val kffVersion: String = libs.versions.kotlinForForge.get()

val jdkVersion = Constants.Dev.JDK_VERSION
val jvmVendor = Constants.Dev.JVM_VENDOR


val exportMixin = true
val loadAddons = true


base {
    archivesName = "${rootProject.name}-$mcVersion"
    version = Constants.Mod.VERSION
    group = Constants.Mod.GROUP
}

repositories {
    mavenLocal()
    maven {
        name = "Kotlin for Forge"
        url = uri("https://thedarkcolour.github.io/KotlinForForge/")
    }
    maven {
        name = "Mekanism / JEI"
        url = uri("https://modmaven.dev/")
    }
    maven {
        name = "Curse Maven"
        url = uri("https://cursemaven.com")
    }
    maven {
        name = "R2"
        url = uri("https://maven.lapis256.dev")
    }
    mavenCentral()
}

val generateModMetadata by tasks.registering(ProcessResources::class)
val generateCoreModMetadata by tasks.registering(ProcessResources::class)

val coreApiSourceSet: SourceSet = sourceSets.create("core.api", Action {})

val mainApiSourceSet: SourceSet = sourceSets.create("main.api", Action {
    compileClasspath += coreApiSourceSet.output
    runtimeClasspath += coreApiSourceSet.output
})

val coreSourceSet: SourceSet = sourceSets.create("core", Action {
    compileClasspath += coreApiSourceSet.output
    runtimeClasspath += coreApiSourceSet.output

    resources {
        srcDirs(
            generateCoreModMetadata.get().outputs.files
        )
        exclude("**/.cache")
    }
})

val mainSourceSet: SourceSet = sourceSets.getByName("main") {
    compileClasspath += mainApiSourceSet.output + coreApiSourceSet.output + coreSourceSet.output
    runtimeClasspath += mainApiSourceSet.output + coreApiSourceSet.output + coreSourceSet.output

    resources {
        srcDirs(
            "src/generated/resources",
            generateModMetadata.get().outputs.files
        )
        exclude("**/.cache")
    }
}

val dataSourceSet: SourceSet = sourceSets.create("data", Action {
    compileClasspath += coreSourceSet.output + mainSourceSet.compileClasspath + mainSourceSet.output
    runtimeClasspath += coreSourceSet.output + mainSourceSet.runtimeClasspath + mainSourceSet.output
})

dependencies {
    run {
        val mainApiCompileOnly by configurations.getting

        mainApiCompileOnly(variantOf(libs.mekanism, "api"))
    }

    run {
        val coreCompileOnly by configurations.getting

        coreCompileOnly(libs.mekanism)
        coreCompileOnly(libs.kotlinForForge)
        coreCompileOnly(libs.easyNestConfig)

        val coreJarJar by configurations.getting
        coreJarJar(libs.easyNestConfig) {
            version {
                strictly("[$this,)")
                prefer(this.toString())
            }
        }
    }

    run {
        val coreApiCompileOnly by configurations.getting

        coreApiCompileOnly(libs.kotlinForForge)
        coreApiCompileOnly(variantOf(libs.mekanism, "api"))
    }

    implementation(libs.kotlinForForge)
    implementation(libs.mekanism)
    implementation(variantOf(libs.mekanism, "generators"))

    compileOnly(variantOf(libs.mekanism, "all"))

    compileOnly(libs.mekanismExtras)
    compileOnly(libs.mekanismElements)
    compileOnly(libs.igleelib)
    compileOnly(libs.evolvedMekanism)
    compileOnly(libs.mekanismMoreMachine)
    compileOnly(libs.evolvedMekanismExtras)
    compileOnly(libs.chemlibMekanized)

    localRuntime(libs.jei)

    if (loadAddons) {
        localRuntime(libs.mekanismElements)
        localRuntime(libs.mekanismExtras)
        localRuntime(libs.igleelib)
        localRuntime(libs.evolvedMekanism)
        localRuntime(libs.mekanismMoreMachine)
        localRuntime(libs.evolvedMekanismExtras)
//        localRuntime(libs.chemlibMekanized)
    }

    implementation(libs.easyNestConfig)
}

neoForge {
    enable {
        version = libs.versions.neoforge.get()
        this.isDisableRecompilation = System.getenv("CI") == "true"
    }

    addModdingDependenciesTo(coreApiSourceSet)
    addModdingDependenciesTo(coreSourceSet)
    addModdingDependenciesTo(mainApiSourceSet)
    addModdingDependenciesTo(dataSourceSet)

    validateAccessTransformers = true

    accessTransformers {
        val atFile = rootProject.file("src/core/resources/META-INF/accesstransformer.cfg").takeIf(File::exists) ?: return@accessTransformers
        from(atFile)
        publish(atFile)
    }

    parchment {
        mappingsVersion = libs.versions.parchmentmc.get()
        minecraftVersion = mcVersion
    }

    runs {
        create("client", Action {
            client()
            gameDirectory.set(rootProject.file("run"))
            systemProperty("neoforge.enabledGameTestNamespaces", modId)
            jvmArgument("-Dmixin.debug.export=$exportMixin")
            jvmArgument("-XX:+AllowEnhancedClassRedefinition")
        })

        create("server", Action {
            server()
            gameDirectory.set(rootProject.file("run-server"))
            programArgument("--nogui")
            systemProperty("neoforge.enabledGameTestNamespaces", modId)
            jvmArgument("-Dmixin.debug.export=$exportMixin")
            jvmArgument("-XX:+AllowEnhancedClassRedefinition")
        })

        create("data", Action {
            data()
            sourceSet = dataSourceSet
            gameDirectory.set(rootProject.file("run-data"))
            programArguments.addAll(
                "--mod",
                modId,
                "--all",
                "--output",
                file("src/generated/resources/").absolutePath,
                "--existing",
                file("src/main/resources/").absolutePath
            )
        })

        configureEach {
            systemProperty("forge.logging.markers", "REGISTRIES")

            logLevel = Level.DEBUG
        }
    }

    mods {
        create(modId, Action {
            sourceSet(mainSourceSet)
            sourceSet(mainApiSourceSet)
            sourceSet(dataSourceSet)
        })
        create("${modId}_core", Action {
            sourceSet(coreApiSourceSet)
            sourceSet(coreSourceSet)
        })
    }

    ideSyncTask(generateModMetadata)
    ideSyncTask(generateCoreModMetadata)
}

fun setupMetaDataTask(modId: String, modName: String, task: TaskProvider<ProcessResources>, deps: List<ModDep>, at: String? = null) {
    task {
        val replaceProperties: MutableMap<String, String> = mutableMapOf(
            "version" to version.toString(),
            "group" to project.group.toString(),
            "minecraft_version" to mcVersion,
            "mod_loader" to "kotlinforforge",
            "mod_loader_version_range" to GreaterThanOrEqual(kffVersion).toString(),
            "mod_name" to modName,
            "mod_author" to Constants.Mod.AUTHOR,
            "mod_id" to modId,
            "license" to Constants.Mod.LICENSE,
            "description" to Constants.Mod.DESCRIPTION,
            "display_url" to Constants.Mod.REPOSITORY_URL,
            "issue_tracker_url" to Constants.Mod.ISSUE_TRACKER_URL,
            "access_transformers" to "",
            "dependencies" to buildDeps(*deps.toTypedArray(), modId = modId),
        )

        if (at != null) {
            replaceProperties["access_transformers"] = "accessTransformers = [ { file = \"$at\" } ]"
        }

        inputs.properties(replaceProperties)
        filter<ReplaceTokens>("beginToken" to "\${", "endToken" to "}", "tokens" to replaceProperties)
        from(rootProject.file("src/templates"))
        into("build/generated/sources/$modId")
    }
}

fun setupJarTask(modName: String, task: TaskProvider<Jar>, vararg sourceSets: SourceSetOutput) = setupJarTask(modName, false, task, null, *sourceSets)
fun setupJarTask(modName: String, renameFile: Boolean, task: TaskProvider<Jar>, classifier: String? = null, vararg sourceSets: SourceSetOutput) {
    val cleanModName = modName.replace(" ", "").replace(":", "")
    task {
        manifest {
            attributes(
                "Specification-Title" to modName,
                "Specification-Vendor" to Constants.Mod.AUTHOR,
                "Specification-Version" to version,
                "Implementation-Title" to cleanModName,
                "Implementation-Version" to version,
                "Implementation-Vendor" to Constants.Mod.AUTHOR,
                "Implementation-Timestamp" to SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(Date()),
                "Timestamp" to System.currentTimeMillis(),
                "Built-On-Java" to "${System.getProperty("java.vm.VERSION")} (${System.getProperty("java.vm.vendor")})",
                "Built-On-Minecraft" to mcVersion,
            )
        }

        archiveClassifier.set(classifier)
        if (renameFile) {
            archiveFileName.set("$cleanModName-$mcVersion-${project.version}.jar")
        }
        from(*sourceSets)
    }
}

val baseDependencies = listOf(
    ModDep("neoforge", libs.versions.neoforge.get() ..< "21.2"),
    ModDep("minecraft", mcVersion.eq()),
    ModDep("kotlinforforge", kffVersion.gte()),
    ModDep("mekanism", "10.7.18".gte(), ordering = Order.AFTER),
)
val mainModDependencies = baseDependencies.toMutableList().apply {
    add(ModDep.optional("mekanism_empowered_core", Constants.Mod.VERSION.eq(), ordering = Order.AFTER))
    add(ModDep.optional("mekanism_extras", "1.3.3".gte()))
    add(ModDep.optional("evolvedmekanism", "1.2.1-fix2".gte()))
    add(ModDep.optional("mekmm", "1.3.1".gte()))
    add(ModDep.optional("emextras", "1.1.1".gte()))
    add(ModDep.incompatible("mekanism_unleashed", "0.0.0".gte(), "Incompatible Mixins"))
}

setupMetaDataTask(modId, Constants.Mod.NAME, generateModMetadata, mainModDependencies)
setupMetaDataTask("${modId}_core", "${Constants.Mod.NAME} Core", generateCoreModMetadata, baseDependencies, at = "accesstransformer.cfg")

tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.release = jdkVersion
    }

    java {
        withSourcesJar()
        toolchain {
            languageVersion = JavaLanguageVersion.of(jdkVersion)
            vendor = jvmVendor
        }
        JavaVersion.toVersion(jdkVersion).let {
            sourceCompatibility = it
            targetCompatibility = it
        }
    }

    kotlin {
        jvmToolchain(jdkVersion)

        compilerOptions {
            freeCompilerArgs.add("-Xjvm-default=all")
        }
    }

    processResources {
        dependsOn(generateModMetadata, generateCoreModMetadata)
    }

    named<Jar>("sourcesJar") {
        dependsOn(classes, "mainApiClasses", "coreClasses", "coreApiClasses")

        from(
            mainApiSourceSet.kotlin,
            coreSourceSet.kotlin,
            coreApiSourceSet.kotlin,
        )
    }

    setupJarTask(Constants.Mod.NAME, jar, mainSourceSet.output, mainApiSourceSet.output)

    setupJarTask(
        Constants.Mod.NAME,
        false,
        register<Jar>("apiJar"),
        "api",
        mainApiSourceSet.output,
    )

    setupJarTask(
        "${Constants.Mod.NAME} Core",
        true,
        register<Jar>("coreJar"),
        "core",
        coreSourceSet.output,
        coreApiSourceSet.output,
    )

    setupJarTask(
        "${Constants.Mod.NAME} Core",
        false,
        register<Jar>("coreApiJar"),
        "core-api",
        coreApiSourceSet.output,
    )

    build {
        dependsOn("apiJar")
        dependsOn("coreJar")
        dependsOn("coreApiJar")
    }

    withType<RunGameTask>().configureEach {
        javaLauncher.set(project.javaToolchains.launcherFor {
            languageVersion.set(JavaLanguageVersion.of(jdkVersion))
            vendor.set(jvmVendor)
        })
        standardInput = System.`in`
    }

    withType<Jar>().configureEach {
        from(rootProject.file("LICENSE")) {
            rename { "LICENSE_${Constants.Mod.ID}" }
        }

        destinationDirectory.set(rootProject.layout.buildDirectory.dir("libs"))
    }

    register<TaskPublishCurseForge>("curseforge") {
        group = "publishing"
        description = "Upload to CurseForge"
        apiToken = System.getenv("CURSE_TOKEN")
        debugMode = System.getenv("PUBLISHER_DEBUG") == "true"

        disableVersionDetection()

        fun UploadArtifact.setShared() {
            releaseType = CFGConstants.RELEASE_TYPE_RELEASE
            changelog = System.getenv("CHANGELOG") ?: "No changelog provided"
            changelogType = CFGConstants.CHANGELOG_MARKDOWN
            displayName = "[$mcVersion] v${project.version}"
            addGameVersion(mcVersion)
            addEnvironment("Client", "Server")
            addModLoader("NeoForge")
            addJavaVersion("Java $jdkVersion")

            addRequirement("kotlin-for-forge", "mekanism")
        }

        upload(Constants.Publisher.CURSEFORGE_MAIN_ID, jar) {
            setShared()

            addRequirement("mekanism-empowered-core")
            addOptional("mekanism-extras")
            addOptional("evolved-mekanism")
            addOptional("mekansim-more-machine")
            addOptional("evolved-mekanism-extras")

            addIncompatibility("mekanism-unleashed")
        }

        upload(Constants.Publisher.CURSEFORGE_CORE_ID, named("coreJar")) {
            setShared()
        }
    }
}

idea {
    module {
        isDownloadJavadoc = true
        isDownloadSources = true
    }
}

run {
    val r2AccessKey = project.findProperty("r2_access_key") ?: System.getenv("R2_ACCESS_KEY") ?: return@run
    val r2SecretKey = project.findProperty("r2_secret_key") ?: System.getenv("R2_SECRET_KEY") ?: return@run

    publishing {
        publications {
            register<MavenPublication>("maven") {
                from(components["java"])
                version = "$mcVersion-${project.version}"

                setArtifacts(listOf(tasks["jar"], tasks["sourcesJar"], tasks["apiJar"], tasks["coreJar"], tasks["coreApiJar"]))
                artifact(layout.buildDirectory.file("copyAccessTransformersPublications/0-accesstransformer.cfg")) {
                    classifier = "accesstransformer"
                    extension = "cfg"
                }
            }
        }
        repositories {
            mavenLocal()
            maven {
                name = "R2"
                url = uri("s3://maven")
                credentials(AwsCredentials::class) {
                    accessKey = r2AccessKey.toString()
                    secretKey = r2SecretKey.toString()
                }
            }
        }
    }
}
