dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

rootProject.name = "study-oidc-spring"

include("resource-server-reactive")
include("resource-server-servlet")

gradle.beforeProject {
    group = "dev.purple"
    version = "1.0-SNAPSHOT"
}