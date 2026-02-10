/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

import dev.icerock.gradle.tasks.GenerateMultiplatformResourcesTask

plugins {
    id("dev.icerock.moko.gradle.multiplatform.mobile")
    id("kotlin-parcelize")
    id("dev.icerock.mobile.multiplatform-resources")
    id("dev.icerock.moko.gradle.detekt")
    id("dev.icerock.moko.gradle.publication")
    id("dev.icerock.moko.gradle.stub.javadoc")
}

group = "dev.icerock.moko"
version = libs.versions.mokoErrorsVersion.get()

android {
    namespace = "dev.icerock.moko.errors"
}

kotlin {
    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }
}

dependencies {
    commonMainImplementation(libs.coroutines)

    androidMainImplementation(libs.appCompat)
    androidMainImplementation(libs.material)

    commonMainImplementation(libs.mokoMvvmCore)
    commonMainApi(libs.mokoResources)
}

multiplatformResources {
    resourcesPackage.set("dev.icerock.moko.errors")
}

tasks.withType<GenerateMultiplatformResourcesTask>().configureEach {
    tasks.getByName("sourcesJar").dependsOn(this)
}
