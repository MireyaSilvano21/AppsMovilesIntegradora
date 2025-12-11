pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
    plugins {
        // Declara que el plugin KSP está disponible y especifica su versión
        id("com.google.devtools.ksp") version "1.9.22-1.0.17" // <-- ¡NUEVA LÍNEA!
        // 💡 NOTA: La versión "1.9.22-1.0.17" debe coincidir con tu versión de Kotlin.
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "MusicP"
include(":app")
