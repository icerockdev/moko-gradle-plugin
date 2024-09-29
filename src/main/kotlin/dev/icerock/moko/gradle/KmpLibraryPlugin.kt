/*
 * Copyright 2022 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.gradle

import dev.icerock.moko.gradle.utils.connectTargetsToSourceSet
import dev.icerock.moko.gradle.utils.createMainTest
import dev.icerock.moko.gradle.utils.setupDependency
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class KmpLibraryPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target.plugins) {
            apply(KmmLibraryPlugin::class.java)
        }

        target.configure<KotlinMultiplatformExtension> {
            // JVM
            jvm()
            // JS
            js(IR) {
                browser()
                nodejs()
            }
            // linux
            linuxX64()
            // macOS
            macosArm64()
            macosX64()
            // watchOS
            watchosX64()
            watchosArm32()
            watchosArm64()
            watchosSimulatorArm64()
            // tvOS
            tvosArm64()
            tvosSimulatorArm64()
            tvosX64()
            // windows
            mingwX64()

            with(this.sourceSets) {
                val appleTargets = listOf(
                    "ios",
                    "macosArm64",
                    "macosX64",
                    "watchosX64",
                    "watchosArm32",
                    "watchosArm64",
                    "watchosSimulatorArm64",
                    "tvosArm64",
                    "tvosSimulatorArm64",
                    "tvosX64"
                )
                val nativeTargets = appleTargets + listOf(
                    "linuxX64",
                    "mingwX64"
                )
                val targetWithoutAndroid = nativeTargets + listOf(
                    "js",
                    "jvm",
                )

                createMainTest("nonAndroid")
                setupDependency("nonAndroid", "common")
                connectTargetsToSourceSet(targetNames = targetWithoutAndroid, sourceSetPrefix = "nonAndroid")

                createMainTest("native")
                setupDependency("native", "nonAndroid")
                connectTargetsToSourceSet(targetNames = nativeTargets, sourceSetPrefix = "native")

                createMainTest("apple")
                setupDependency("apple", "native")
                setupDependency("ios", "apple")
                connectTargetsToSourceSet(targetNames = appleTargets, sourceSetPrefix = "apple")

                createMainTest("mobile")
                setupDependency("mobile", "common")
                setupDependency("android", "mobile")
                setupDependency("ios", "mobile")
            }
        }
    }
}
