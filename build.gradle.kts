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
val forgeVersion: String = libs.versions.forge.get()
val kffVersion: String = libs.versions.kotlinForForge.get()

val jdkVersion = Constants.Dev.JDK_VERSION
val jvmVendor = Constants.Dev.JVM_VENDOR


val exportMixin = true
val loadMekExt = false
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

val coreApiSourceSet: SourceSet = sourceSets.create("core.api")

val mainApiSourceSet: SourceSet = sourceSets.create("main.api", Action {
    compileClasspath += coreApiSourceSet.output
    runtimeClasspath += coreApiSourceSet.output
})

val coreSourceSet: SourceSet = sourceSets.create("core", Action {
    compileClasspath += coreApiSourceSet.output
    runtimeClasspath += coreApiSourceSet.output

    ext.set("refMap", "${modId}_core.refmap.json")

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

mixin {
    add(mainSourceSet, "${modId}.refmap.json")
    add(coreSourceSet, "${modId}_core.refmap.json")

    config("${modId}.mixins.json")
    config("${modId}_core.mixins.json")
}

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
        coreCompileOnly(libs.mixinExtrasCommon)
        coreCompileOnly(libs.mixinExtrasForge)

        val coreJarJar by configurations.getting

        coreJarJar(variantOf(libs.mixinExtrasForge, "slim")) {
            version {
                strictly("[$this,)")
                prefer(this.toString())
            }
        }

        coreJarJar(libs.easyNestConfig)

        val coreAnnotationProcessor by configurations.getting

        coreAnnotationProcessor(variantOf(libs.mixin, "processor"))
        coreAnnotationProcessor(libs.mixinExtrasCommon)
    }

    run {
        val coreApiCompileOnly by configurations.getting {
            extendsFrom(configurations.getByName("modCompileOnly"))
        }

        coreApiCompileOnly(libs.kotlinForForge)
        coreApiCompileOnly(variantOf(libs.mekanism, "all"))
    }

    modImplementation(libs.kotlinForForge)
    modImplementation(libs.mekanism)

    modRuntimeOnly(variantOf(libs.mekanism, "generators"))

    modCompileOnly(variantOf(libs.mekanism, "generators"))
    modCompileOnly(libs.mekanismExtras)
    modCompileOnly(libs.evolvedMekanism)
    modCompileOnly(libs.mekanismElements)
    modCompileOnly(libs.evolvedMekanismExtras)
    modCompileOnly(libs.mekanismMoreMachines)

    modRuntimeOnly(libs.jei)

    if (loadAddons) {
        modRuntimeOnly(libs.mekanismExtras)
        modRuntimeOnly(libs.igleelib)
        modRuntimeOnly(libs.evolvedMekanism)
        modRuntimeOnly(libs.mekanismElements)
        modRuntimeOnly(libs.evolvedMekanismExtras)
        modRuntimeOnly(libs.mekanismMoreMachines)
    }

    modImplementation(libs.easyNestConfig)

    annotationProcessor(variantOf(libs.mixin, "processor"))
    annotationProcessor(libs.mixinExtrasCommon)
    compileOnly(libs.mixinExtrasCommon)
    implementation(libs.mixinExtrasForge)
}

legacyForge {
    version = "$mcVersion-$forgeVersion"

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
            systemProperty("forge.enabledGameTestNamespaces", modId)
            jvmArgument("-Dmixin.debug=true")
            jvmArgument("-Dmixin.debug.export=$exportMixin")
            jvmArgument("-XX:+AllowEnhancedClassRedefinition")
        })

        create("server", Action {
            server()
            gameDirectory.set(rootProject.file("run-server"))
            programArgument("--nogui")
            systemProperty("forge.enabledGameTestNamespaces", modId)
            jvmArgument("-Dmixin.debug=true")
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
            sourceSet(coreSourceSet)
            sourceSet(coreApiSourceSet)
        })
    }

    ideSyncTask(generateModMetadata)
    ideSyncTask(generateCoreModMetadata)
}

fun setupMetaDataTask(modId: String, modName: String, task: TaskProvider<ProcessResources>, deps: List<ModDep>) {
    task {
        val replaceProperties = mutableMapOf(
            "version" to version,
            "group" to project.group,
            "minecraft_version" to mcVersion,
            "mod_loader" to "javafml",
            "mod_loader_version_range" to "[${extractVersionSegments(forgeVersion)},)",
            "mod_name" to modName,
            "mod_author" to Constants.Mod.AUTHOR,
            "mod_id" to modId,
            "license" to Constants.Mod.LICENSE,
            "description" to Constants.Mod.DESCRIPTION,
            "display_url" to Constants.Mod.REPOSITORY_URL,
            "issue_tracker_url" to Constants.Mod.ISSUE_TRACKER_URL,
            "dependencies" to buildDeps(*deps.toTypedArray(), modId = modId),
        )

        inputs.properties(replaceProperties)
        filter<ReplaceTokens>("beginToken" to "\${", "endToken" to "}", "tokens" to replaceProperties)
        from(rootProject.file("src/templates"))
        into("build/generated/sources/$modId")
    }
}

