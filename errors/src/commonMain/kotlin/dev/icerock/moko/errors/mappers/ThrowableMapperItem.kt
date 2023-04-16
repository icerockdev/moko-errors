/*
 * Copyright 2023 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.errors.mappers

internal typealias ThrowableMapper<T> = (Throwable) -> T

internal data class ThrowableMapperItem<T>(
    val mapper: ThrowableMapper<T>,
    val isApplied: (Throwable) -> Boolean
)
