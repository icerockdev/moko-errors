/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.errors.handler

import dev.icerock.moko.errors.ErrorEventListener
import dev.icerock.moko.errors.presenters.ErrorPresenter
import dev.icerock.moko.mvvm.dispatcher.EventsDispatcher

interface ExceptionHandler : ExceptionHandlerBinder {
    fun <R> handle(block: suspend () -> R): ExceptionHandlerContext<R>

    /**
     * Directly launches the error-presenter to display the error for exception [throwable].
     */
    fun showError(throwable: Throwable)

    companion object {
        operator fun <T : Any> invoke(
            exceptionMapper: ExceptionMapper<T>,
            errorPresenter: ErrorPresenter<T>,
            errorEventsDispatcher: EventsDispatcher<ErrorEventListener<T>> = EventsDispatcher(),
            onCatch: ((Throwable) -> Unit)? = null
        ): ExceptionHandler {
            return PresenterExceptionHandler(
                exceptionMapper = exceptionMapper,
                errorPresenter = errorPresenter,
                errorEventsDispatcher = errorEventsDispatcher,
                onCatch = onCatch
            )
        }
    }
}
