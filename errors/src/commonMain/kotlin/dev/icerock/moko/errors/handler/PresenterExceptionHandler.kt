/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.errors.handler

import dev.icerock.moko.errors.ErrorEventListener
import dev.icerock.moko.errors.presenters.ErrorPresenter
import dev.icerock.moko.mvvm.dispatcher.EventsDispatcher

internal class PresenterExceptionHandler<T : Any>(
    private val exceptionMapper: ExceptionMapper<T>,
    private val errorPresenter: ErrorPresenter<T>,
    private val errorEventsDispatcher: EventsDispatcher<ErrorEventListener<T>>,
    private val onCatch: ((Throwable) -> Unit)? = null
) : ExceptionHandlerBinder by ExceptionHandlerBinderImpl<T>(
    errorPresenter,
    errorEventsDispatcher
), ExceptionHandler {
    override fun <R> handle(block: suspend () -> R): ExceptionHandlerContext<R> {
        return ExceptionHandlerContext(exceptionMapper, errorEventsDispatcher, onCatch, block)
    }
}
