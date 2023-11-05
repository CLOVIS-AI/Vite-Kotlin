# Module Vite for Gradle

General purpose Gradle plugin to use Vite without preconfigured behavior.

<a href="https://search.maven.org/search?q=dev.opensavvy.vite.base.gradle.plugin"><img src="https://img.shields.io/maven-central/v/dev.opensavvy.gradle.vite/vite-base.svg?label=Maven%20Central"></a>

## Use Vite in your own terms

Use this plugin to integrate Vite into your build processes exactly how you want it.

To avoid manually configuring Vite, use one of the other modules in this project, which are pre-configured to be
compatible with other parts of the Gradle ecosystem.

## Usage

Import the plugin in your build script:
```kotlin
// build.gradle.kts
plugins {
	id("dev.opensavvy.vite.base") version "<add the version here>"
}
```

The plugin automatically creates [the `vite` extension block](opensavvy.gradle.vite.base.config.ViteConfig), but it creates no tasks
nor other behavior. To access it, use [the `viteConfig` accessor](opensavvy.gradle.vite.base.viteConfig).

## Task types

This plugin exposes multiple task types that you can use to create your own build.
- [`WriteConfig`][opensavvy.gradle.vite.base.tasks.WriteConfig]: generate the `vite.config.js` file.
- [`ViteExec`][opensavvy.gradle.vite.base.tasks.ViteExec]: execute a Vite command.

Note that it is the user's responsibility to install Vite and other dependencies.
