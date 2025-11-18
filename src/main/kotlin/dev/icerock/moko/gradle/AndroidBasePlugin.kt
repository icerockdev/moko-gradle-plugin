/*
 * Copyright 2022 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.gradle

import com.android.build.gradle.BaseExtension
import dev.icerock.moko.gradle.utils.requiredIntProperty
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

class AndroidBasePlugin : Plugin<Project> {

    override fun apply(target: Project) {
        val targetSdkValue: Int = target.requiredIntProperty("android.targetSdk")
        val compileSdkValue: Int = target.requiredIntProperty("android.compileSdk")
        val minSdkValue: Int = target.requiredIntProperty("android.minSdk")

        target.configure<BaseExtension> {
            compileSdkVersion(compileSdkValue)

            defaultConfig {
                minSdk = minSdkValue
                targetSdk = targetSdkValue
            }

            compileOptions {
                sourceCompatibility = JavaVersion.VERSION_11
                targetCompatibility = JavaVersion.VERSION_11
            }
        }

        target.tasks
            .withType<KotlinCompile>()
            .configureEach {
                compilerOptions.jvmTarget.set(JvmTarget.JVM_11)
            }
    }
}
