/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.errors.registry

data class ConditionPair(
    val condition: (BasicException) -> Boolean,
    val mapper: ExceptionMapper
)
