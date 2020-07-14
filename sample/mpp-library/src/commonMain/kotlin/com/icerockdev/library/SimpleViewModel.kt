/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package com.icerockdev.library

import dev.icerock.moko.errors.ErrorEventListener
import dev.icerock.moko.errors.handler.ExceptionHandler
import dev.icerock.moko.errors.presenters.AlertErrorPresenter
import dev.icerock.moko.errors.registry.ExceptionMappersRegistry
import dev.icerock.moko.errors.registry.throwableToStringDesc
import dev.icerock.moko.mvvm.dispatcher.EventsDispatcher
import dev.icerock.moko.mvvm.livedata.LiveData
import dev.icerock.moko.mvvm.livedata.MutableLiveData
import dev.icerock.moko.mvvm.livedata.readOnly
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import dev.icerock.moko.resources.desc.desc
import kotlinx.coroutines.launch
import kotlin.random.Random

fun createSimpleViewModel(errorEventsDispatcher: EventsDispatcher<ErrorEventListener>): SimpleViewModel {
    return SimpleViewModel(
        exceptionHandler = ExceptionHandler(
            errorEventsDispatcher = errorEventsDispatcher,
            errorPresenter = AlertErrorPresenter(
                exceptionMapper = ExceptionMappersRegistry::throwableToStringDesc,
                alertTitle = MR.strings.errorDialogTitle.desc()
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

    private var exceptionFlag = false

    // Simulates a server response with an error
    private suspend fun serverRequest() {
        if(exceptionFlag) {
            exceptionFlag = !exceptionFlag
            throw CustomException(Random.nextInt(2) * 10)
        } else {
            exceptionFlag = !exceptionFlag
            throw IllegalArgumentException()
        }
    }
}