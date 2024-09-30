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

    // fix Reason: Task ':errors:publishAndroidPublicationToSonatypeRepository' uses this output of task ':errors:signIosArm64Publication' without declaring an explicit or implicit dependency. This can lead to incorrect results being produced, depending on what order the tasks are executed.
    val signingTasks = tasks.withType<Sign>()
    tasks.withType<AbstractPublishToMaven>().configureEach {
        dependsOn(signingTasks)
    }
}

tasks.register("clean", Delete::class).configure {
    delete(rootProject.layout.buildDirectory)
}
