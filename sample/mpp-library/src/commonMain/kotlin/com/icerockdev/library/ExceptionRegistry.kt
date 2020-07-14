package com.icerockdev.library

import dev.icerock.moko.errors.registry.ExceptionMappersRegistry
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc

@Suppress("MagicNumber")
fun initExceptionRegistry() {
    ExceptionMappersRegistry
        .condition<StringDesc>(
            condition = { it is CustomException && it.code == 10 },
            mapper = { MR.strings.myExceptionText.desc() }
        )
        .register<IllegalArgumentException, StringDesc> {
            MR.strings.illegalArgumentText.desc()
        }
}
