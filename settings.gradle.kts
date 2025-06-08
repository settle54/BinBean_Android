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
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = java.net.URI("https://devrepo.kakao.com/nexus/content/groups/public/") }
    }
}

rootProject.name = "BinBean_Android"
include(":app")
include(":data")
include(":domain")
include(":presentation:user:bookmark")
include(":presentation:user:mypage")
include(":presentation:user:map")
include(":presentation:user:container")
include(":core:ui")
include(":core:retrofit")
include(":presentation:user:review")
include(":presentation")
include(":presentation:admin:main")
include(":presentation:admin:home")
include(":core:resource")
include(":presentation:admin:register")
include(":presentation:login")
include(":presentation:myPage")
include(":presentation:review")
include(":core:navigation")
include(":data:login")
include(":domain:login")
include(":domain:user")
include(":data:user")
include(":data:admin")
include(":core:util")
include(":domain:admin")
