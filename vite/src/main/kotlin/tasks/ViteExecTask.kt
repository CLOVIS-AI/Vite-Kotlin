package opensavvy.gradle.vite.kotlin.tasks

import opensavvy.gradle.vite.kotlin.*
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.file.RegularFile
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*
import org.gradle.process.ExecOperations
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootPlugin.Companion.kotlinNodeJsExtension
import org.jetbrains.kotlin.gradle.targets.js.npm.NpmProject
import java.io.File
import javax.inject.Inject

/**
 * Executes a Vite command in the context of this project.
 */
@Suppress("LeakingThis")
@CacheableTask
abstract class ViteExecTask @Inject constructor(
	private val process: ExecOperations,
) : DefaultTask() {

	/**
	 * The Vite command to execute, for example `run` or `build`.
	 */
	@get:Input
	@get:Optional
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

		val kotlinEnvironment = project.rootProject.kotlinNodeJsExtension
		nodePath.convention(
			kotlinEnvironment
				.requireConfigured()
				.let { RegularFile { File(it.nodeExecutable) } }
		)

		vitePath.convention(
			kotlinEnvironment
				.projectPackagesDir.parentFile.resolve(NpmProject.NODE_MODULES)
				.let { RegularFile { File("$it/vite/bin/vite.js") } }
		)

		val config = project.kotlinViteExtension
		inputs.property("vite_version", config.version)
	}

	@TaskAction
	fun execute() {
		process.exec {
			commandLine(
				nodePath.get().asFile.absolutePath,
				vitePath.get().asFile.absolutePath,
				command.getOrElse(""),
				"-c",
				"../vite.config.js",
				*arguments.get().toTypedArray(),
			)

			workingDir(workingDirectory.get())
		}
	}

}

internal fun createExecTasks(project: Project) {
	project.tasks.register("viteBuild", ViteExecTask::class.java) {
		description = "Builds the production variant of the project"
		dependsOn("viteConfigureProd", ":kotlinNpmInstall")

		command.set("build")

		workingDirectory.set(project.viteBuildProdDir.map { "$it/kotlin" })
		outputs.dir(project.viteBuildDistDir)
	}

	project.tasks.register("viteRun", ViteExecTask::class.java) {
		description = "Hosts the development variant of the project"
		dependsOn("viteConfigureDev", ":kotlinNpmInstall")

		workingDirectory.set(project.viteBuildDevDir.map { "$it/kotlin" })
	}

	project.tasks.named("assemble") {
		dependsOn("viteBuild")
	}

	project.tasks.named("clean") {
		dependsOn("cleanViteBuild", "cleanViteRun")
	}
}
