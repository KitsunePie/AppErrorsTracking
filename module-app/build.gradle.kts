plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.ksp)
    alias(libs.plugins.flexi.locale)
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
            val currentType = it.buildType
            val currentSuffix = gropify.github.ci.commit.id.let { suffix ->
                if (suffix.isNotBlank()) "-$suffix" else ""
            }
            val currentVersion = "${output.versionName.get()}$currentSuffix(${output.versionCode.get()})"
            if (output is com.android.build.api.variant.impl.VariantOutputImpl)
                output.outputFileName.set("${gropify.project.name}-module-v$currentVersion-$currentType.apk")
        }
    }
}

dependencies {
    compileOnly(libs.rovo89.xposed.api)
    implementation(libs.yukihookapi)
    ksp(libs.yukihookapi.ksp.xposed)
    implementation(libs.kavaref.core)
    implementation(libs.kavaref.extension)
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