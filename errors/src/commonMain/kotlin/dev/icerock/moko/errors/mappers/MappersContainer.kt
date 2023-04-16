/*
 * Copyright 2023 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.errors.mappers

internal data class MappersContainer<T>(
    val mappers: List<ThrowableMapperItem<T>>,
    val fallback: ThrowableMapper<T>
)
