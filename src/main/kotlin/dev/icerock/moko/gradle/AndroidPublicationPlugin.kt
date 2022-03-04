/*
 * Copyright 2022 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.configure

class AndroidPublicationPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target.plugins) {
            apply(PublicationPlugin::class.java)
        }

        target.configure<PublishingExtension> {
            publications.create("release", MavenPublication::class.java) {
                from(target.components.getByName("release"))
            }
        }
    }
}
