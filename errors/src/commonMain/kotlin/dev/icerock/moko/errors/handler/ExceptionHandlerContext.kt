/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.errors.handler

import dev.icerock.moko.errors.HandlerResult
import kotlin.reflect.KClass

interface ExceptionHandlerContext<R> {
    fun <E : Throwable> catch(clazz: KClass<E>, catcher: (E) -> Boolean): ExceptionHandlerContext<R>
    fun finally(block: () -> Unit): ExceptionHandlerContext<R>
    suspend fun execute(): HandlerResult<R, Throwable>
}

inline fun <reified E : Throwable> ExceptionHandlerContext<*>.catch(
    noinline catcher: (E) -> Boolean
): ExceptionHandlerContext<*> {
    return catch(E::class, catcher)
}
