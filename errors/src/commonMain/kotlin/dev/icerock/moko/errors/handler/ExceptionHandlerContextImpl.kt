/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.errors.handler

import dev.icerock.moko.errors.ErrorEventListener
import dev.icerock.moko.errors.HandlerResult
import dev.icerock.moko.mvvm.dispatcher.EventsDispatcher

private typealias Catcher = (Throwable) -> Boolean

internal class ExceptionHandlerContextImpl<T : Any, R>(
    private val exceptionMapper: ExceptionMapper<T>,
    private val eventsDispatcher: EventsDispatcher<ErrorEventListener<T>>,
    private val onCatch: ((Throwable) -> Unit)?,
    private val block: suspend () -> R
) : ExceptionHandlerContext<R>() {
    private val catchers = mutableListOf<Pair<(Throwable) -> Boolean, Catcher>>()

    private var finallyBlock: (() -> Unit)? = null

    @Suppress("UNCHECKED_CAST")
    override fun <E : Throwable> catch(
        condition: (Throwable) -> Boolean,
        catcher: (E) -> Boolean
    ): ExceptionHandlerContext<R> {
        catchers.add(condition to catcher as Catcher)
        return this
    }

    override fun finally(block: () -> Unit): ExceptionHandlerContext<R> {
        finallyBlock = block
        return this
    }

    @Suppress("TooGenericExceptionCaught")
    override suspend fun execute(): HandlerResult<R, Throwable> {
        return try {
            HandlerResult.Success(block())
        } catch (e: Throwable) {
            onCatch?.invoke(e)
            val isHandled = isHandledByCustomCatcher(e)
            if (!isHandled) { // If not handled by a custom catcher
                val errorValue = exceptionMapper(e)
                eventsDispatcher.dispatchEvent {
                    showError(e, errorValue)
                }
            }
            HandlerResult.Failure(e)
        } finally {
            finallyBlock?.invoke()
        }
    }

    private fun isHandledByCustomCatcher(cause: Throwable): Boolean {
        return catchers
            .firstOrNull { it.first.invoke(cause) } // Finds custom catcher by invoking conditions
            ?.second?.invoke(cause)                 // If catcher was found then execute it
            ?: false
    }
}