fun setupJarTask(modName: String, modID: String, task: TaskProvider<Jar>, sourceSet: SourceSet, additionalSourceSets: List<SourceSet> = emptyList()) =
    setupJarTask(modName, modID, false, task, null, sourceSet, additionalSourceSets)

fun setupJarTask(
    modName: String,
    id: String,
    renameFile: Boolean,
    task: TaskProvider<Jar>,
    classifier: String? = null,
    sourceSet: SourceSet,
    additionalSourceSets: List<SourceSet> = emptyList()
) {
    val cleanModName = modName.replace(" ", "").replace(":", "")
    val newName = "$cleanModName-$mcVersion-${project.version}.jar"

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
                "MixinConfigs" to "$id.mixins.json"
            )
        }

        archiveClassifier.set(classifier)
        if (renameFile) {
            archiveFileName.set(newName)
        }
        from(sourceSet.output, additionalSourceSets.map { it.output }.toTypedArray())
    }

    if (sourceSet.name != "main") {
        obfuscation {
            reobfuscate(task, sourceSet) {
                dependsOn("compileJava")

                if (renameFile) {
                    archiveFileName.set(newName)
                }
            }
        }
    }
}

val baseDependencies = listOf(
    ModDep("forge", libs.versions.forge.get()),
    ModDep("minecraft", mcVersion),
    ModDep("kotlinforforge", kffVersion),
    ModDep("mekanism", "10.4.16", ordering = Order.AFTER),
)
val mainModDependencies = baseDependencies.toMutableList().apply {
    add(ModDep("mekanism_empowered_core", Constants.Mod.VERSION, ordering = Order.AFTER))
    add(ModDep("evolvedmekanism", "1.2.1", ordering = Order.AFTER, mandatory = false))
    add(ModDep("mekanismelements", "2.3", ordering = Order.AFTER, mandatory = false))
    add(ModDep("mekanism_extras", "1.20.1-1.4.6", ordering = Order.AFTER, mandatory = false))
    add(ModDep("mekmm", "1.20.1-1.0.1", ordering = Order.AFTER, mandatory = false))
    add(ModDep("emextras", "1.3.3", ordering = Order.AFTER, mandatory = false))
    add(ModDep("mekanismtweaks", "999.999.999-INCOMPATIBLE", false))
    add(ModDep("mekanismupgradesreborn", "999.999.999-INCOMPATIBLE", false))
}

setupMetaDataTask(modId, Constants.Mod.NAME, generateModMetadata, mainModDependencies)
setupMetaDataTask("${modId}_core", "${Constants.Mod.NAME} Core", generateCoreModMetadata, baseDependencies)

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
        dependsOn(
            generateModMetadata,
            generateCoreModMetadata
        )
    }

    named<Jar>("sourcesJar") {
        dependsOn(classes, "mainApiClasses", "coreClasses", "coreApiClasses")

        from(
            mainApiSourceSet.kotlin,
            coreSourceSet.kotlin,
            coreApiSourceSet.kotlin,
        )
    }

    setupJarTask(Constants.Mod.NAME, Constants.Mod.ID, jar, mainSourceSet, listOf(mainApiSourceSet))

    setupJarTask(
        Constants.Mod.NAME,
        Constants.Mod.ID,
        false,
        register<Jar>("apiJar"),
        "api",
        mainApiSourceSet
    )

    setupJarTask(
        "${Constants.Mod.NAME} Core",
        Constants.Mod.ID + "_core",
        true,
        register<Jar>("coreJar"),
        "core",
        coreSourceSet,
        listOf(coreApiSourceSet)
    )

    setupJarTask(
        "${Constants.Mod.NAME} Core",
        Constants.Mod.ID + "_core",
        false,
        register<Jar>("coreApiJar"),
        "core-api",
        coreApiSourceSet
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
            addModLoader("Forge")
            addJavaVersion("Java $jdkVersion")

            addRequirement("kotlin-for-forge", "mekanism")
        }

        upload(Constants.Publisher.CURSEFORGE_MAIN_ID, named("reobfJar")) {
            setShared()

            addRequirement("mekanism-empowered-core")
            addOptional("mekanism-extras")
            addOptional("evolved-mekanism")
            addOptional("mekanism-elements")
            addOptional("evolved-mekanism-extras")
            addOptional("mekanism-more-machine")
            addIncompatibility("mekanism-tweaks")
            addIncompatibility("mekanism-upgrades-reborn")
        }

        upload(Constants.Publisher.CURSEFORGE_CORE_ID, named("reobfCoreJar")) {
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

                setArtifacts(listOf(tasks["reobfJar"], tasks["sourcesJar"], tasks["reobfApiJar"], tasks["reobfCoreJar"], tasks["reobfCoreApiJar"]))
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
