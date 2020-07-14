/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.errors.handler

import dev.icerock.moko.errors.ErrorEventListener
import dev.icerock.moko.errors.ErrorPresenter
import dev.icerock.moko.mvvm.dispatcher.EventsDispatcher

class ExceptionHandler(
    errorPresenter: ErrorPresenter,
    private val errorEventsDispatcher: EventsDispatcher<ErrorEventListener>
) : ExceptionHandlerBinder by ExceptionHandlerBinderImpl(
    errorPresenter,
    errorEventsDispatcher
) {

    fun <T> handle(block: suspend () -> T): ExceptionHandlerContext<T> {
        return ExceptionHandlerContext(errorEventsDispatcher, block)
    }
}
