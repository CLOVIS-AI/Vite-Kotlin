package opensavvy.gradle.vite.kotlin

import opensavvy.gradle.vite.kotlin.config.ViteConfig
import opensavvy.gradle.vite.kotlin.config.defaultConfigurationFor
import opensavvy.gradle.vite.kotlin.tasks.configureDependencies
import opensavvy.gradle.vite.kotlin.tasks.createConfigWriterTask
import opensavvy.gradle.vite.kotlin.tasks.createDumpTask
import org.gradle.api.Plugin
import org.gradle.api.Project

class KotlinVitePlugin : Plugin<Project> {

	override fun apply(target: Project) {
		check(target.pluginManager.hasPlugin("org.jetbrains.kotlin.multiplatform")) { """The kotlin("multiplatform") plugin must be applied before the Vite for Kotlin plugin""" }

		val config = target.extensions.create("vite", ViteConfig::class.java)
		config.defaultConfigurationFor(target)

		configureDependencies(target, config)
		createDumpTask(target, config)
		createConfigWriterTask(target)
	}

	companion object {
		const val GROUP = "Vite for Kotlin"
	}
}
