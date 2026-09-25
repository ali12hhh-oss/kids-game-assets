import java.util.zip.ZipFile

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
        targetSdk = 36
        versionCode = 1
        versionName = "1.0.0"
    }

    sourceSets["main"].assets.directories.add("../assets/characters/KayKit/Mannequin Character/characters")
    sourceSets["main"].assets.directories.add("build/generated/anim-assets")

    // Merge must succeed. Never permit a silent static-model fallback.
    val mergeAnimations = tasks.register<Exec>("mergeAnimations") {
        workingDir = rootProject.projectDir
        commandLine(
            "bash", "-c",
            "npm install --no-save --prefix tools @gltf-transform/core && " +
                "node tools/merge-animations.mjs app/build/generated/anim-assets/Mannequin_Medium_Anim.glb"
        )
        isIgnoreExitValue = false
    }

    val verifyCharacterAssets = tasks.register("verifyCharacterAssets") {
        doLast {
            val navigationFile = file("src/main/java/com/ali12hhh/kidslearning/navigation/AppNavigation.kt")
            check(navigationFile.isFile && navigationFile.readText().contains("Mannequin_Medium_Anim.glb")) {
                "BUILD FAILED: AppNavigation.kt must reference the merged animated model."
            }
            check(!navigationFile.readText().contains("createModelInstance(\"Mannequin_Medium.glb\")")) {
                "BUILD FAILED: Static model fallback is forbidden; it can cause an idle duplicate behind the animated character."
            }

            val generatedModel = layout.buildDirectory.file("generated/anim-assets/Mannequin_Medium_Anim.glb").get().asFile
            check(generatedModel.isFile && generatedModel.length() > 0L) {
                "BUILD FAILED: Merged animated character was not generated."
            }

            val apk = layout.buildDirectory.file("outputs/apk/debug/app-debug.apk").get().asFile
            check(apk.isFile && apk.length() > 0L) { "BUILD FAILED: Debug APK was not produced." }
            ZipFile(apk).use { zip ->
                val entry = zip.getEntry("assets/Mannequin_Medium_Anim.glb")
                check(entry != null && entry.size > 0L) {
                    "BUILD FAILED: Animated character is not packaged in the APK."
                }
                zip.getInputStream(entry).use { input ->
                    val header = ByteArray(4)
                    val read = input.read(header)
                    check(read == 4 && header.contentEquals(byteArrayOf(0x67, 0x6C, 0x54, 0x46))) {
                        "BUILD FAILED: Packaged animated character is not a valid GLB."
                    }
                }
            }
            println("ANIMATED CHARACTER VERIFIED: merged GLB generated and packaged; static fallback is disabled.")
        }
    }

    buildFeatures { compose = true }
    lint {
        abortOnError = true
        warningsAsErrors = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    tasks.configureEach {
        if (name == "assembleDebug") finalizedBy(verifyCharacterAssets)
        if (name.startsWith("merge") && name.endsWith("Assets")) dependsOn(mergeAnimations)
    }
    kotlin {
        compilerOptions { jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17) }
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
    implementation("io.coil-kt:coil-compose:2.7.0")
    implementation("io.github.sceneview:sceneview:2.3.1")
    testImplementation("junit:junit:4.13.2")
    debugImplementation("androidx.compose.ui:ui-tooling")
}
