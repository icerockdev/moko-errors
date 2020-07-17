package dev.icerock.moko.errors.mappers

import kotlin.reflect.KClass

class FallbackValueNotFoundException(val clazz: KClass<*>) :
    Exception("There is no fallback value for class [$clazz]")
