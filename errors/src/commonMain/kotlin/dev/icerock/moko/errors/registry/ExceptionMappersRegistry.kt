/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.errors.registry

import dev.icerock.moko.errors.MR
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import kotlin.native.concurrent.ThreadLocal
import kotlin.reflect.KClass

typealias BasicException = Throwable
internal typealias ExceptionMapper = (BasicException) -> Any

@ThreadLocal
object ExceptionMappersRegistry {

    var unknownErrorText: StringDesc = MR.strings.moko_errors_unknownError.desc()

    private val mappersMap: MutableMap<KClass<out Any>, MutableMap<KClass<out BasicException>, ExceptionMapper>> =
        mutableMapOf()
    private val conditionMappers: MutableMap<KClass<out Any>, MutableList<ConditionPair>> =
        mutableMapOf()

    fun <T : Any, E : BasicException> register(
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

    fun <T : Any> register(
        resultClass: KClass<T>,
        conditionPair: ConditionPair
    ): ExceptionMappersRegistry {
        if (!conditionMappers.containsKey(resultClass)) {
            conditionMappers[resultClass] = mutableListOf()
        }
        conditionMappers.get(resultClass)?.add(conditionPair)
        return this
    }

    inline fun <reified E : BasicException, reified T : Any> register(
        noinline mapper: (E) -> T
    ): ExceptionMappersRegistry {
        return register(T::class, E::class, mapper)
    }

    inline fun <reified T : Any> condition(
        noinline condition: (BasicException) -> Boolean,
        noinline mapper: (BasicException) -> T
    ): ExceptionMappersRegistry = register(
        resultClass = T::class,
        conditionPair = ConditionPair(condition, mapper as ExceptionMapper)
    )

    fun <T : Any, E : BasicException> find(
        resultClass: KClass<T>,
        exception: E,
        exceptionClass: KClass<out E>
    ): ((E) -> T)? {
        return conditionMappers.get(resultClass)
            ?.find { it.condition(exception) }
            ?.mapper as? ((E) -> T)
            ?: mappersMap.get(resultClass)?.get(exceptionClass) as? ((E) -> T)
    }

    inline fun <reified T : Any, E : BasicException> find(exception: E): ((E) -> T)? =
        find(T::class, exception, exception::class)

    fun setUnknownErrorText(text: StringDesc): ExceptionMappersRegistry {
        unknownErrorText = text
        return this
    }
}

fun <E : BasicException> ExceptionMappersRegistry.throwableToStringDesc(e: E): StringDesc {
    return find<StringDesc, E>(e)?.invoke(e) ?: unknownErrorText
}
