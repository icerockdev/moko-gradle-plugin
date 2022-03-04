/*
 * Copyright 2022 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.gradle.utils

import org.gradle.api.GradleException
import org.gradle.api.Project

internal fun <T> Project.requiredProperty(name: String, mapper: (Any) -> T): T {
    val propertyName = "moko.$name"
    val anyValue: Any = property(propertyName)
        ?: throw GradleException("Required property $propertyName not defined!")

    return try {
        mapper(anyValue)
    } catch (exc: Exception) {
        throw GradleException("Can't map property $propertyName to required type", exc)
    }
}

internal fun Project.requiredIntProperty(name: String): Int {
    return requiredProperty(name) { it.toString().toInt() }
}

internal fun Project.requiredStringProperty(name: String): String {
    return requiredProperty(name) { it.toString() }
}
