/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.errors.presenters

import dev.icerock.moko.errors.MR
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc

expect class AlertErrorPresenter(
    exceptionMapper: (Throwable) -> StringDesc,
    alertTitle: StringDesc = MR.strings.moko_errors_presenters_alertDialogTitle.desc(),
    positiveButtonText: StringDesc = MR.strings.moko_errors_presenters_alertPositiveButton.desc()
) : ErrorPresenter<StringDesc>
