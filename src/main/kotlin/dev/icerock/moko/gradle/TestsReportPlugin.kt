/*
 * Copyright 2022 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.AbstractTestTask
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.gradle.kotlin.dsl.withType

class TestsReportPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.tasks.withType<AbstractTestTask> {
            testLogging {
                exceptionFormat = TestExceptionFormat.FULL
                events = setOf(
                    TestLogEvent.SKIPPED,
                    TestLogEvent.PASSED,
                    TestLogEvent.FAILED
                )
                showStandardStreams = true
            }
            outputs.upToDateWhen { false }
        }
    }
}
