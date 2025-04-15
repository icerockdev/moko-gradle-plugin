/*
 * Copyright 2022 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.gradle

import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidAppPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target.plugins) {
            apply("com.android.application")
            apply("kotlin-android")
            apply(AndroidBasePlugin::class.java)
        }

        target.configure<ApplicationExtension> {
            buildTypes {
                getByName("release") {
                    isMinifyEnabled = true
                    proguardFiles(
                        getDefaultProguardFile("proguard-android.txt"),
                        "proguard-rules.pro"
                    )
                }
                getByName("debug") {
                    isDebuggable = true
                    applicationIdSuffix = ".debug"
                }
            }

            packaging {
                resources.excludes.add("META-INF/*.kotlin_module")
                resources.excludes.add("META-INF/AL2.0")
                resources.excludes.add("META-INF/LGPL2.1")
            }
        }
    }
}
