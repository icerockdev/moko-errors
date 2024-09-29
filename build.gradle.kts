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

val mokoVersion = libs.versions.mokoErrorsVersion.get()
allprojects {
    this.group = "dev.icerock.moko"
    this.version = mokoVersion
}

tasks.register("clean", Delete::class).configure {
    delete(rootProject.layout.buildDirectory)
}
