/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package com.icerockdev.library

import dev.icerock.moko.errors.handler.ExceptionHandler
import dev.icerock.moko.errors.mappers.ExceptionMappersStorage
import dev.icerock.moko.errors.presenters.SelectorErrorPresenter
import dev.icerock.moko.errors.presenters.SnackBarDuration
import dev.icerock.moko.errors.presenters.SnackBarErrorPresenter
import dev.icerock.moko.errors.presenters.ToastDuration
import dev.icerock.moko.errors.presenters.ToastErrorPresenter
import dev.icerock.moko.mvvm.livedata.LiveData
import dev.icerock.moko.mvvm.livedata.MutableLiveData
import dev.icerock.moko.mvvm.livedata.readOnly
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.launch
import kotlin.random.Random

fun createSimpleViewModel(): SimpleViewModel {
    val snackBarErrorPresenter = SnackBarErrorPresenter(
        duration = SnackBarDuration.SHORT
    )
    val toastErrorPresenter = ToastErrorPresenter(
        duration = ToastDuration.LONG
    )
    return SimpleViewModel(
        exceptionHandler = ExceptionHandler(
            errorPresenter = SelectorErrorPresenter { throwable ->
                when (throwable) {
                    is CustomException -> snackBarErrorPresenter
                    else -> toastErrorPresenter
                }
            },
            exceptionMapper = ExceptionMappersStorage.throwableMapper(),
            onCatch = {
                // E.g. here we can log all exceptions that are handled by ExceptionHandler
                println("Got exception: $it")
            }
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
            }.catch<CustomException> {
                println("Got CustomException!")
                false
            }.finally {
                _isLoading.value = false
            }.execute()
        }
    }

    private var exceptionFlag = false

    // Simulates a server response with an error
    @Suppress("MagicNumber")
    private suspend fun serverRequest() {
        if (exceptionFlag) {
            exceptionFlag = !exceptionFlag
            throw CustomException(Random.nextInt(2) * 10)
        } else {
            exceptionFlag = !exceptionFlag
            throw IllegalArgumentException()
        }
    }
}
