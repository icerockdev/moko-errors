/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.errors.presenters

expect class SelectorErrorPresenter<T : Any>(
    errorPresenterSelector: (Throwable) -> ErrorPresenter<T>
) : ErrorPresenter<T>