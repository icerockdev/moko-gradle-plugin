/*
 * Copyright 2022 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.gradle

import io.github.gradlenexus.publishplugin.NexusPublishExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import java.net.URI

class NexusPublicationPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target.plugins) {
            apply("io.github.gradle-nexus.publish-plugin")
        }

        target.configure<NexusPublishExtension> {
            repositories {
                sonatype {
                    nexusUrl.set(URI.create("https://s01.oss.sonatype.org/service/local/"))
                    username.set(System.getenv("OSSRH_USER"))
                    password.set(System.getenv("OSSRH_KEY"))
                }
            }
        }
    }
}
