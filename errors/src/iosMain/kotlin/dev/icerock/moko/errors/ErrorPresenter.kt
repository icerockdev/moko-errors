/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.errors

import platform.UIKit.UIViewController

actual interface ErrorPresenter {
    fun show(exception: Throwable, viewController: UIViewController)
}
