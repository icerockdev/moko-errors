/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.errors.handler

import dev.icerock.moko.errors.ErrorEventListener
import dev.icerock.moko.errors.ErrorPresenter
import dev.icerock.moko.mvvm.dispatcher.EventsDispatcher
import platform.UIKit.UIViewController

actual interface ExceptionHandlerBinder {
    fun bind(viewController: UIViewController)
}

actual class ExceptionHandlerBinderImpl actual constructor(
    private val errorPresenter: ErrorPresenter,
    private val eventsDispatcher: EventsDispatcher<ErrorEventListener>
) : ExceptionHandlerBinder {
    override fun bind(viewController: UIViewController) {
        eventsDispatcher.listener = object : ErrorEventListener {
            override fun showError(exception: Throwable) {
                errorPresenter.show(exception, viewController)
            }
        }
    }
}
