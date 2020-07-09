/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.errors.handler

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import dev.icerock.moko.errors.ErrorEventListener
import dev.icerock.moko.errors.ErrorPresenter
import dev.icerock.moko.mvvm.dispatcher.EventsDispatcher

actual interface ExceptionHandlerBinder {
    fun bind(lifecycleOwner: LifecycleOwner, activity: FragmentActivity)
}

actual class ExceptionHandlerBinderImpl actual constructor(
    private val errorPresenter: ErrorPresenter,
    private val eventsDispatcher: EventsDispatcher<ErrorEventListener>
) : ExceptionHandlerBinder {
    override fun bind(lifecycleOwner: LifecycleOwner, activity: FragmentActivity) {
        eventsDispatcher.bind(lifecycleOwner, EventsListener(activity, errorPresenter))
    }

    class EventsListener(
        private val activity: FragmentActivity,
        private val errorPresenter: ErrorPresenter
    ) : ErrorEventListener {
        override fun showError(exception: Throwable) {
            errorPresenter.show(exception, activity)
        }
    }
}
