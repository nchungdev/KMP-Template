import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

plugins {
    id(Plugins.androidApplication)
    id(Plugins.serialization)
    kotlin(Plugins.kotlinAndroid)
    id(Plugins.kotlinParcel)
    kotlin(Plugins.kapt)
    id(Plugins.safeArgs)
}

android {
    compileSdk = Configs.compileSdk

    defaultConfig {
        applicationId = Configs.androidApplicationId
        minSdk = Configs.minSdk
        targetSdk = Configs.targetSdk
        versionCode = getVersionCodeX()
        versionName = getVersionNameX()
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    buildFeatures {
        viewBinding = true
        dataBinding = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    lint {
        // Disable lintVital. Not needed since lint is run on CI
        isCheckReleaseBuilds = false
        // Ignore any tests
        isIgnoreTestSources = true
        // Make the build fail on any lint errors
        isAbortOnError = true
        // Allow lint to check dependencies
        isCheckDependencies = true
    }
}

fun getVersionNameX(): String {
    return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yy.MM.dd-HH"))
}

fun getVersionCodeX(): Int {
    return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMddHH")).toInt()
}

dependencies {
    implementation(project(":shared"))

    // UI
    implementation(Deps.AndroidX.appCompat)
    implementation(Deps.AndroidX.material)
    implementation(Deps.AndroidX.recyclerView)
    implementation(Deps.AndroidX.swipeToRefresh)
    implementation(Deps.AndroidX.constraint)
    implementation(Deps.AndroidX.viewPager)
    implementation(Deps.AndroidX.core)
    implementation(Deps.AndroidX.fragment)
    implementation(Deps.AndroidX.activity)
    implementation(Deps.AndroidX.lifeCycle)
    implementation(Deps.AndroidX.viewModel)
    implementation(Deps.AndroidX.lifeCycleCommon)
    implementation(Deps.AndroidX.navFragment)
    implementation(Deps.AndroidX.navUI)
    // splashScreen
    implementation(Deps.AndroidX.splashScreen)

    // Coroutines
    implementation(Deps.Coroutines.common)
    implementation(Deps.Coroutines.android)

    // DI
    implementation(Deps.koinCore)
    implementation(Deps.koinAndroid)

    // Image load
    implementation(Deps.ImageLoader.core)
    implementation(Deps.ImageLoader.transform)
    kapt(Deps.ImageLoader.compiler)
    implementation(Deps.ImageLoader.rcv) {
        isTransitive = false
    }
}
