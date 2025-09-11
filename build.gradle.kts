// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    extra["kotlin_version"] = "1.9.10"
    extra["compose_version"] = "1.5.4"
    
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    
    dependencies {
        classpath("com.android.tools.build:gradle:8.1.2")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${extra["kotlin_version"]}")
        classpath("org.springframework.boot:spring-boot-gradle-plugin:3.1.5")
        classpath("org.jetbrains.kotlin:kotlin-serialization:${extra["kotlin_version"]}")
    }
}

plugins {
    id("org.springframework.boot") version "3.1.5" apply false
    id("io.spring.dependency-management") version "1.1.3" apply false
    kotlin("jvm") version "1.9.10" apply false
    kotlin("plugin.spring") version "1.9.10" apply false
    kotlin("plugin.jpa") version "1.9.10" apply false
    kotlin("plugin.serialization") version "1.9.10" apply false
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
        maven { url = uri("https://repo.spring.io/milestone") }
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}