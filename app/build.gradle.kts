plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.ali12hhh.kidslearning"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.ali12hhh.kidslearning"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"
    }

    sourceSets["main"].assets.directories.add("assets/characters/KayKit/Mannequin Character/characters")

    // Build gate: fail the build if the required 3D asset is missing,
    // not referenced by the app, or not packaged into the APK.
    val verifyCharacterAssets = tasks.register("verifyCharacterAssets") {
        doLast {
            val assetRelativePath =
                "assets/characters/KayKit/Mannequin Character/characters/Mannequin_Medium.glb"
            val assetFile = file(assetRelativePath)
            check(assetFile.isFile && assetFile.length() > 0L) {
                "BUILD FAILED: Required 3D asset is missing or empty: $assetRelativePath"
            }

            val navigationFile =
                file("src/main/java/com/ali12hhh/kidslearning/navigation/AppNavigation.kt")
            check(navigationFile.isFile) {
                "BUILD FAILED: AppNavigation.kt was not found; cannot verify 3D asset reference."
            }
            check(navigationFile.readText().contains("Mannequin_Medium.glb")) {
                "BUILD FAILED: Mannequin_Medium.glb is not referenced by AppNavigation.kt."
            }

            val apk = layout.buildDirectory
                .file("outputs/apk/debug/app-debug.apk")
                .get()
                .asFile
            check(apk.isFile && apk.length() > 0L) {
                "BUILD FAILED: Debug APK was not produced before asset verification."
            }

            java.util.zip.ZipFile(apk).use { zip ->
                val packagedPath = "assets/Mannequin_Medium.glb"
                val entry = zip.getEntry(packagedPath)
                check(entry != null && entry.size > 0L) {
                    "BUILD FAILED: Mannequin_Medium.glb is not packaged in the APK at $packagedPath"
                }
            }

            println("3D ASSET VERIFICATION PASSED: Mannequin_Medium.glb is present, referenced, and packaged.")
        }
    }

    buildFeatures { compose = true }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    tasks.named("assembleDebug") {
        finalizedBy(verifyCharacterAssets)
    }

    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }
}
dependencies {
    implementation(platform("androidx.compose:compose-bom:2024.12.01"))
    implementation("androidx.activity:activity-compose:1.10.0")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.navigation:navigation-compose:2.8.5")
    implementation("io.github.sceneview:sceneview:2.3.1")
    testImplementation("junit:junit:4.13.2")
    debugImplementation("androidx.compose.ui:ui-tooling")
}
