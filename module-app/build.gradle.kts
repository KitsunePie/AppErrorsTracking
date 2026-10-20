plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.ksp)
}

android {
    namespace = gropify.project.module.app.packageName
    compileSdk = gropify.project.android.compileSdk

    signingConfigs {
        create("universal") {
            keyAlias = gropify.project.module.app.signing.keyAlias
            keyPassword = gropify.project.module.app.signing.keyPassword
            storeFile = rootProject.file(gropify.project.module.app.signing.storeFilePath)
            storePassword = gropify.project.module.app.signing.storePassword
            enableV1Signing = true
            enableV2Signing = true
        }
    }
    defaultConfig {
        applicationId = gropify.project.module.app.packageName
        minSdk = gropify.project.android.minSdk
        targetSdk = gropify.project.android.targetSdk
        versionName = gropify.project.module.app.versionName
        versionCode = gropify.project.module.app.versionCode
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    flavorDimensions += "runtime"
    productFlavors {
        create("modern") {
            dimension = "runtime"
            isDefault = true
        }
        create("legacy") {
            dimension = "runtime"
            minSdk = gropify.project.android.legacy.minSdk
        }
    }
    buildTypes {
        all { signingConfig = signingConfigs.getByName("universal") }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        buildConfig = true
        viewBinding = true
    }
    lint { checkReleaseBuilds = false }
    androidResources.additionalParameters += listOf("--allow-reserved-package-id", "--package-id", "0x37")
}

androidComponents {
    onVariants(selector().all()) {
        it.outputs.forEach { output ->
            val currentFlavor = it.flavorName
            val currentType = it.buildType

            // Workaround for GitHub Actions.
            // Strongly transfer type to [String].
            @Suppress("UNNECESSARY_SAFE_CALL")
            val currentSuffix = gropify.github.ci.commit.id?.let { suffix ->
                if (suffix.isNotBlank()) "-$suffix" else ""
            }
            val currentVersion = "${output.versionName.get()}$currentSuffix(${output.versionCode.get()})"
            if (output is com.android.build.api.variant.impl.VariantOutputImpl)
                output.outputFileName.set("${gropify.project.name}-module-v$currentVersion-$currentFlavor-$currentType.apk")
        }
    }
}

dependencies {
    platform(libs.yukihook.bom).apply {
        implementation(this)
        ksp(this)
    }
    implementation(libs.yukihook.core)
    ksp(libs.yukihook.compiler)
    implementation(libs.yukihook.extension.kavaref)
    "modernImplementation"(libs.yukihook.runtime.libxposed)
    "modernCompileOnly"(libs.libxposed.api)
    "legacyImplementation"(libs.yukihook.runtime.xposed82)
    "legacyCompileOnly"(libs.xposed.framework.api)
    implementation(platform(libs.kavaref.bom))
    implementation(libs.kavaref.core)
    implementation(libs.kavaref.android)
    implementation(libs.kavaref.extension)
    implementation(libs.betterandroid.ui.extension)
    implementation(libs.betterandroid.system.extension)
    implementation(libs.project.promote)
    implementation(libs.microsoft.appcenter.analytics)
    implementation(libs.microsoft.appcenter.crashes)
    implementation(libs.libsu)
    implementation(libs.drawabletoolbox)
    implementation(libs.gson)
    implementation(libs.okhttp)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.espresso.core)
}