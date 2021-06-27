/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

import java.util.Base64
import kotlin.text.String

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.multiplatform")
    id("kotlin-android-extensions")
   id("dev.icerock.mobile.multiplatform.android-manifest")
    id("dev.icerock.mobile.multiplatform-resources")
    id("org.gradle.maven-publish")
    id("signing")
}

group = "dev.icerock.moko"
version = libs.versions.mokoErrorsVersion.get()

dependencies {
    commonMainImplementation(libs.coroutines)

    androidMainImplementation(libs.appCompat)
    androidMainImplementation(libs.material)

    commonMainImplementation(libs.mokoMvvmCore)
    commonMainApi(libs.mokoResources)

    // temporary fix of https://youtrack.jetbrains.com/issue/KT-41083
    commonMainImplementation("dev.icerock.moko:parcelize:0.4.0")
    commonMainImplementation("dev.icerock.moko:graphics:0.4.0")
}

multiplatformResources {
    multiplatformResourcesPackage = "dev.icerock.moko.errors"
}

val javadocJar by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
}

publishing {
    repositories.maven("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/") {
        name = "OSSRH"

        credentials {
            username = System.getenv("OSSRH_USER")
            password = System.getenv("OSSRH_KEY")
        }
    }

    publications.withType<MavenPublication> {
        // Stub javadoc.jar artifact
        artifact(javadocJar.get())

        // Provide artifacts information requited by Maven Central
        pom {
            name.set("MOKO errors")
            description.set("Automated exceptions handler for mobile (android & ios) Kotlin Multiplatform development.")
            url.set("https://github.com/icerockdev/moko-errors")
            licenses {
                license {
                    url.set("https://github.com/icerockdev/moko-errors/blob/master/LICENSE.md")
                }
            }

            developers {
                developer {
                    id.set("Tetraquark")
                    name.set("Vladislav Areshkin")
                    email.set("vareshkin@icerockdev.com")
                }
                developer {
                    id.set("Alex009")
                    name.set("Aleksey Mikhailov")
                    email.set("aleksey.mikhailov@icerockdev.com")
                }
            }

            scm {
                connection.set("scm:git:ssh://github.com/icerockdev/moko-errors.git")
                developerConnection.set("scm:git:ssh://github.com/icerockdev/moko-errors.git")
                url.set("https://github.com/icerockdev/moko-errors")
            }
        }
    }

    signing {
        val signingKeyId: String? = System.getenv("SIGNING_KEY_ID")
        val signingPassword: String? = System.getenv("SIGNING_PASSWORD")
        val signingKey: String? = System.getenv("SIGNING_KEY")?.let { base64Key ->
            String(Base64.getDecoder().decode(base64Key))
        }
        if (signingKeyId != null) {
            useInMemoryPgpKeys(signingKeyId, signingKey, signingPassword)
            sign(publishing.publications)
        }
    }
}

