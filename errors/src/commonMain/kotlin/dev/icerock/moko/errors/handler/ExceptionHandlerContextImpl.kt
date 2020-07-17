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

private typealias Catcher = (Throwable) -> Boolean

internal class ExceptionHandlerContextImpl<T : Any, R>(
    private val errorPresenter: ErrorPresenter<T>,
    private val eventsDispatcher: EventsDispatcher<ErrorEventListener<T>>,
    private val onCatch: ((Throwable) -> Unit)?,
    private val block: suspend () -> R
) : ExceptionHandlerContext<R>() {
    private val catchersMap = mutableMapOf<KClass<*>, Catcher>()

    private var finallyBlock: (() -> Unit)? = null

    override fun <E : Throwable> catch(
        clazz: KClass<E>,
        catcher: (E) -> Boolean
    ): ExceptionHandlerContext<R> {
        catchersMap[clazz] = catcher as Catcher
        return this
    }

    override fun finally(block: () -> Unit): ExceptionHandlerContext<R> {
        finallyBlock = block
        return this
    }

    override suspend fun execute(): HandlerResult<R, Throwable> {
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
