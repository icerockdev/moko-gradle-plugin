/*
 * Copyright 2022 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.gradle

import com.android.build.gradle.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidLibraryPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target.plugins) {
            apply("com.android.library")
            apply("kotlin-android")
            apply(AndroidBasePlugin::class.java)
        }

        target.configure<LibraryExtension> {
            sourceSets.all { java.srcDir("src/$name/kotlin") }
        }
    }
}
