/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.errors.handler

import dev.icerock.moko.errors.ErrorEventListener
import dev.icerock.moko.errors.presenters.ErrorPresenter
import dev.icerock.moko.mvvm.dispatcher.EventsDispatcher
import platform.UIKit.UIViewController

actual interface ExceptionHandlerBinder {
    fun bind(viewController: UIViewController)
}

actual class ExceptionHandlerBinderImpl<T : Any> actual constructor(
    private val errorPresenter: ErrorPresenter<T>,
    private val eventsDispatcher: EventsDispatcher<ErrorEventListener<T>>
) : ExceptionHandlerBinder {

    private var eventsListener: ErrorEventListener<T>? = null

    override fun bind(viewController: UIViewController) {
        eventsListener = createEventsListener(viewController)
        eventsDispatcher.listener = eventsListener
    }

    private fun createEventsListener(viewController: UIViewController) =
        object : ErrorEventListener<T> {
            override fun showError(throwable: Throwable, data: T) {
                errorPresenter.show(throwable, viewController, data)
            }
        }
}
