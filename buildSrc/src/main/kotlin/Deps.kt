/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

object Deps {
    object Plugins {
        const val android =
            "com.android.tools.build:gradle:${Versions.Plugins.android}"
        const val kotlin =
            "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.Plugins.kotlin}"
    }

    object Libs {
        object Android {
            val kotlinStdLib = AndroidLibrary(
                name = "org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlin}"
            )
            val appCompat = AndroidLibrary(
                name = "androidx.appcompat:appcompat:${Versions.Libs.Android.appCompat}"
            )
            val lifecycle = AndroidLibrary(
                name = "androidx.lifecycle:lifecycle-extensions:${Versions.Libs.Android.lifecycle}"
            )
            val material = AndroidLibrary(
                name = "com.google.android.material:material:${Versions.Libs.Android.material}"
            )
        }

        object MultiPlatform {
            val kotlinStdLib = MultiPlatformLibrary(
                android = Android.kotlinStdLib.name,
                common = "org.jetbrains.kotlin:kotlin-stdlib-common:${Versions.kotlin}"
            )
            val coroutines = MultiPlatformLibrary(
                android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.Libs.MultiPlatform.coroutines}",
                common = "org.jetbrains.kotlinx:kotlinx-coroutines-core-common:${Versions.Libs.MultiPlatform.coroutines}",
                ios = "org.jetbrains.kotlinx:kotlinx-coroutines-core-native:${Versions.Libs.MultiPlatform.coroutines}"
            )
            val mokoErrors = MultiPlatformLibrary(
                common = "dev.icerock.moko:errors:${Versions.Libs.MultiPlatform.mokoErrors}",
                iosX64 = "dev.icerock.moko:errors-iosx64:${Versions.Libs.MultiPlatform.mokoErrors}",
                iosArm64 = "dev.icerock.moko:errors-iosarm64:${Versions.Libs.MultiPlatform.mokoErrors}"
            )
            val mokoMvvm = MultiPlatformLibrary(
                common = "dev.icerock.moko:mvvm:${Versions.Libs.MultiPlatform.mokoMvvm}",
                iosX64 = "dev.icerock.moko:mvvm-iosx64:${Versions.Libs.MultiPlatform.mokoMvvm}",
                iosArm64 = "dev.icerock.moko:mvvm-iosarm64:${Versions.Libs.MultiPlatform.mokoMvvm}"
            )
        }
    }

    object Tests {
        val kotlinTestCommon = MultiPlatformLibrary(
            android = "org.jetbrains.kotlin:kotlin-test:${Versions.kotlin}",
            common = "org.jetbrains.kotlin:kotlin-test-common:${Versions.kotlin}"
        )
        val kotlinTestCommonAnnotations = MultiPlatformLibrary(
            android = "org.jetbrains.kotlin:kotlin-test-junit:${Versions.kotlin}",
            common = "org.jetbrains.kotlin:kotlin-test-annotations-common:${Versions.kotlin}"
        )
        val androidCoreTesting = MultiPlatformLibrary(
            android = "androidx.arch.core:core-testing:${Versions.Tests.androidCoreTesting}"
        )
    }

    val plugins: Map<String, String> = mapOf(
        "com.android.application" to Plugins.android,
        "com.android.library" to Plugins.android,
        "org.jetbrains.kotlin.multiplatform" to Plugins.kotlin,
        "kotlin-kapt" to Plugins.kotlin,
        "kotlin-android" to Plugins.kotlin
    )
}