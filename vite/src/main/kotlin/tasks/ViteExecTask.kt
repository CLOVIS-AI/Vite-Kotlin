package opensavvy.gradle.vite.kotlin.tasks

import opensavvy.gradle.vite.kotlin.KotlinVitePlugin
import opensavvy.gradle.vite.kotlin.kotlinViteExtension
import opensavvy.gradle.vite.kotlin.viteBuildProdDir
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.file.RegularFile
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootPlugin.Companion.kotlinNodeJsExtension
import org.jetbrains.kotlin.gradle.targets.js.npm.NpmProject
import java.io.File

@Suppress("LeakingThis")
@CacheableTask
abstract class ViteExecTask : DefaultTask() {

	/**
	 * The Vite command to execute, for example `run` or `build`.
	 */
	@get:Input
	abstract val command: Property<String>

	/**
	 * Arguments passed to [command].
	 */
	@get:Input
	abstract val arguments: ListProperty<String>

	/**
	 * The path to the Node executable.
	 *
	 * Use this property to override which Node installation is used.
	 * By default, the Node installation used by the Kotlin plugin is used.
	 */
	@get:InputFile
	@get:PathSensitive(PathSensitivity.RELATIVE)
	abstract val nodePath: RegularFileProperty

	/**
	 * The path to the Vite executable.
	 *
	 * Use this property to override which Vite installation is used.
	 * By default, Vite is installed as a development dependency of the project in which this plugin is applied.
	 */
	@get:InputFile
	@get:PathSensitive(PathSensitivity.RELATIVE)
	abstract val vitePath: RegularFileProperty

	@get:Input
	abstract val workingDirectory: Property<String>

	init {
		group = KotlinVitePlugin.GROUP
		description = "Executes a given Vite command"

		arguments.convention(emptyList())

		val kotlinEnvironment = project.provider {
			project.rootProject.kotlinNodeJsExtension
		}
		nodePath.convention(
			kotlinEnvironment
				.map { it.requireConfigured() }
				.map { RegularFile { File(it.nodeExecutable) } }
		)

		vitePath.convention(
			kotlinEnvironment
				.map { it.projectPackagesDir.parentFile.resolve(NpmProject.NODE_MODULES) }
				.map { RegularFile { File("$it/vite/bin/vite.js") } }
		)

		val config = project.kotlinViteExtension
		inputs.property("vite_version", config.version)
	}

	@TaskAction
	fun execute() {
		project.exec {
			commandLine(
				nodePath.get().asFile.absolutePath,
				vitePath.get().asFile.absolutePath,
				command.get(),
				*arguments.get().toTypedArray(),
			)

			workingDir(workingDirectory.get())
		}
	}

}

internal fun createExecTasks(project: Project) {
	project.tasks.register("viteBuild", ViteExecTask::class.java) {
		description = "Builds the production variant of the project"
		dependsOn("viteConfigureProd")

		command.set("build")

		workingDirectory.set(project.viteBuildProdDir.map { it.asFile.absolutePath })
		outputs.dir(workingDirectory.map { "$it/dist" })
	}

	project.tasks.named("assemble") {
		dependsOn("viteBuild")
	}

	project.tasks.named("clean") {
		dependsOn("cleanViteBuild")
	}
}
