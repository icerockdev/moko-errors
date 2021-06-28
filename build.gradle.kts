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
        classpath("dev.icerock.moko:resources-generator:0.16.0")

        classpath(":errors-build-logic")
    }
}

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
