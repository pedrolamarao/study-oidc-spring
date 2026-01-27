plugins {
    `java-gradle-plugin`
}

gradlePlugin {
    plugins {
        register("br.dev.purpura.versioning") {
            id = "br.dev.purpura.versioning"
            implementationClass = "br.dev.purpura.gradle.versioning.VersioningPlugin"
        }
    }
}

tasks.validatePlugins {
    enableStricterValidation = true
}