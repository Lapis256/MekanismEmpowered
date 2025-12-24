import org.gradle.jvm.toolchain.JvmVendorSpec


object Constants {
    object Mod {
        const val ID = "mekanism_empowered"
        const val NAME = "Mekanism: Empowered"
        const val DESCRIPTION = "An addon mod that empowers Mekanism."
        const val LICENSE = "MIT"
        const val VERSION = "21.1.1.1.0" // <MC Major>.<MC Minor>.<Mod Major>.<Mod Minor>.<Mod Patch>
        const val GROUP = "dev.lapis256"
        const val AUTHOR = "Lapis256"
        const val REPOSITORY_URL = "https://github.com/Lapis256/MekanismEmpowered"
        const val ISSUE_TRACKER_URL = "$REPOSITORY_URL/issues"
    }

    object Publisher {
        const val CURSEFORGE_MAIN_ID = "1256390"
        const val CURSEFORGE_CORE_ID = "1256399"
    }

    object Dev {
        const val JDK_VERSION = 21
        @Suppress("UnstableApiUsage")
        val JVM_VENDOR: JvmVendorSpec = JvmVendorSpec.JETBRAINS
    }
}
