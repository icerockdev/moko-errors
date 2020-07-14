/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.errors

sealed class HandlerResult<out T : Any?, out E : Throwable> {
    class Success<out T : Any?>(val value: T) : HandlerResult<T, Nothing>()
    class Failure<out E : Throwable>(val error: E) : HandlerResult<Nothing, E>()
}
