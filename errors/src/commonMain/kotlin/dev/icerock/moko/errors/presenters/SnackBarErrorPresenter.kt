/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.errors.presenters

import dev.icerock.moko.errors.ErrorPresenter
import dev.icerock.moko.resources.desc.StringDesc

expect class SnackBarErrorPresenter(
    exceptionMapper: (Throwable) -> StringDesc,
    duration: SnackBarDuration = SnackBarDuration.INDEFINITE
) : ErrorPresenter

enum class SnackBarDuration(val code: Int) {
    INDEFINITE(-2),
    SHORT(-1),
    LONG(0)
}
