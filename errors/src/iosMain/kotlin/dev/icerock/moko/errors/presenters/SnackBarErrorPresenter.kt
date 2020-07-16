/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.errors.presenters

import dev.icerock.moko.resources.desc.StringDesc
import platform.UIKit.UIViewController

/**
 * In iOS there is no such thing as snackbar, so it shows [AlertErrorPresenter].
 */
actual class SnackBarErrorPresenter actual constructor(
    exceptionMapper: (Throwable) -> StringDesc,
    private val duration: SnackBarDuration
) : ErrorPresenter<StringDesc>(exceptionMapper) {

    private val alertErrorPresenter = AlertErrorPresenter(exceptionMapper)

    override fun show(exception: Throwable, viewController: UIViewController, data: StringDesc) =
        alertErrorPresenter.show(exception, viewController, data)
}
