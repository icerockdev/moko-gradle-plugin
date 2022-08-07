/*
 * Copyright 2022 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.tasks.AbstractPublishToMaven
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType

class HostsPublicationPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.plugins.withId("org.gradle.maven-publish") {
            target.configure<PublishingExtension> {
                val publicationsFromMainHost = listOf(
                    "wasm32",
                    "jvm",
                    "js",
                    "kotlinMultiplatform",
                    "androidRelease",
                    "androidDebug",
                    "linuxArm64",
                    "linuxArm32Hfp",
                    "linuxX64"
                )

                target.tasks.withType<AbstractPublishToMaven>()
                    .matching { it.publication?.name in publicationsFromMainHost }
                    .configureEach { onlyIf { System.getProperty("IS_MAIN_HOST") == "true" } }
            }
        }
    }
}
