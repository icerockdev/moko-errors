/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

object Versions {
    object Android {
        const val compileSdk = 28
        const val targetSdk = 28
        const val minSdk = 16
    }

    const val mokoErrors = "0.2.0"

    const val kotlin = "1.3.72"
    private const val androidArch = "2.0.0"
    private const val mokoResources = "0.11.0"
    const val detekt = "1.10.0"

    object Plugins {
        const val android = "3.6.3"

        const val kotlin = Versions.kotlin
        const val mokoResources = Versions.mokoResources
    }

    object Libs {
        object Android {
            const val appCompat = "1.1.0"
            const val material = "1.0.0"
            const val lifecycle = androidArch
        }

        object MultiPlatform {
            const val coroutines = "1.3.4"
            const val mokoErrors = Versions.mokoErrors
            const val mokoMvvm = "0.7.0"
            const val mokoResources = Versions.mokoResources
        }
    }

    object Tests {
        const val androidCoreTesting = androidArch
    }
}
