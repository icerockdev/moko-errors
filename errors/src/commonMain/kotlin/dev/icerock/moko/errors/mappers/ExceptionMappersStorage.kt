/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.errors.mappers

import dev.icerock.moko.errors.MR
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import kotlin.native.concurrent.ThreadLocal
import kotlin.reflect.KClass

internal typealias ExceptionMapper = (Throwable) -> Any

@ThreadLocal
object ExceptionMappersStorage {

    private val fallbackValuesMap: MutableMap<KClass<out Any>, Any> = mutableMapOf()

    private val mappersMap: MutableMap<KClass<out Any>, MutableMap<KClass<out Throwable>, ExceptionMapper>> =
        mutableMapOf()
    private val conditionMappers: MutableMap<KClass<out Any>, MutableList<ConditionPair>> =
        mutableMapOf()

    init {
        fallbackValuesMap[StringDesc::class] = MR.strings.moko_errors_unknownError.desc()
    }

    fun <T : Any, E : Throwable> register(
        resultClass: KClass<T>,
        exceptionClass: KClass<E>,
        mapper: (E) -> T
    ): ExceptionMappersStorage {
        if (!mappersMap.containsKey(resultClass)) {
            mappersMap[resultClass] = mutableMapOf()
        }
        mappersMap.get(resultClass)?.put(exceptionClass, mapper as ExceptionMapper)
        return this
    }

    fun <T : Any> register(
        resultClass: KClass<T>,
        conditionPair: ConditionPair
    ): ExceptionMappersStorage {
        if (!conditionMappers.containsKey(resultClass)) {
            conditionMappers[resultClass] = mutableListOf()
        }
        conditionMappers.get(resultClass)?.add(conditionPair)
        return this
    }

    /**
     * Register simple mapper (E) -> T.
     */
    inline fun <reified E : Throwable, reified T : Any> register(
        noinline mapper: (E) -> T
    ): ExceptionMappersStorage {
        return register(
            T::class,
            E::class,
            mapper
        )
    }

    /**
     * Registers mapper (Throwable) -> T with specific condition (Throwable) -> Boolean.
     */
    inline fun <reified T : Any> condition(
        noinline condition: (Throwable) -> Boolean,
        noinline mapper: (Throwable) -> T
    ): ExceptionMappersStorage = register(
        resultClass = T::class,
        conditionPair = ConditionPair(
            condition,
            mapper as ExceptionMapper
        )
    )

    /**
     * Tries to find mapper for [throwable] instance. First, a mapper with condition is
     * looked for. If mapper with condition was not found, then a simple mapper is looked for. If
     * the mapper was not found, it will return null.
     * If there is no mapper for the [throwable] of class [E] and [E] does't inherits
     * [kotlin.Exception], then exception will be rethrown.
     */
    fun <E : Throwable, T : Any> find(
        resultClass: KClass<T>,
        throwable: E,
        exceptionClass: KClass<out E>
    ): ((E) -> T)? {
        val mapper = conditionMappers.get(resultClass)
            ?.find { it.condition(throwable) }
            ?.mapper as? ((E) -> T)
            ?: mappersMap.get(resultClass)?.get(exceptionClass) as? ((E) -> T)

        return if (mapper == null && throwable !is Exception) {
            throw throwable
        } else {
            mapper
        }
    }

    /**
     * Tries to find mapper (E) -> T by [throwable] instance. First, a mapper with condition is
     * looked for. If mapper with condition was not found, then a simple mapper is looked for. If
     * the mapper was not found, it will return null.
     * If there is no mapper for the [throwable] of class [E] and [E] does't inherits
     * [kotlin.Exception], then exception will be rethrown.
     */
    inline fun <E : Throwable, reified T : Any> find(throwable: E): ((E) -> T)? = find(
        resultClass = T::class,
        throwable = throwable,
        exceptionClass = throwable::class
    )

    fun <T : Any> setFallbackValue(clazz: KClass<T>, value: T): ExceptionMappersStorage {
        fallbackValuesMap[clazz] = value
        return ExceptionMappersStorage
    }

    inline fun <reified T : Any> setFallbackValue(value: T): ExceptionMappersStorage =
        setFallbackValue(T::class, value)

    fun <T : Any> getFallbackValue(clazz: KClass<T>): T {
        return fallbackValuesMap[clazz] as? T
            ?: throw IllegalArgumentException("There is no fallback value for class $clazz")
    }

    inline fun <reified T : Any> getFallbackValue(): T = getFallbackValue(T::class)
}

fun <E : Throwable> ExceptionMappersStorage.throwableToStringDesc(e: E): StringDesc {
    return find<E, StringDesc>(e)?.invoke(e) ?: getFallbackValue()
}
