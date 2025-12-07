# Module Vite for Kotlin

Gradle plugin to use Vite in Kotlin Multiplatform projects.

<a href="https://search.maven.org/search?q=dev.opensavvy.vite.kotlin.gradle.plugin"><img src="https://img.shields.io/maven-central/v/dev.opensavvy.gradle.vite/vite-kotlin.svg?label=Maven%20Central"></a>

## Configuration

Always import the [Kotlin Multiplatform plugin](https://kotlinlang.org/docs/multiplatform-dsl-reference.html) before this one.

The Vite plugin is only needed to bundle the final application, so:
- there is no need to apply the plugin to library modules,
- the module should have `binaries.executable()` enabled.

```kotlin
// build.gradle.kts
plugins {
	kotlin("multiplatform")
	id("dev.opensavvy.vite.kotlin") version "<add the version here>"
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

Additionally, you must edit your `index.html` file and add `type="module"` to the line that imports your projects' main JS file.
For example:
```html
<script src="app.js" type="module">
```
To learn more about why this is necessary, see the [official documentation](https://vitejs.dev/guide/#index-html-and-project-root).

To configure the plugin, see [the vite block][opensavvy.gradle.vite.base.config.ViteConfig].

### ESM

ESM is a more modern module system natively supported by Vite. Using it may make the build faster, especially during development.

To use ESM, you must add `useEsModules()` in the `js {}` block:
```kotlin
// …

kotlin {
	js(IR) {
		browser()
		binaries.executable()
		useEsModules()
	}
}
```

Additionally, you must edit your `index.html` file to use the extension `.mjs` instead of `.js`.
For example:
```html
<script src="app.mjs" type="module">
```

## Tasks

The Vite plugin adds multiple tasks to the module it is applied to.
To see the entire list, run `./gradlew :<module>:tasks`.

### :viteRun

> Example: `./gradlew :website:viteRun`

Compiles and hosts the server for development. 
When compilation is finished, the URL to the development server is printed.

Keep this task running for as long as you want to have access to the project in your browser.

### :viteCompileDev --continuous

> Example: `./gradlew :website:viteCompileDev --continuous`

Enables auto-reload for the `:viteRun` task. Both need to be running at the same time for auto-reload to work.

Keep this task running for as long as you need auto-reload.

### :viteBuild

> Example: `./gradlew :website:viteBuild`

Compiles the production version of the website, and runs all minification tasks using [Rollup](https://rollupjs.org/).

Once the task has finished, the files are available in the `<module>/build/vite/<platform>/dist` directory.
