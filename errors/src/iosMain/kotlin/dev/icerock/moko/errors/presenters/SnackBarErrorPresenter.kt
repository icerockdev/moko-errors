/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.errors.presenters

import dev.icerock.moko.errors.MR
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc

/**
 * In iOS there is no such thing as snackbar, so it shows [AlertErrorPresenter].
 */
actual class SnackBarErrorPresenter actual constructor(
    duration: SnackBarDuration
) : ErrorPresenter<StringDesc> by AlertErrorPresenter(
    alertTitle = MR.strings.moko_errors_presenters_alertDialogTitle.desc(),
    positiveButtonText = MR.strings.moko_errors_presenters_alertPositiveButton.desc()
)
