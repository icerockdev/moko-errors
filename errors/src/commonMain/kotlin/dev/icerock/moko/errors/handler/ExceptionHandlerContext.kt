/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.errors.handler

import dev.icerock.moko.errors.ErrorEventListener
import dev.icerock.moko.errors.HandlerResult
import dev.icerock.moko.mvvm.dispatcher.EventsDispatcher
import kotlin.reflect.KClass

typealias Catcher = (Throwable) -> Boolean

class ExceptionHandlerContext<T> internal constructor(
    private val eventsDispatcher: EventsDispatcher<ErrorEventListener>,
    private val block: suspend () -> T
) {
    val catchersMap = mutableMapOf<KClass<*>, Catcher>()

    private var finallyBlock: (() -> Unit)? = null

    inline fun <reified E : Throwable> catch(
        noinline catcher: (E) -> Boolean
    ): ExceptionHandlerContext<T> {
        catchersMap[E::class] = catcher as Catcher
        return this
    }

    fun finally(block: () -> Unit): ExceptionHandlerContext<T> {
        finallyBlock = block
        return this
    }

    suspend fun execute(): HandlerResult<T, Throwable> {
        return try {
            HandlerResult.Success(block())
        } catch (e: Throwable) {
            val isHandled = catchersMap[e::class]?.invoke(e)
            if(isHandled == null || isHandled == false) {
                eventsDispatcher.dispatchEvent {
                    showError(e)
                }
            }

            HandlerResult.Failure(e)
        } finally {
            finallyBlock?.invoke()
        }
    }
}
