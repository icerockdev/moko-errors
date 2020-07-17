/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.errors.presenters

import dev.icerock.moko.errors.ErrorEventListener
import dev.icerock.moko.errors.handler.ExceptionHandlerBinder
import dev.icerock.moko.mvvm.dispatcher.EventsDispatcher

expect abstract class ErrorPresenter<T : Any>() : ErrorEventListener<T>, ExceptionHandlerBinder {
    internal abstract val eventsDispatcher: EventsDispatcher<ErrorEventListener<T>>
}
