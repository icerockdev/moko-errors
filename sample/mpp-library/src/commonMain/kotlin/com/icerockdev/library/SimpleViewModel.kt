/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package com.icerockdev.library

import dev.icerock.moko.errors.ErrorEventListener
import dev.icerock.moko.errors.ExceptionMappersRegistry
import dev.icerock.moko.errors.handler.ExceptionHandler
import dev.icerock.moko.errors.presenters.AlertErrorPresenter
import dev.icerock.moko.errors.throwableToStringDesc
import dev.icerock.moko.mvvm.dispatcher.EventsDispatcher
import dev.icerock.moko.mvvm.livedata.LiveData
import dev.icerock.moko.mvvm.livedata.MutableLiveData
import dev.icerock.moko.mvvm.livedata.readOnly
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import dev.icerock.moko.resources.desc.desc
import kotlinx.coroutines.launch

fun createSimpleViewModel(errorEventsDispatcher: EventsDispatcher<ErrorEventListener>): SimpleViewModel {
    return SimpleViewModel(
        exceptionHandler = ExceptionHandler(
            errorEventsDispatcher = errorEventsDispatcher,
            errorPresenter = AlertErrorPresenter(
                exceptionMapper = ExceptionMappersRegistry::throwableToStringDesc,
                alertTitle = "Warning".desc()
            )
        )
    )
}

class SimpleViewModel(
    val exceptionHandler: ExceptionHandler
) : ViewModel() {

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading.readOnly()

    fun onAlertButtonClick() {
        _isLoading.value = true
        viewModelScope.launch {
            exceptionHandler.handle {
                serverRequest()
            }.finally {
                _isLoading.value = false
            }.execute()
        }
    }

    // Simulates a server response with an error
    private suspend fun serverRequest() {
        throw IllegalArgumentException()
    }
}
