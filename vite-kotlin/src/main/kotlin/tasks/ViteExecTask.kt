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
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.getValue
import org.gradle.process.ExecOperations
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
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
	process: ExecOperations,
) : ViteExec(process), RequiresNpmDependencies {

	@get:Internal
	override val compilation: KotlinJsIrCompilation
		get() = project.extensions.getByType<KotlinMultiplatformExtension>().js()
			.compilations.getByName(KotlinCompilation.MAIN_COMPILATION_NAME)

	@get:Internal
	override val requiredNpmDependencies: Set<RequiredKotlinJsDependency>
		get() = setOf(NpmPackageVersion("vite", config.version.get()))

	init {
		dependsOn("kotlinNodeJsSetup", "jsPackageJson", ":kotlinNpmInstall")
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
		vitePath.set(project.rootProject.layout.buildDirectory.file("js/node_modules/vite/bin/vite.js"))
	}
}

internal fun createExecTasks(project: Project) {
	val configureDev: WriteConfig by project.tasks.named(VITE_CONFIGURE_DEV_NAME)
	val configureProd: WriteConfig by project.tasks.named(VITE_CONFIGURE_PROD_NAME)

	project.tasks.register("viteBuild", KotlinViteExec::class.java) {
		description = "Builds the production variant of the project"
		dependsOn(configureProd, "viteCompileProd")

		command.set("build")

		config.setDefaultsFrom(configureProd.config)

		config.root.set(project.viteBuildProdDir.map { it.dir("kotlin") })
		configurationFile.set(project.viteBuildProdDir.map { it.file("vite.config.js") })
		outputs.dir(project.viteBuildDistDir)
	}

	project.tasks.register("viteRun", KotlinViteExec::class.java) {
		description = "Hosts the development variant of the project"
		dependsOn(configureDev, "viteCompileDev")

		config.setDefaultsFrom(configureDev.config)

		config.root.set(project.viteBuildDevDir.map { it.dir("kotlin") })
		configurationFile.set(project.viteBuildDevDir.map { it.file("vite.config.js") })

		inputs.property("server.host", config.server.host)
		inputs.property("server.port", config.server.port)
		inputs.property("server.strictPort", config.server.strictPort)
	}

	project.tasks.named("assemble") {
		dependsOn("viteBuild")
	}

	project.tasks.named("clean") {
		dependsOn("cleanViteBuild", "cleanViteRun")
	}
}
