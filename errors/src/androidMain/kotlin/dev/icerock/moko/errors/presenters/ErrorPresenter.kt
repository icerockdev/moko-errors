/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.errors.presenters

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import dev.icerock.moko.errors.ErrorEventListener
import dev.icerock.moko.errors.handler.ExceptionHandlerBinder
import dev.icerock.moko.errors.handler.ExceptionHandlerBinderImpl
import dev.icerock.moko.mvvm.dispatcher.EventsDispatcher

actual abstract class ErrorPresenter<T : Any> : ErrorEventListener<T>, ExceptionHandlerBinder {
    internal actual abstract val eventsDispatcher: EventsDispatcher<ErrorEventListener<T>>

    protected var activity: FragmentActivity? = null

    override fun bind(lifecycleOwner: LifecycleOwner, activity: FragmentActivity) {
        this.activity = activity // Could there be a leak?
        eventsDispatcher.bind(lifecycleOwner, this)
    }

}
