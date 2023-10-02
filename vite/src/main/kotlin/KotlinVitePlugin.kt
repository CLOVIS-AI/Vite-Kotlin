package opensavvy.gradle.vite.kotlin

import opensavvy.gradle.vite.kotlin.config.ViteConfig
import opensavvy.gradle.vite.kotlin.config.defaultConfigurationFor
import opensavvy.gradle.vite.kotlin.tasks.configureDependencies
import opensavvy.gradle.vite.kotlin.tasks.createConfigWriterTask
import opensavvy.gradle.vite.kotlin.tasks.createCopyTask
import opensavvy.gradle.vite.kotlin.tasks.createDumpTask
import opensavvy.gradle.vite.kotlin.tasks.createExecTasks
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.provider.Provider

class KotlinVitePlugin : Plugin<Project> {

	override fun apply(target: Project) {
		check(target.pluginManager.hasPlugin("org.jetbrains.kotlin.multiplatform")) { """The kotlin("multiplatform") plugin must be applied before the Vite for Kotlin plugin""" }

		val config = target.extensions.create("vite", ViteConfig::class.java)
		config.defaultConfigurationFor(target)

		configureDependencies(target, config)
		createDumpTask(target, config)
		createCopyTask(target, "viteRunEnv", "jsDevelopmentExecutableCompileSync", target.viteBuildDevDir, config)
		createCopyTask(target, "viteBuildEnv", "jsProductionExecutableCompileSync", target.viteBuildProdDir, config)
		createConfigWriterTask(target)
		createExecTasks(target)
	}

	companion object {
		const val GROUP = "Vite for Kotlin"
	}
}

val Project.kotlinViteExtension: ViteConfig
	get() = extensions.getByType(ViteConfig::class.java)

private val Project.viteBuildDir: Provider<Directory>
	get() = project.layout.buildDirectory.dir("vite")

val Project.viteBuildProdDir: Provider<Directory>
	get() = viteBuildDir.map { it.dir("prod") }

val Project.viteBuildDevDir: Provider<Directory>
	get() = viteBuildDir.map { it.dir("dev") }
