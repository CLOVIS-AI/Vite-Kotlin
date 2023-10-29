package opensavvy.gradle.vite.base.tasks

import opensavvy.gradle.vite.base.config.ViteConfig
import opensavvy.gradle.vite.base.viteConfig
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFile
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*
import org.gradle.process.ExecOperations
import java.io.File
import javax.inject.Inject

/**
 * Executes a Vite command in the context of this project.
 */
@Suppress("LeakingThis")
@CacheableTask
abstract class ViteExec @Inject constructor(
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

	/**
	 * The directory in which the command will be executed.
	 */
	@get:Input
	abstract val workingDirectory: Property<String>

	/**
	 * The path to the `vite.config.js` file.
	 */
	@get:InputFile
	@get:PathSensitive(PathSensitivity.RELATIVE)
	abstract val configurationFile: RegularFileProperty

	/**
	 * The Vite configuration.
	 */
	@get:Nested
	abstract val config: ViteConfig

	init {
		description = "Executes a given Vite command"

		command.convention("")
		arguments.convention(emptyList())
		config.setDefaultsFrom(project.viteConfig)
		configurationFile.convention(config.root.map { "$it/vite.config.js" }.map { RegularFile { File(it) } })
		workingDirectory.convention(config.root.map { it.asFile.absolutePath })

		inputs.property("vite_version", config.version)

		inputs.dir(workingDirectory)
	}

	fun config(block: ViteConfig.() -> Unit) = config.apply(block)

	@TaskAction
	fun execute() {
		process.exec {
			commandLine(
				nodePath.get().asFile.absolutePath,
				vitePath.get().asFile.absolutePath,
				command.get(),
				"-c",
				configurationFile.get().asFile.relativeTo(File(workingDirectory.get())),
				*arguments.get().toTypedArray(),
			)

			workingDir(workingDirectory.get())
		}
	}
}
