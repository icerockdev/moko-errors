/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.errors

interface ErrorEventListener<T> {
    fun showError(exception: Throwable, data: T)
}
