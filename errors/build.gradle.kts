/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    id("dev.icerock.moko.gradle.multiplatform.mobile")
    id("kotlin-parcelize")
    id("dev.icerock.mobile.multiplatform-resources")
    id("dev.icerock.moko.gradle.detekt")
    id("dev.icerock.moko.gradle.publication")
}

group = "dev.icerock.moko"
version = libs.versions.mokoErrorsVersion.get()

dependencies {
    commonMainImplementation(libs.coroutines)

    androidMainImplementation(libs.appCompat)
    androidMainImplementation(libs.material)

    commonMainImplementation(libs.mokoMvvmCore)
    commonMainApi(libs.mokoResources)
}

multiplatformResources {
    multiplatformResourcesPackage = "dev.icerock.moko.errors"
}

