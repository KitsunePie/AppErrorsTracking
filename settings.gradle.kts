pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}
plugins {
    id("com.highcapable.sweetdependency") version "1.0.2"
    id("com.highcapable.sweetproperty") version "1.0.3"
}
sweetProperty {
    global {
        all {
            permanentKeyValues(
                "GITHUB_CI_COMMIT_ID" to "",
                "APP_CENTER_SECRET" to ""
            )
            generateFrom(ROOT_PROJECT, SYSTEM_ENV)
        }
        sourcesCode {
            propertiesFileNames(".secret/secret.properties")
            includeKeys("GITHUB_CI_COMMIT_ID", "APP_CENTER_SECRET")
        }
    }
    rootProject { all { isEnable = false } }
}
rootProject.name = "AppErrorsTracking"
include(":module-app", ":demo-app")