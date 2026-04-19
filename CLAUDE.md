# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Overview

This is a Gradle convention plugin library (`io.github.khalid64927`) that packages common build logic for Kotlin Multiplatform (KMP/KMM) libraries — originally from IceRock's MOKO project. The plugin is published to Maven Central.

## Commands

```bash
# Build the plugin
./gradlew build

# Run checks (includes detekt linting)
./gradlew check

# Publish to OSSRH/Maven Central (requires env vars: OSSRH_USER, OSSRH_KEY, SIGNING_KEY_ID, SIGNING_PASSWORD, SIGNING_KEY)
./gradlew publishMavenJavaPublicationToOSSRHRepository

# Publish to local Maven repo for local testing
./gradlew publishToMavenLocal
```

There are no unit tests in this project — the plugins are integration-tested by consuming projects.

## Architecture

This is a single-module `kotlin-dsl` Gradle plugin project. All plugin implementations live in `src/main/kotlin/dev/icerock/moko/gradle/`.

### Plugin registry (`build.gradle.kts`)

Each plugin class is registered with a stable plugin ID in `gradlePlugin { plugins { ... } }`. The group is `io.github.khalid64927` and the version comes from `gradle/libs.versions.toml` (`mokoGradlePluginVersion`).

### Plugin hierarchy

- **`AndroidBasePlugin`** — configures `compileSdk`, `minSdk`, `targetSdk` from `gradle.properties` keys prefixed `moko.android.*`
- **`AndroidAppPlugin`** / **`AndroidLibraryPlugin`** — apply Android app/library plugin + `AndroidBasePlugin`
- **`KmmLibraryPlugin`** (`multiplatform.mobile`) — applies Android library + KMP + sets up Android + iOS targets with a shared `iosMain`/`iosTest` source set hierarchy
- **`KmpLibraryPlugin`** (`multiplatform.all`) — extends `KmmLibraryPlugin` with JVM, JS, Linux, macOS, watchOS, tvOS, and Windows targets; builds a layered source set tree: `common → nonAndroid → native → apple → ios/macOS/watchOS/tvOS`
- **`JvmPlugin`** — pure Kotlin/JVM module with Java 8 source/target and a `mavenJava` publication
- **`DetektPlugin`** — applies detekt with `detekt-formatting`, wires a `detektWithoutTests` task into `check`
- **`PublicationPlugin`** — configures Maven POM from `gradle.properties` keys (`moko.publish.*`) and in-memory PGP signing from env vars
- **`NexusPublicationPlugin`** — configures `io.github.gradle-nexus.publish-plugin` to point at `s01.oss.sonatype.org`
- **`HostsPublicationPlugin`** — gates certain publications (jvm, js, kotlinMultiplatform, android, linux) behind `IS_MAIN_HOST=true` system property; used for cross-platform CI to avoid duplicate uploads
- **`AndroidPublicationPlugin`** — Android-specific publication setup
- **`StubJavaDocPlugin`** — generates a stub Javadoc jar (satisfies Maven Central requirement)
- **`TestsReportPlugin`** — aggregates test reports

### Utilities

- `KotlinSourceSetUtils.kt` — helpers (`createMainTest`, `setupDependency`, `connectTargetsToSourceSet`) for building KMP source set dependency trees
- `PropertyUtils.kt` — `Project.requiredStringProperty` / `requiredIntProperty` that read `moko.<name>` from `gradle.properties` and throw a clear `GradleException` if missing

### Configuration via `gradle.properties`

Consumer projects drive plugin behaviour through properties prefixed with `moko.`:

| Property | Used by |
|---|---|
| `moko.android.compileSdk` | `AndroidBasePlugin` |
| `moko.android.targetSdk` | `AndroidBasePlugin` |
| `moko.android.minSdk` | `AndroidBasePlugin` |
| `moko.publish.name` | `PublicationPlugin` |
| `moko.publish.description` | `PublicationPlugin` |
| `moko.publish.repo.org` | `PublicationPlugin` |
| `moko.publish.repo.name` | `PublicationPlugin` |
| `moko.publish.license` | `PublicationPlugin` |
| `moko.publish.developers` | `PublicationPlugin` (format: `id\|Name\|email`, comma-separated for multiple) |

### Release process

Releases are triggered manually via the `Create release` GitHub Actions workflow (`.github/workflows/create-release.yml`), which publishes to OSSRH then creates a draft GitHub release tagged `release/<version>`. The version in `gradle/libs.versions.toml` (`mokoGradlePluginVersion`) must be updated before triggering the workflow.