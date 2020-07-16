/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.errors.handler

import dev.icerock.moko.errors.ErrorEventListener
import dev.icerock.moko.errors.presenters.ErrorPresenter
import dev.icerock.moko.mvvm.dispatcher.EventsDispatcher

class ExceptionHandler<T : Any>(
    private val errorPresenter: ErrorPresenter<T>,
    private val errorEventsDispatcher: EventsDispatcher<ErrorEventListener<T>>
) : ExceptionHandlerBinder by ExceptionHandlerBinderImpl<T>(
    errorPresenter,
    errorEventsDispatcher
) {

    fun <R> handle(block: suspend () -> R): ExceptionHandlerContext<T, R> {
        return ExceptionHandlerContext(errorPresenter, errorEventsDispatcher, block)
    }
}
