pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    repositories {
        mavenCentral()
    }
}

rootProject.name = "NextGenAPK"

include(":backend-core")
include(":voice-engine")
include(":database-layer")
include(":integration-hub")
include(":mcp-server")
include(":shared")