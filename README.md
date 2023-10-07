# Vite for Kotlin

Gradle plugin to create [Kotlin/JS](https://kotlinlang.org/docs/js-overview.html) projects using [Vite](https://vitejs.dev/).

[TOC]

## Usage

### Minimal configuration

> **Important:**
> This section is under construction.
> At the moment, the plugin is not yet published anywhere, so it is not possible to import it without cloning the repository.

Always import the Kotlin Multiplatform plugin before this plugin.
The Vite plugin is used to bundle the final application, so the JS source set must be executable.

```kotlin
// build.gradle.kts
plugins {
	kotlin("multiplatform")
	id("opensavvy.vite.kotlin") version "<add the version here>"
}

repositories {
	mavenCentral()
}

kotlin {
	js(IR) {
		browser()
		binaries.executable()
	}
}
```

### Tasks

To compile and host the development version of the project, run `./gradlew viteRun`.
Let it run for however long you want to continue hosting it.

To enable auto-reloading when the code changes, open a new terminal and run `./gradlew viteCompileDev --continuous` while `viteRun` is running.

To export the project, use `./gradlew viteBuild`. The website is generated in the project's `build/vite/dist` folder.

### Advanced configuration

To configure other aspects of the Vite configuration, use the `vite` extension:
```kotlin
vite {
	version.set("4.4.11") // use another version of Vite than the one bundled with the plugin
}
```

## License

This project is licensed under the [Apache 2.0 license](LICENSE).

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md).
- To learn more about our coding conventions and workflow, see the [OpenSavvy Wiki](https://gitlab.com/opensavvy/wiki/-/blob/main/README.md#wiki).
- This project is based on the [OpenSavvy Playground](docs/playground/README.md), a collection of preconfigured project templates.
