@file:Suppress("ImplicitThis", "KDocMissingDocumentation")

plugins {
    id("multiloader-common")
    id("net.neoforged.moddev")
//    kotlin("kapt")
    kotlin("kapt")
}

private fun getProp(property: String): String = providers.gradleProperty(property).get()

neoForge {
    neoFormVersion = getProp("neo_form_version")
    // Automatically enable AccessTransformers if the file exists
    accessTransformers {
        file("src/main/resources/META-INF/accesstransformer.cfg")
    }
    parchment {
        minecraftVersion = getProp("minecraft_version")
        mappingsVersion = getProp("parchment_version")
    }
}

dependencies {
    compileOnly("net.fabricmc:sponge-mixin:0.17.3+mixin.0.8.7")
    // fabric and neoforge both bundle mixinextras, so it is safe to use it in common
    compileOnly("io.github.llamalad7:mixinextras-common:0.5.4")
    annotationProcessor("io.github.llamalad7:mixinextras-common:0.5.4")
    kapt("io.github.llamalad7:mixinextras-common:0.5.4")
//    kapt("io.github.llamalad7:mixinextras-common:0.5.4")
    testImplementation(kotlin("test"))
}

configurations {
    create("commonJava") {
        isCanBeResolved = false
        isCanBeConsumed = true
    }
    create("commonResources") {
        isCanBeResolved = false
        isCanBeConsumed = true
    }
}

artifacts {
    add("commonJava", sourceSets.main.get().java.sourceDirectories.singleFile)
    add("commonResources", sourceSets.main.get().resources.sourceDirectories.singleFile)
}
repositories {
    mavenCentral()
}