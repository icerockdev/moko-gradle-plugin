/*
 * Copyright 2022 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.gradle

import dev.icerock.moko.gradle.utils.requiredStringProperty
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withType
import org.gradle.plugins.signing.SigningExtension
import java.util.Base64

class PublicationPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target.plugins) {
            apply("org.gradle.maven-publish")
            apply("signing")
        }

        val libraryName: String = target.requiredStringProperty("publish.name")
        val description: String = target.requiredStringProperty("publish.description")
        val gitHubOrganization: String = target.requiredStringProperty("publish.repo.org")
        val gitHubName: String = target.requiredStringProperty("publish.repo.name")
        val license: String = target.requiredStringProperty("publish.license")
        val developersString: String = target.requiredStringProperty("publish.developers")
        val gitHubUrl = "https://github.com/$gitHubOrganization/$gitHubName"
        val sshUrl = "scm:git:ssh://github.com/$gitHubOrganization/$gitHubName.git"
        val developersList: List<Developer> = developersString.split(",").map { parseDeveloper(it) }

        target.configure<PublishingExtension> {
            publications.withType<MavenPublication> {
                // Provide artifacts information required by Maven Central
                pom {
                    this.name.set(libraryName)
                    this.description.set(description)
                    this.url.set(gitHubUrl)
                    licenses {
                        license {
                            this.name.set(license)
                            this.distribution.set("repo")
                            this.url.set("$gitHubUrl/blob/master/LICENSE.md")
                        }
                    }

                    developers {
                        developersList.forEach { dev ->
                            developer {
                                id.set(dev.id)
                                name.set(dev.name)
                                email.set(dev.email)
                            }
                        }
                    }

                    scm {
                        this.connection.set(sshUrl)
                        this.developerConnection.set(sshUrl)
                        this.url.set(gitHubUrl)
                    }
                }
            }
        }

        target.configure<SigningExtension> {
            val signingKeyId: String? = System.getenv("SIGNING_KEY_ID")
            val signingPassword: String? = System.getenv("SIGNING_PASSWORD")
            val signingKey: String? = System.getenv("SIGNING_KEY")?.let { base64Key ->
                String(Base64.getDecoder().decode(base64Key))
            }

            if (signingKeyId != null) {
                useInMemoryPgpKeys(signingKeyId, signingKey, signingPassword)
                sign(configuration.artifacts.toString())
            }
        }
    }

    private fun parseDeveloper(text: String): Developer {
        val items: List<String> = text.split("|").map { it.trim() }
        if (items.size != 3) throw GradleException("Developer profile should have 3 parts with | delimiter. For example: alex009|Aleksey Mikhailov|Aleksey.Mikhailov@icerockdev.com")
        return Developer(
            id = items[0],
            name = items[1],
            email = items[2]
        )
    }

    private data class Developer(
        val id: String,
        val name: String,
        val email: String,
    )
}
