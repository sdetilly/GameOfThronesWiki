import com.google.devtools.ksp.gradle.KspAATask
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.ksp)
    alias(libs.plugins.androidx.room)
    alias(libs.plugins.kover)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    sourceSets {
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.ktor.client.android)
            implementation(libs.androidx.room.sqlite.wrapper)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)

            // Ktor
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)

            // Kotlinx serialization
            implementation(libs.kotlinx.serialization.json)

            // Koin
            implementation(libs.bundles.koin)

            // Image loading
            implementation(libs.coil.compose)
            implementation(libs.coil.network.ktor)

            // Room
            implementation(libs.androidx.room.runtime)
            implementation(libs.androidx.sqlite.bundled)

            implementation(libs.kotlinx.datetime)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }

        androidUnitTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.ktor.client.mock)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.mockk)
        }
    }
    sourceSets.named("commonMain").configure {
        kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin")
    }
}

android {
    namespace = "com.tillylabs.gameofthroneswiki"
    compileSdk =
        libs.versions.android.compileSdk
            .get()
            .toInt()

    defaultConfig {
        applicationId = "com.tillylabs.gameofthroneswiki"
        minSdk =
            libs.versions.android.minSdk
                .get()
                .toInt()
        targetSdk =
            libs.versions.android.targetSdk
                .get()
                .toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("debug") {
            enableAndroidTestCoverage = true
            enableUnitTestCoverage = true
        }
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

ktlint {
    filter {
        exclude { it.file.path.contains("/generated/") }
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
    add("kspCommonMainMetadata", libs.koin.ksp.compiler)
    add("kspAndroid", libs.koin.ksp.compiler)
    add("kspIosArm64", libs.koin.ksp.compiler)
    add("kspIosSimulatorArm64", libs.koin.ksp.compiler)

    add("kspAndroid", libs.androidx.room.compiler)
    add("kspIosSimulatorArm64", libs.androidx.room.compiler)
    add("kspIosArm64", libs.androidx.room.compiler)
}

// Make sure KSP runs before compilation
project.tasks.withType(KspAATask::class.java).configureEach {
    if (name != "kspCommonMainKotlinMetadata") {
        dependsOn("kspCommonMainKotlinMetadata")
    }
    if (name == "kspDebugKotlinAndroid") {
        dependsOn("generateResourceAccessorsForAndroidDebug")
        dependsOn("generateResourceAccessorsForAndroidMain")
        dependsOn("generateActualResourceCollectorsForAndroidMain")
    }

    if (name == "kspReleaseKotlinAndroid") {
        dependsOn("generateResourceAccessorsForAndroidRelease")
        dependsOn("generateResourceAccessorsForAndroidMain")
        dependsOn("generateActualResourceCollectorsForAndroidMain")
    }
}

tasks["runKtlintFormatOverCommonMainSourceSet"].dependsOn("kspCommonMainKotlinMetadata")
tasks["runKtlintCheckOverCommonMainSourceSet"].dependsOn("kspCommonMainKotlinMetadata")

room {
    schemaDirectory("$projectDir/schemas")
}

kover {
    reports {
        filters {
            excludes {
                packages(
                    "*.generated.*",
                    "gameofthroneswiki.composeapp.generated.*",
                    "org.koin.ksp.generated",
                    "com.tillylabs.gameofthroneswiki.ui.preview.*",
                    "com.tillylabs.gameofthroneswiki.ui.theme.*",
                    "com.tillylabs.gameofthroneswiki.ui.navigation.*",
                    "com.tillylabs.gameofthroneswiki.database.dao.*",
                    "*.MainActivityKt*",
                    "*.App*",
                    "*.ComposableSingletons*"
                )
                classes(
                    "*\$*",
                    "*_Impl",
                    "*_Factory",
                    "*Database*",
                    "*Dao_Impl",
                    "*Activity*",
                    "*Preview*",
                    "*Theme*"
                )
            }
        }
    }
}

val ctlf: Task by tasks.creating {
    group = "verification"
    description = "Runs all tests and adds automatic lint fixing"
    dependsOn("ktlintFormat")
    dependsOn("testDebugUnitTest")
    dependsOn("koverHtmlReport")
}
