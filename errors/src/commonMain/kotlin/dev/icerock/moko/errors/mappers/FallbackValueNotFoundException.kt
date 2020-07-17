/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.errors.mappers

import kotlin.reflect.KClass

class FallbackValueNotFoundException(val clazz: KClass<*>) :
    Exception("There is no fallback value for class [$clazz]")
