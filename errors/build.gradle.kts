/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    id("multiplatform-library-convention")
    id("kotlin-parcelize")
    id("dev.icerock.mobile.multiplatform-resources")
    id("detekt-convention")
    id("publication-convention")
}

group = "dev.icerock.moko"
version = libs.versions.mokoErrorsVersion.get()

dependencies {
    commonMainImplementation(libs.coroutines)

    "androidMainImplementation"(libs.appCompat)
    "androidMainImplementation"(libs.material)

    commonMainImplementation(libs.mokoMvvmCore)
    commonMainApi(libs.mokoResources)
}

multiplatformResources {
    multiplatformResourcesPackage = "dev.icerock.moko.errors"
}

