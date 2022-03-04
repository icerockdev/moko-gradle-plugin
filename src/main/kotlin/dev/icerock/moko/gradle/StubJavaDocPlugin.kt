/*
 * Copyright 2022 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.withType

class StubJavaDocPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target.plugins) {
            apply("org.gradle.maven-publish")
        }

        val javadocJar = target.tasks.register("javadocJar", Jar::class) {
            archiveClassifier.set("javadoc")
        }

        target.configure<PublishingExtension> {
            publications.withType<MavenPublication> {
                // Stub javadoc.jar artifact
                artifact(javadocJar.get())
            }
        }
    }
}
