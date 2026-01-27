pluginManagement {
    includeBuild("gradle/plugins")
}

plugins {
    id("br.dev.purpura.versioning")
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

versioning {
    major = "1"
    minor = "0"
    build.add(gitCount)
    build.add(gitRevision)
}


include("resource-server-reactive")
include("resource-server-servlet")