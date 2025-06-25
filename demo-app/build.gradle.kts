plugins {
    autowire(libs.plugins.android.application)
    autowire(libs.plugins.kotlin.android)
}

android {
    namespace = property.project.demo.app.packageName
    compileSdk = property.project.android.compileSdk
    ndkVersion = property.project.android.ndk.version

    signingConfigs {
        create("universal") {
            keyAlias = property.project.demo.app.signing.keyAlias
            keyPassword = property.project.demo.app.signing.keyPassword
            storeFile = rootProject.file(property.project.demo.app.signing.storeFilePath)
            storePassword = property.project.demo.app.signing.storePassword
            enableV1Signing = true
            enableV2Signing = true
        }
    }
    defaultConfig {
        applicationId = property.project.demo.app.packageName
        minSdk = property.project.android.minSdk
        targetSdk = property.project.android.targetSdk
        versionName = property.project.demo.app.versionName
        versionCode = property.project.demo.app.versionCode
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
            version = property.project.android.cmake.version
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs = listOf(
            "-Xno-param-assertions",
            "-Xno-call-assertions",
            "-Xno-receiver-assertions"
        )
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

            // Workaround for GitHub Actions.
            // Why? I don't know, but it works.
            // Unresolved reference. None of the following candidates is applicable because of receiver type mismatch:
            //                       public inline fun CharSequence.isNotBlank(): Boolean defined in kotlin.text.
            @Suppress("UNNECESSARY_SAFE_CALL", "RemoveRedundantCallsOfConversionMethods")
            val currentSuffix = property.github.ci.commit.id?.let { suffix ->
                // Workaround for GitHub Actions.
                // Strongly transfer type to [String].
                val sSuffix = suffix.toString()
                if (sSuffix.isNotBlank()) "-$sSuffix" else ""
            }
            val currentVersion = "${output.versionName.get()}$currentSuffix(${output.versionCode.get()})"
            if (output is com.android.build.api.variant.impl.VariantOutputImpl)
                output.outputFileName.set("${property.project.name}-demo-v$currentVersion-$currentType.apk")
        }
    }
}

dependencies {
    implementation(com.fankes.projectpromote.project.promote)
    implementation(com.highcapable.kavaref.kavaref.core)
    implementation(com.highcapable.kavaref.kavaref.extension)
    implementation(androidx.core.core.ktx)
    implementation(androidx.appcompat.appcompat)
    implementation(com.google.android.material.material)
    testImplementation(junit.junit)
    androidTestImplementation(androidx.test.ext.junit)
    androidTestImplementation(androidx.test.espresso.espresso.core)
}