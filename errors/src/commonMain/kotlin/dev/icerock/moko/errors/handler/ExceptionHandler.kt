/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.errors.handler

interface ExceptionHandler : ExceptionHandlerBinder {
    fun <R> handle(block: suspend () -> R): ExceptionHandlerContext<R>
}
