/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.errors.presenters

import platform.UIKit.UIViewController

actual abstract class ErrorPresenter<T : Any> : ErrorPresenterBase<T>() {
    abstract fun show(exception: Throwable, viewController: UIViewController, data: T)
}
