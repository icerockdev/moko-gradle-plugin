/*
 * Copyright 2022 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.gradle

import io.gitlab.arturbosch.detekt.Detekt
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.TaskCollection
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType

class DetektPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target.plugins) {
            apply("io.gitlab.arturbosch.detekt")
        }

        val detektTasks: TaskCollection<Detekt> = target.tasks.withType()
        target.afterEvaluate {
            detektTasks.configureEach {
                this.source = source.matching {
                    exclude {
                        it.file.path.startsWith(target.buildDir.path)
                    }
                }
            }
        }
        val detektTask = target.tasks.register("detektWithoutTests") {
            group = "verification"

            dependsOn(detektTasks.matching { it.name.contains("Test").not() })
        }
        target.tasks.getByName("check").dependsOn(detektTask)

        target.dependencies {
            "detektPlugins"("io.gitlab.arturbosch.detekt:detekt-formatting:1.19.0")
        }
    }
}
