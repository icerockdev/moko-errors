/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.errors.handler

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import dev.icerock.moko.errors.ErrorEventListener
import dev.icerock.moko.errors.presenters.PlatformErrorPresenter
import dev.icerock.moko.mvvm.dispatcher.EventsDispatcher

actual interface ExceptionHandlerBinder {
    fun bind(lifecycleOwner: LifecycleOwner, activity: FragmentActivity)
}

actual class ExceptionHandlerBinderImpl<T : Any> actual constructor(
    private val errorPresenter: PlatformErrorPresenter<T>,
    private val eventsDispatcher: EventsDispatcher<ErrorEventListener<T>>
) : ExceptionHandlerBinder {
    override fun bind(lifecycleOwner: LifecycleOwner, activity: FragmentActivity) {
        eventsDispatcher.bind(lifecycleOwner, EventsListener(activity, errorPresenter))
    }

    class EventsListener<T : Any>(
        private val activity: FragmentActivity,
        private val errorPresenter: PlatformErrorPresenter<T>
    ) : ErrorEventListener<T> {
        override fun showError(throwable: Throwable, data: T) {
            errorPresenter.show(throwable, activity, data)
        }
    }
}
