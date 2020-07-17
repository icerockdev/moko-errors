/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.errors.presenters

import dev.icerock.moko.errors.ErrorEventListener
import dev.icerock.moko.mvvm.dispatcher.EventsDispatcher

expect abstract class ErrorPresenter<T : Any>() : ErrorPresenterBase<T>

abstract class ErrorPresenterBase<T : Any> {

    protected abstract val exceptionMapper: (Throwable) -> T

    open fun sendErrorEvent(
        eventsDispatcher: EventsDispatcher<ErrorEventListener<T>>,
        throwable: Throwable
    ) {
        val data = exceptionMapper(throwable)
        eventsDispatcher.dispatchEvent { showError(throwable, data) }
    }
}
