plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = gropify.project.demo.app.packageName
    compileSdk = gropify.project.android.compileSdk
    ndkVersion = gropify.project.android.ndk.version

    signingConfigs {
        create("universal") {
            keyAlias = gropify.project.demo.app.signing.keyAlias
            keyPassword = gropify.project.demo.app.signing.keyPassword
            storeFile = rootProject.file(gropify.project.demo.app.signing.storeFilePath)
            storePassword = gropify.project.demo.app.signing.storePassword
            enableV1Signing = true
            enableV2Signing = true
        }
    }
    defaultConfig {
        applicationId = gropify.project.demo.app.packageName
        minSdk = gropify.project.android.minSdk
        targetSdk = gropify.project.android.targetSdk
        versionName = gropify.project.demo.app.versionName
        versionCode = gropify.project.demo.app.versionCode
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        all { signingConfig = signingConfigs.getByName("universal") }
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    externalNativeBuild {
        cmake {
            path("src/main/cpp/CMakeLists.txt")
            version = gropify.project.android.cmake.version
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
            val currentType = it.buildType
            val currentSuffix = gropify.github.ci.commit.id.let { suffix ->
                if (suffix.isNotBlank()) "-$suffix" else ""
            }
            val currentVersion = "${output.versionName.get()}$currentSuffix(${output.versionCode.get()})"
            if (output is com.android.build.api.variant.impl.VariantOutputImpl)
                output.outputFileName.set("${gropify.project.name}-demo-v$currentVersion-$currentType.apk")
        }
    }
}

dependencies {
    implementation(libs.project.promote)
    implementation(libs.kavaref.core)
    implementation(libs.kavaref.extension)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.espresso.core)
}