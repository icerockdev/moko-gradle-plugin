/*
 * Copyright 2022 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

import java.net.URI
import java.util.Base64

plugins {
    `kotlin-dsl`
    id("org.gradle.maven-publish")
    id("signing")
    id("java-gradle-plugin")
    id("io.github.gradle-nexus.publish-plugin") version "2.0.0"
}

group = "dev.icerock.moko"
version = libs.versions.mokoGradlePluginVersion.get()

repositories {
    mavenCentral()
    google()
    gradlePluginPortal()
}

dependencies {
    implementation(kotlin("stdlib"))
    api(libs.androidGradlePlugin)
    api(libs.kotlinGradlePlugin)
    api(libs.mobileMultiplatformGradlePlugin)
    api(libs.detektGradlePlugin)
    api(libs.nexusPublishGradlePlugin)
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
    withJavadocJar()
    withSourcesJar()
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "11"
    }
}

gradlePlugin {
    plugins {
        create("android-app") {
            id = "dev.icerock.moko.gradle.android.application"
            implementationClass = "dev.icerock.moko.gradle.AndroidAppPlugin"
            displayName = "MOKO Android Application Plugin"
            description = "Configures an Android application module with MOKO conventions."
        }

        create("android-library") {
            id = "dev.icerock.moko.gradle.android.library"
            implementationClass = "dev.icerock.moko.gradle.AndroidLibraryPlugin"
            displayName = "MOKO Android Library Plugin"
            description = "Configures an Android library module with MOKO build settings."
        }

        create("android-base") {
            id = "dev.icerock.moko.gradle.android.base"
            implementationClass = "dev.icerock.moko.gradle.AndroidBasePlugin"
            displayName = "MOKO Android Base Plugin"
            description = "Provides base configuration for Android projects in MOKO."
        }

        create("android-publication") {
            id = "dev.icerock.moko.gradle.android.publication"
            implementationClass = "dev.icerock.moko.gradle.AndroidPublicationPlugin"
            displayName = "MOKO Android Publication Plugin"
            description = "Handles publication setup for Android libraries in MOKO."
        }

        create("detekt") {
            id = "dev.icerock.moko.gradle.detekt"
            implementationClass = "dev.icerock.moko.gradle.DetektPlugin"
            displayName = "MOKO Detekt Plugin"
            description = "Integrates Detekt static code analysis with MOKO projects."
        }

        create("multiplatform-mobile") {
            id = "dev.icerock.moko.gradle.multiplatform.mobile"
            implementationClass = "dev.icerock.moko.gradle.KmmLibraryPlugin"
            displayName = "MOKO Multiplatform Mobile Plugin"
            description = "Configures Kotlin Multiplatform Mobile (KMM) modules for MOKO."
        }

        create("multiplatform-all") {
            id = "dev.icerock.moko.gradle.multiplatform.all"
            implementationClass = "dev.icerock.moko.gradle.KmpLibraryPlugin"
            displayName = "MOKO Multiplatform Plugin"
            description = "Applies MOKO conventions for full Kotlin Multiplatform (KMP) libraries."
        }

        create("publication") {
            id = "dev.icerock.moko.gradle.publication"
            implementationClass = "dev.icerock.moko.gradle.PublicationPlugin"
            displayName = "MOKO Publication Plugin"
            description = "Base plugin for handling Maven publication logic in MOKO."
        }

        create("publication-nexus") {
            id = "dev.icerock.moko.gradle.publication.nexus"
            implementationClass = "dev.icerock.moko.gradle.NexusPublicationPlugin"
            displayName = "MOKO Nexus Publication Plugin"
            description = "Publishes artifacts to Maven Central via Sonatype Nexus from MOKO."
        }

        create("publication-hosts") {
            id = "dev.icerock.moko.gradle.publication.hosts"
            implementationClass = "dev.icerock.moko.gradle.HostsPublicationPlugin"
            displayName = "MOKO Hosts Publication Plugin"
            description = "Configures conditional Maven publication logic to separate hosts-dependend publication."
        }

        create("stubjavadoc") {
            id = "dev.icerock.moko.gradle.stub.javadoc"
            implementationClass = "dev.icerock.moko.gradle.StubJavaDocPlugin"
            displayName = "MOKO Stub Javadoc Plugin"
            description = "Generates stub Javadoc artifacts required for Maven Central publication."
        }

        create("tests") {
            id = "dev.icerock.moko.gradle.tests"
            implementationClass = "dev.icerock.moko.gradle.TestsReportPlugin"
            displayName = "MOKO Tests Plugin"
            description = "Configures test tasks and reporting for MOKO projects."
        }

        create("jvm") {
            id = "dev.icerock.moko.gradle.jvm"
            implementationClass = "dev.icerock.moko.gradle.JvmPlugin"
            displayName = "MOKO JVM Plugin"
            description = "Applies default settings for JVM-only modules in MOKO projects."
        }
    }
}

nexusPublishing {
    repositories {
        // see https://central.sonatype.org/publish/publish-portal-ossrh-staging-api/#configuration
        sonatype {
            nexusUrl.set(uri("https://ossrh-staging-api.central.sonatype.com/service/local/"))
            snapshotRepositoryUrl.set(URI.create("https://central.sonatype.com/repository/maven-snapshots/"))
            username.set(System.getenv("OSSRH_USER"))
            password.set(System.getenv("OSSRH_KEY"))
        }
    }
}

publishing {
    publications.withType<MavenPublication>().configureEach {
        pom {
            name.set("MOKO gradle plugin")
            description.set("This is a Gradle plugin with common build logic for all MOKO libraries.")
            url.set("https://github.com/icerockdev/moko-gradle-plugin")
            licenses {
                license {
                    name.set("Apache-2.0")
                    distribution.set("repo")
                    url.set("https://github.com/icerockdev/moko-gradle-plugin/blob/master/LICENSE.md")
                }
            }

            developers {
                developer {
                    id.set("Alex009")
                    name.set("Aleksey Mikhailov")
                    email.set("aleksey.mikhailov@icerockdev.com")
                }
            }

            scm {
                connection.set("scm:git:ssh://github.com/icerockdev/moko-gradle-plugin.git")
                developerConnection.set("scm:git:ssh://github.com/icerockdev/moko-gradle-plugin.git")
                url.set("https://github.com/icerockdev/moko-gradle-plugin")
            }
        }
    }
}

signing {
    val signingKeyId: String? = System.getenv("SIGNING_KEY_ID")
    val signingPassword: String? = System.getenv("SIGNING_PASSWORD")
    val signingKey: String? = System.getenv("SIGNING_KEY")?.let { base64Key ->
        String(Base64.getDecoder().decode(base64Key))
    }

    if (signingKeyId != null) {
        useInMemoryPgpKeys(signingKeyId, signingKey, signingPassword)
        sign(publishing.publications)
    }

    val signingTasks = tasks.withType<Sign>()
    tasks.withType<PublishToMavenRepository>().configureEach {
        dependsOn(signingTasks)
    }
}
