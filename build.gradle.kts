/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

buildscript {
    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
    }

    dependencies {
        classpath(libs.kotlinGradlePlugin)
        classpath(libs.androidGradlePlugin)
        classpath(libs.mokoGradlePlugin)
        classpath(libs.mokoResourcesGradlePlugin)
    }
}

apply(plugin = "dev.icerock.moko.gradle.publication.nexus")

allprojects {

    allprojects {
        plugins.withId("org.gradle.maven-publish") {
            group = "dev.icerock.moko"
            version = libs.versions.mokoErrorsVersion.get()
        }
    }
}

tasks.register("clean", Delete::class).configure {
    delete(rootProject.buildDir)
}
