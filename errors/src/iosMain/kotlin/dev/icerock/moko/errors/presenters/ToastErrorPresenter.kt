/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.errors.presenters

import dev.icerock.moko.resources.desc.StringDesc
import platform.UIKit.UIViewController

/**
 * In iOS there is no such thing as toast, so it shows [AlertErrorPresenter].
 */
actual class ToastErrorPresenter actual constructor(
    override val exceptionMapper: (Throwable) -> StringDesc,
    private val duration: ToastDuration
) : PlatformErrorPresenter<StringDesc>() {

    private val alertErrorPresenter = AlertErrorPresenter(exceptionMapper)

    override fun show(throwable: Throwable, viewController: UIViewController, data: StringDesc) =
        alertErrorPresenter.show(throwable, viewController, data)
}
