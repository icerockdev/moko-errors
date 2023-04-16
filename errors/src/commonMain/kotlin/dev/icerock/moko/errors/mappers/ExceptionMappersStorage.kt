/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.errors.mappers

import dev.icerock.moko.errors.MR
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import kotlin.native.concurrent.ThreadLocal
import kotlin.reflect.KClass

@Suppress("TooManyFunctions")
@ThreadLocal
object ExceptionMappersStorage {

    private val containers: MutableMap<KClass<*>, MappersContainer<*>> = mutableMapOf(
        StringDesc::class to MappersContainer<StringDesc>(
            mappers = emptyList(),
            fallback = { MR.strings.moko_errors_unknownError.desc() }
        )
    )
    private val notifiers: MutableList<(Throwable, KClass<*>, Any) -> Unit> = mutableListOf()

    private fun <T : Any> getOrCreateContainer(resultClass: KClass<T>): MappersContainer<T> {
        val existContainer: MappersContainer<*>? = containers[resultClass]
        if (existContainer != null) return existContainer as MappersContainer<T>

        return MappersContainer<T>(
            mappers = emptyList(),
            fallback = { throw FallbackValueNotFoundException(resultClass) }
        ).also { containers[resultClass] = it }
    }

    private fun <T : Any> updateContainer(
        resultClass: KClass<T>,
        block: (MappersContainer<T>) -> MappersContainer<T>
    ) {
        val container: MappersContainer<T> = getOrCreateContainer(resultClass)
        containers[resultClass] = block(container)
    }

    /**
     * Register simple mapper (E) -> T.
     */
    fun <T : Any, E : Throwable> register(
        resultClass: KClass<T>,
        exceptionClass: KClass<E>,
        mapper: (E) -> T
    ): ExceptionMappersStorage {
        updateContainer(
            resultClass
        ) { container ->
            container.copy(
                mappers = container.mappers + ThrowableMapperItem(
                    mapper = { mapper(it as E) },
                    isApplied = { it::class == exceptionClass }
                )
            )
        }
        return this
    }

    /**
     * Register mapper (E) -> T with condition (Throwable) -> Boolean.
     */
    fun <T : Any> register(
        resultClass: KClass<T>,
        isApplied: (Throwable) -> Boolean,
        mapper: (Throwable) -> T
    ): ExceptionMappersStorage {
        updateContainer(
            resultClass
        ) { container ->
            container.copy(
                mappers = container.mappers + ThrowableMapperItem(
                    mapper = mapper,
                    isApplied = isApplied
                )
            )
        }
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
        isApplied = condition,
        mapper = mapper
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
        throwable: E
    ): ((E) -> T)? {
        val container: MappersContainer<T>? = containers[resultClass] as MappersContainer<T>?

        if (container == null && throwable !is Exception) {
            throw throwable
        } else if (container == null) {
            return null
        }

        val mapper: (Throwable) -> T = container.mappers
            .firstOrNull { it.isApplied(throwable) }
            ?.mapper
            ?: container.fallback

        return { exception ->
            val result: T = mapper(exception)
            notifiers.forEach { notifier ->
                notifier(exception, resultClass, result)
            }
            result
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
        throwable = throwable
    )

    /**
     * Sets fallback (default) value for [T] errors type.
     */
    fun <T : Any> setFallbackValue(clazz: KClass<T>, value: T): ExceptionMappersStorage {
        updateContainer(
            clazz
        ) { container ->
            container.copy(
                fallback = { value }
            )
        }
        return this
    }

    /**
     * Sets fallback (default) value for [T] errors type.
     */
    inline fun <reified T : Any> setFallbackValue(value: T): ExceptionMappersStorage =
        setFallbackValue(T::class, value)

    /**
     * Sets fallback (default) factory for [T] errors type.
     */
    fun <T : Any> setFallbackFactory(
        clazz: KClass<T>,
        factory: (Throwable) -> T
    ): ExceptionMappersStorage {
        updateContainer(
            clazz
        ) { container ->
            container.copy(
                fallback = factory
            )
        }
        return this
    }

    inline fun <reified T : Any> setFallbackFactory(
        noinline factory: (Throwable) -> T
    ): ExceptionMappersStorage = setFallbackFactory(T::class, factory)

    /**
     * Factory method that creates mappers (Throwable) -> T with a registered fallback value for
     * class [T].
     */
    fun <E : Throwable, T : Any> throwableMapper(clazz: KClass<T>): (e: E) -> T {
        return { e ->
            find(clazz, e)?.invoke(e) ?: throw FallbackValueNotFoundException(clazz)
        }
    }

    inline fun <E : Throwable, reified T : Any> throwableMapper(): (e: E) -> T {
        return dev.icerock.moko.errors.mappers.throwableMapper()
    }

    /**
     * Listen all mappers calls. Useful for logging
     *
     * @param block - lambda that will be called when exception map to some class
     */
    fun onEach(
        block: (Throwable, KClass<*>, Any) -> Unit
    ): ExceptionMappersStorage {
        notifiers.add(block)
        return this
    }
}

/**
 * Factory method that creates mappers (Throwable) -> T with a registered fallback value for
 * class [T].
 */
inline fun <E : Throwable, reified T : Any> throwableMapper(): (e: E) -> T {
    return ExceptionMappersStorage.throwableMapper(T::class)
}

/**
 * Factory method that allows getting exception description
 */
inline fun <reified E : Throwable, reified T : Any> E.mapThrowable(): T {
    return ExceptionMappersStorage.throwableMapper<E, T>(T::class)(this)
}
