package opensavvy.gradle.vite.kotlin.tasks

import opensavvy.gradle.vite.base.tasks.ViteExec
import opensavvy.gradle.vite.base.tasks.WriteConfig
import opensavvy.gradle.vite.kotlin.KotlinVitePlugin
import opensavvy.gradle.vite.kotlin.viteBuildDevDir
import opensavvy.gradle.vite.kotlin.viteBuildDistDir
import opensavvy.gradle.vite.kotlin.viteBuildProdDir
import org.gradle.api.Project
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Internal
import org.gradle.internal.extensions.stdlib.capitalized
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.getValue
import org.gradle.process.ExecOperations
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget
import org.jetbrains.kotlin.gradle.targets.js.NpmPackageVersion
import org.jetbrains.kotlin.gradle.targets.js.RequiredKotlinJsDependency
import org.jetbrains.kotlin.gradle.targets.js.ir.KotlinJsIrCompilation
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsPlugin
import org.jetbrains.kotlin.gradle.targets.js.npm.RequiresNpmDependencies
import java.io.File
import javax.inject.Inject

/**
 * A specialization of [ViteExec] that uses the Kotlin Gradle Plugin to download
 * Vite.
 */
@CacheableTask
abstract class KotlinViteExec @Inject constructor(
	process: ExecOperations, targetName: String, private val targetType: String,
) : ViteExec(process), RequiresNpmDependencies {

	@OptIn(ExperimentalWasmDsl::class)
	@get:Internal
	override val compilation: KotlinJsIrCompilation
		get() {
			val extension = project.extensions.getByType<KotlinMultiplatformExtension>()
			val targetDsl = if (targetType == "js") extension.js() else extension.wasmJs()
			return targetDsl.compilations.getByName(KotlinCompilation.MAIN_COMPILATION_NAME)
		}

	@get:Internal
	override val requiredNpmDependencies: Set<RequiredKotlinJsDependency>
		get() = setOf(NpmPackageVersion("vite", config.version.get()))
			.plus(config.plugins.get().map { NpmPackageVersion(it.packageName, it.version) })

	init {
		val kotlinNodeJsSetup = if (targetType == "js") "kotlinNodeJsSetup" else "kotlinWasmNodeJsSetup"
		val kotlinNpmInstall = if (targetType == "js") ":kotlinNpmInstall" else ":kotlinWasmNpmInstall"
		dependsOn(kotlinNodeJsSetup, "${targetName}PackageJson", kotlinNpmInstall)
		group = KotlinVitePlugin.GROUP

		nodePath.set {
			File(NodeJsPlugin.apply(project.rootProject).executable.get())
		}

		// In theory, we should be able to access the Vite path via:
		//     File(compilation.npmProject.require("vite"))
		// However, the configuration cache will initialize all inputs eagerly, and the above line
		// throws if executed before the :kotlinNpmInstall task. Using the line above thus
		// results in a build that crashes after a clean build, but works if a :kotlinNpmInstall is executed
		// by itself first.
		vitePath.set(project.rootProject.layout.buildDirectory.file("${targetType}/node_modules/vite/bin/vite.js"))
	}
}

internal fun createExecTasks(project: Project, vitePrefix: String, target: KotlinTarget) {
	val configureDev: WriteConfig by project.tasks.named("${vitePrefix}ConfigureDev")
	val configureProd: WriteConfig by project.tasks.named("${vitePrefix}ConfigureProd")
	val targetName = target.name
	val targetType = if (target.targetName == "wasmJs") "wasm" else "js"

	project.tasks.register("${vitePrefix}Build", KotlinViteExec::class.java, targetName, targetType).apply {
		configure {
			description = "Builds the production variant of the project"
			dependsOn(configureProd, "${vitePrefix}CompileProd")

			command.set("build")

			config.setDefaultsFrom(configureProd.config)

			config.root.set(project.viteBuildProdDir(targetName).map { it.dir("kotlin") })
			configurationFile.set(configureProd.configurationFile)
			outputs.dir(project.viteBuildDistDir(targetName))
		}
	}

	project.tasks.register("${vitePrefix}Run", KotlinViteExec::class.java, targetName, targetType).apply {
		configure {
			description = "Hosts the development variant of the project"
			dependsOn(configureDev, "${vitePrefix}CompileDev")

			config.setDefaultsFrom(configureDev.config)

			config.root.set(project.viteBuildDevDir(targetName).map { it.dir("kotlin") })
			configurationFile.set(configureDev.configurationFile)

			inputs.property("server.host", config.server.host)
			inputs.property("server.port", config.server.port)
			inputs.property("server.strictPort", config.server.strictPort)
		}
	}

	project.tasks.named("assemble") {
		dependsOn("${vitePrefix}Build")
	}

	project.tasks.named("clean") {
		dependsOn("clean${vitePrefix.capitalized()}Build", "clean${vitePrefix.capitalized()}Run")
	}
}
