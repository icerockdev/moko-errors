/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.errors.presenters

import dev.icerock.moko.errors.mappers.ExceptionMappersStorage
import dev.icerock.moko.resources.desc.StringDesc

expect class ToastErrorPresenter(
    exceptionMapper: (Throwable) -> StringDesc = ExceptionMappersStorage.throwableMapper(),
    duration: ToastDuration = ToastDuration.SHORT
) : ErrorPresenter<StringDesc>

enum class ToastDuration {
    SHORT,
    LONG
}
