package com.icerockdev.library

import dev.icerock.moko.errors.ExceptionMappersRegistry
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc

fun initExceptionRegistry() {
    ExceptionMappersRegistry
        .register<IllegalArgumentException, StringDesc> {
            "Wrong argument!".desc()
        }
        .setUnknownErrorText("Unknown error".desc())
}
