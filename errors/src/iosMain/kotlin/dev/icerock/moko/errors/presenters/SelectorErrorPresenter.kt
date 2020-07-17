/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.errors.presenters

import platform.UIKit.UIViewController

actual class SelectorErrorPresenter<T : Any> actual constructor(
    private val errorPresenterSelector: (Throwable) -> ErrorPresenter<T>
) : ErrorPresenter<T>() {
    override val exceptionMapper: (Throwable) -> T
        get() = throw IllegalArgumentException("this will never call")

    override fun show(throwable: Throwable, viewController: UIViewController, data: T) {
        errorPresenterSelector(throwable).show(throwable, viewController, data)
    }
}
