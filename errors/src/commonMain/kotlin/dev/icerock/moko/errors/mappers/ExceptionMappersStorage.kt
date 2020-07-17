/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.errors.mappers

import dev.icerock.moko.errors.MR
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import kotlin.native.concurrent.ThreadLocal
import kotlin.reflect.KClass

internal typealias ThrowableMapper = (Throwable) -> Any

@Suppress("TooManyFunctions")
@ThreadLocal
object ExceptionMappersStorage {

    private val fallbackValuesMap: MutableMap<KClass<out Any>, Any> = mutableMapOf(
        StringDesc::class to MR.strings.moko_errors_unknownError.desc()
    )

    private val mappersMap: MutableMap<KClass<out Any>, MutableMap<KClass<out Throwable>, ThrowableMapper>> =
        mutableMapOf()
    private val conditionMappers: MutableMap<KClass<out Any>, MutableList<ConditionPair>> =
        mutableMapOf()

    /**
     * Register simple mapper (E) -> T.
     */
    fun <T : Any, E : Throwable> register(
        resultClass: KClass<T>,
        exceptionClass: KClass<E>,
        mapper: (E) -> T
    ): ExceptionMappersStorage {
        if (!mappersMap.containsKey(resultClass)) {
            mappersMap[resultClass] = mutableMapOf()
        }
        mappersMap.get(resultClass)?.put(exceptionClass, mapper as ThrowableMapper)
        return this
    }

    /**
     * Register mapper (E) -> T with condition (Throwable) -> Boolean.
     */
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
            mapper as ThrowableMapper
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

    /**
     * Sets fallback (default) value for [T] errors type.
     */
    fun <T : Any> setFallbackValue(clazz: KClass<T>, value: T): ExceptionMappersStorage {
        fallbackValuesMap[clazz] = value
        return ExceptionMappersStorage
    }

    /**
     * Sets fallback (default) value for [T] errors type.
     */
    inline fun <reified T : Any> setFallbackValue(value: T): ExceptionMappersStorage =
        setFallbackValue(T::class, value)

    /**
     * Returns fallback (default) value for [T] errors type.
     * If there is no default value for the class [T], then [FallbackValueNotFoundException]
     * exception will be thrown.
     */
    fun <T : Any> getFallbackValue(clazz: KClass<T>): T {
        return fallbackValuesMap[clazz] as? T
            ?: throw FallbackValueNotFoundException(clazz)
    }

    /**
     * Returns fallback (default) value for [T] errors type.
     * If there is no default value for the class [T], then [FallbackValueNotFoundException]
     * exception will be thrown.
     */
    inline fun <reified T : Any> getFallbackValue(): T = getFallbackValue(T::class)

    /**
     * Factory method that creates mappers (Throwable) -> T with a registered fallback value for
     * class [T].
     */
    fun <E : Throwable, T : Any> throwableMapper(clazz: KClass<T>): (e: E) -> T {
        val fallback = getFallbackValue(clazz)
        return { e ->
            find(clazz, e, e::class)?.invoke(e) ?: fallback
        }
    }

    /**
     * Factory method that creates mappers (Throwable) -> T with a registered fallback value for
     * class [T].
     */
    inline fun <E : Throwable, reified T : Any> throwableMapper(): (e: E) -> T {
        return throwableMapper(T::class)
    }
}
