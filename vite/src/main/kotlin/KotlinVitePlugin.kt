package opensavvy.gradle.vite.kotlin

import opensavvy.gradle.vite.kotlin.config.ViteConfig
import opensavvy.gradle.vite.kotlin.config.defaultConfiguration
import opensavvy.gradle.vite.kotlin.tasks.*
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.provider.Provider

class KotlinVitePlugin : Plugin<Project> {

	override fun apply(target: Project) {
		check(target.pluginManager.hasPlugin("org.jetbrains.kotlin.multiplatform")) { """The kotlin("multiplatform") plugin must be applied before the Vite for Kotlin plugin""" }

		target.pluginManager.apply("dev.opensavvy.vite.base")

		val config = target.extensions.create("vite", ViteConfig::class.java)
		config.defaultConfiguration()

		// Lifecycle tasks for convenience
		target.tasks.register("viteCompileDev") {
			group = GROUP
			description = "Compiles the project and collects dependencies; run with --continuous to enable auto-reload for 'viteRun'"
			dependsOn("viteCompileKotlinDev")
		}
		target.tasks.register("viteCompileProd") {
			group = GROUP
			description = "Compiles the project and collects dependencies for the production variant"
			dependsOn("viteCompileKotlinProd")
		}

		configureDependencies(target, config)
		createDumpTask(target, config)
		createCopyTask(target, "viteCompileKotlinDev", "jsDevelopmentExecutableCompileSync", target.viteBuildDevDir)
		createCopyTask(target, "viteCompileKotlinProd", "jsProductionExecutableCompileSync", target.viteBuildProdDir)
		copyLocalResources(target, config)
		createConfigWriterTasks(target)
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

internal val Project.viteBuildProdDir: Provider<Directory>
	get() = viteBuildDir.map { it.dir("prod") }

internal val Project.viteBuildDevDir: Provider<Directory>
	get() = viteBuildDir.map { it.dir("dev/child") } // subfolder to match the nesting of the source maps :)

/**
 * Default dist directory (override by config)
 */
internal val Project.viteBuildDistDir: Provider<Directory>
	get() = viteBuildDir.map { it.dir("dist") }
