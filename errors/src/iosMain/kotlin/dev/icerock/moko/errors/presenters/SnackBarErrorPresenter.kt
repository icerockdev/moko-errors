/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.errors.presenters

import dev.icerock.moko.errors.ErrorPresenter
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc

actual class SnackBarErrorPresenter actual constructor(
    private val exceptionMapper: (Throwable) -> StringDesc,
    private val duration: SnackBarDuration
) : ErrorPresenter by AlertErrorPresenter(exceptionMapper, "Error".desc())
