/*
 * Copyright 2022 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.gradle.utils

import org.gradle.api.NamedDomainObjectContainer
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet

fun NamedDomainObjectContainer<KotlinSourceSet>.connectTargetsToSourceSet(
    targetNames: List<String>,
    sourceSetPrefix: String,
) {
    targetNames.forEach { setupDependency(name = it, dependencyName = sourceSetPrefix) }
}

fun NamedDomainObjectContainer<KotlinSourceSet>.setupDependency(name: String, dependencyName: String) {
    getByName("${name}Main").dependsOn(getByName("${dependencyName}Main"))
    getByName("${name}Test").dependsOn(getByName("${dependencyName}Test"))
}

fun NamedDomainObjectContainer<KotlinSourceSet>.createMainTest(name: String) {
    create("${name}Main")
    create("${name}Test")
}
