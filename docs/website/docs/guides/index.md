# Building a KotlinJS website with Vite

[Vite](https://vite.dev/) is a modern build tool for JavaScript. Compared to Webpack, which is used by default by Kotlin, Vite is faster in both development and production builds, and creates smaller  production artifacts.

## Configuration

First, create your Gradle project like you usually would using the [Kotlin Multiplatform](https://kotlinlang.org/docs/multiplatform-dsl-reference.html) plugin.

The Vite plugin is only useful to bundle the final application, so only apply it to modules that have `binaries.executable()` enabled.

```kotlin title="build.gradle.kts" hl_lines="3"
plugins {
	kotlin("multiplatform")
	id("dev.opensavvy.vite.kotlin") version "xxx" //(1)!
}

repositories {
	mavenCentral()
}

kotlin {
	js {
		browser()
		binaries.executable() //(2)!
	}
}

vite {
	//(3)!
}
```

1. Visit the available versions in the [news](../news/index.md).
2. `binaries.executable()` is necessary in application modules. Do not apply the Vite plugin to non-application modules.
3. The plugin works out of the box with no additional configuration. The configuration options are documented [here](../api/-vite%20for%20-gradle/opensavvy.gradle.vite.base.config/-vite-config/index.html).

The Kotlin compiler generates a JS file which Vite must import. To do so, create an HTML file in `src/jsMain/resources`:
```html title="index.html" hl_lines="10"
<!DOCTYPE html>
<html lang="en">
<head>
	<meta charset="UTF-8">
	<title>Vite for Kotlin</title>
</head>
<body>
<div id="root"></div>
<!--suppress HtmlUnknownTarget The file is valid at runtime, but IDEA can't see it -->
<script src="example-simple.js" type="module"></script>
</body>
</html>
```

The name of the file is the name of your Gradle build followed by the name of the module.

!!! tip
    To find out the name of the generated file:
    
    1. Run `./gradlew jsDevelopmentExecutableCompileSync`
    2. Look in the directory `build/js/packages/<your project>/kotlin/`

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

Once the task has finished, the files are available in the `<module>/build/vite/dist` directory.

## Troubleshooting

### Windows: A required privilege is not held by the client

You may need to enable Developer Mode in the settings. This plugin requires the creation of symbolic links to avoid duplicating the `node_modules` files. See [gradle#9077](https://github.com/gradle/gradle/issues/9077) for more information.
