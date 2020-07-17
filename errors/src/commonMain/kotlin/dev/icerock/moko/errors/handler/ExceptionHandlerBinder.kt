/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.errors.handler

import dev.icerock.moko.errors.ErrorEventListener
import dev.icerock.moko.errors.presenters.ErrorPresenter
import dev.icerock.moko.mvvm.dispatcher.EventsDispatcher

expect interface ExceptionHandlerBinder

expect class ExceptionHandlerBinderImpl<T : Any>(
    errorPresenter: ErrorPresenter<T>,
    eventsDispatcher: EventsDispatcher<ErrorEventListener<T>>
) : ExceptionHandlerBinder
