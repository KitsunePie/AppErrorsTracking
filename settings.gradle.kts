pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
        maven("https://api.xposed.info/")
        maven("https://raw.githubusercontent.com/fankes/maven-repository/main/repository/releases")
    }
}
plugins {
    id("com.highcapable.gropify") version "1.0.0"
}
gropify {
    global {
        common {
            permanentKeyValues(
                "GITHUB_CI_COMMIT_ID" to "",
                "APP_CENTER_SECRET" to ""
            )
            includeKeys(
                "GITHUB_CI_COMMIT_ID" to "",
                "APP_CENTER_SECRET" to "",
                "^project\\..*\$".toRegex()
            )
            locations(GropifyLocation.RootProject, GropifyLocation.SystemEnv)
        }
        android {
            existsPropertyFiles(".secret/secret.properties")
            includeKeys("GITHUB_CI_COMMIT_ID", "APP_CENTER_SECRET")
            // 关闭类型自动转换功能，防止一些特殊 "COMMIT ID" 被生成为数值
            useTypeAutoConversion = false
        }
    }
    rootProject { common { isEnabled = false } }
}
rootProject.name = "AppErrorsTracking"
include(":module-app", ":demo-app")