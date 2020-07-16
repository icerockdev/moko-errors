/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("TooGenericExceptionCaught")

package dev.icerock.moko.errors.handler

import dev.icerock.moko.errors.ErrorEventListener
import dev.icerock.moko.errors.HandlerResult
import dev.icerock.moko.errors.presenters.ErrorPresenter
import dev.icerock.moko.mvvm.dispatcher.EventsDispatcher
import kotlin.reflect.KClass

typealias Catcher = (Throwable) -> Boolean

class ExceptionHandlerContext<T : Any, R> internal constructor(
    private val errorPresenter: ErrorPresenter<T>,
    private val eventsDispatcher: EventsDispatcher<ErrorEventListener<T>>,
    private val onCatch: ((Throwable) -> Unit)?,
    private val block: suspend () -> R
) {
    val catchersMap = mutableMapOf<KClass<*>, Catcher>()

    private var finallyBlock: (() -> Unit)? = null

    inline fun <reified E : Throwable> catch(
        noinline catcher: (E) -> Boolean
    ): ExceptionHandlerContext<T, R> {
        catchersMap[E::class] = catcher as Catcher
        return this
    }

    fun finally(block: () -> Unit): ExceptionHandlerContext<T, R> {
        finallyBlock = block
        return this
    }

    suspend fun execute(): HandlerResult<R, Throwable> {
        return try {
            HandlerResult.Success(block())
        } catch (e: Throwable) {
            onCatch?.invoke(e)
            val isHandled = catchersMap[e::class]?.invoke(e)
            if (isHandled == null || isHandled == false) {
                errorPresenter.sendErrorEvent(eventsDispatcher, e)
            }

            HandlerResult.Failure(e)
        } finally {
            finallyBlock?.invoke()
        }
    }
}
