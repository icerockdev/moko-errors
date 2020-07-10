/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.errors

import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import kotlin.native.concurrent.ThreadLocal
import kotlin.reflect.KClass

internal typealias ExceptionMapper = (Throwable) -> Any

@ThreadLocal
object ExceptionMappersRegistry {

    var unknownErrorText: StringDesc = "Unknown error".desc()

    private val mappersMap: MutableMap<KClass<out Any>, MutableMap<KClass<out Throwable>, ExceptionMapper>> =
        mutableMapOf()

    fun <T : Any, E : Throwable> register(
        resultClass: KClass<T>,
        exceptionClass: KClass<E>,
        mapper: (E) -> T
    ): ExceptionMappersRegistry {
        if (!mappersMap.containsKey(resultClass)) {
            mappersMap[resultClass] = mutableMapOf()
        }
        mappersMap.get(resultClass)?.put(exceptionClass, mapper as ExceptionMapper)
        return this
    }

    inline fun <reified E : Throwable, reified T : Any> register(
        noinline mapper: (E) -> T
    ): ExceptionMappersRegistry {
        return register(T::class, E::class, mapper)
    }

    fun <T : Any, E : Throwable> find(
        resultClass: KClass<T>,
        exceptionClass: KClass<out E>
    ): ((E) -> T)? {
        return mappersMap.get(resultClass)?.get(exceptionClass) as? (E) -> T
    }

    inline fun <reified T : Any, E : Throwable> find(exception: E): ((E) -> T)? =
        find(T::class, exception::class)

    fun setUnknownErrorText(text: StringDesc): ExceptionMappersRegistry {
        unknownErrorText = text
        return this
    }
}

fun <E : Throwable> ExceptionMappersRegistry.throwableToStringDesc(e: E): StringDesc {
    return find<StringDesc, E>(e)?.invoke(e) ?: unknownErrorText
}
