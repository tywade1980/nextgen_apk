pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}

rootProject.name = "NextGenAPK"

include(":app")
include(":backend-core")
include(":voice-engine")
include(":database-layer")
include(":integration-hub")
include(":mcp-server")
include(":shared")