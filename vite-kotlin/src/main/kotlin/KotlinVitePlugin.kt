package opensavvy.gradle.vite.kotlin

import opensavvy.gradle.vite.base.config.ViteConfig
import opensavvy.gradle.vite.kotlin.config.KotlinViteConfig
import opensavvy.gradle.vite.kotlin.config.defaultConfiguration
import opensavvy.gradle.vite.kotlin.tasks.*
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.provider.Provider
import org.gradle.internal.extensions.stdlib.capitalized
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget
import org.jetbrains.kotlin.gradle.targets.js.dsl.KotlinJsTargetDsl

class KotlinVitePlugin : Plugin<Project> {

	override fun apply(target: Project) {
		check(target.pluginManager.hasPlugin("org.jetbrains.kotlin.multiplatform")) { """The kotlin("multiplatform") plugin must be applied before the Vite for Kotlin plugin""" }

		target.extensions.create("vite", KotlinViteConfig::class.java)

		val config = target.kotlinViteConfig
		config.defaultConfiguration()

		fun registerTasks(kotlinTarget: KotlinTarget, useTargetPrefix: Boolean) {
			val vitePrefix = if (useTargetPrefix) "${kotlinTarget.name}Vite" else "vite"
			val dumpPrefix = if (useTargetPrefix) "${kotlinTarget.name}Dump" else "dump"

			// Lifecycle tasks for convenience
			target.tasks.register("${vitePrefix}CompileDev") {
				group = GROUP
				description = "Compiles the project and collects dependencies; run with --continuous to enable auto-reload for 'viteRun'"
				dependsOn("${vitePrefix}CompileKotlinDev")
			}
			target.tasks.register("${vitePrefix}CompileProd") {
				group = GROUP
				description = "Compiles the project and collects dependencies for the production variant"
				dependsOn("${vitePrefix}CompileKotlinProd")
			}
			configureDependencies(target, config, kotlinTarget.name)
			createDumpTask(target, "${dumpPrefix}ViteConfiguration", config, kotlinTarget.name)
			createCopyTask(target, "${vitePrefix}CompileKotlinDev", "compileDevelopmentExecutableKotlin${kotlinTarget.name.capitalized()}", target.viteBuildDevDir(kotlinTarget.name), kotlinTarget)
			val productionSuffix = if (kotlinTarget.targetName == "wasmJs") "Optimize" else ""
			createCopyTask(target, "${vitePrefix}CompileKotlinProd", "compileProductionExecutableKotlin${kotlinTarget.name.capitalized()}${productionSuffix}", target.viteBuildProdDir(kotlinTarget.name), kotlinTarget)
			createConfigWriterTasks(target, vitePrefix, kotlinTarget.name)
			createExecTasks(target, vitePrefix, kotlinTarget)
			configureFiltering(target, vitePrefix, kotlinTarget, config)
		}

		target.afterEvaluate {
			val kotlin = target.extensions.findByType(KotlinMultiplatformExtension::class.java)
				?: error("Could not find the 'kotlin' block")
			val targets = kotlin.targets.filterIsInstance<KotlinJsTargetDsl>()
				.filterNot { config.disabledTargets.get().contains(it.name) }
			targets.forEach {
				registerTasks(it, useTargetPrefix = targets.size > 1)
			}
		}
	}

	companion object {
		const val GROUP = "Vite for Kotlin"
	}
}

val Project.kotlinViteExtension: ViteConfig
	get() = extensions.getByType(ViteConfig::class.java)

private val Project.viteBuildDir: Provider<Directory>
	get() = project.layout.buildDirectory.dir("vite")

internal fun Project.viteBuildProdDir(targetName: String): Provider<Directory> =
	viteBuildDir.map { it.dir(targetName).dir("prod") }

internal fun Project.viteBuildDevDir(targetName: String): Provider<Directory> =
	viteBuildDir.map { it.dir(targetName).dir("dev") }

/**
 * Default dist directory (override by config)
 */
internal fun Project.viteBuildDistDir(targetName: String): Provider<Directory> =
	viteBuildDir.map { it.dir(targetName).dir("dist") }

/**
 * Accesses the project's [KotlinViteConfig] extension.
 */
val Project.kotlinViteConfig: KotlinViteConfig
	get() = extensions.getByType<KotlinViteConfig>()
