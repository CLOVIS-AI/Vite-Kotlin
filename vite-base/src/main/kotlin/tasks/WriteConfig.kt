package opensavvy.gradle.vite.base.tasks

import opensavvy.gradle.vite.base.config.ExternalVitePlugin
import opensavvy.gradle.vite.base.config.ViteBuildConfig
import opensavvy.gradle.vite.base.config.ViteConfig
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFile
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.work.DisableCachingByDefault
import java.io.File

/**
 * Generates the `vite.config.js` file from the values configured in the [vite block][ViteConfig].
 */
@Suppress("LeakingThis")
@DisableCachingByDefault(because = "Not worth caching")
abstract class WriteConfig : DefaultTask() {

	/**
	 * Directory in which the build happens.
	 */
	@get:Input
	abstract val buildRoot: Property<String>

	/** See [ViteConfig.plugins]. */
	@get:Input
	abstract val plugins: ListProperty<ExternalVitePlugin>

	/** See [ViteBuildConfig.target]. */
	@get:Input
	abstract val buildTarget: Property<String>

	/**
	 * The directory in which the distribution will be created.
	 */
	@get:Input
	abstract val outDir: Property<String>

	/**
	 * The path to the `vite.config.js` file which will be created by this task.
	 *
	 * By default, it is created in the [buildRoot] directory.
	 */
	@get:OutputFile
	abstract val configurationFile: RegularFileProperty

	init {
		description = "Generates the vite.config.js file"

		val config = project.extensions.getByType(ViteConfig::class.java)
		plugins.convention(config.plugins)
		buildTarget.convention(config.build.target)
		configurationFile.convention(buildRoot.map { "$it/vite.config.js" }.map { RegularFile { File(it) } })
	}

	private fun pluginImport(plugin: ExternalVitePlugin) = if (plugin.isNamedExport)
		"import {${plugin.exportedAs}} from '${plugin.packageName}'"
	else
		"import ${plugin.exportedAs} from '${plugin.packageName}'"

	@TaskAction
	fun create() {
		val output = configurationFile.get().asFile
		output.writeText( // language=JavaScript
			"""
            ${
				plugins.get().joinToString(separator = "\n            ") { pluginImport(it) }
			}

            /** @type {import('vite').UserConfig} */
            export default {
				base: '',
                plugins: [
                    ${
				plugins.get()
					.joinToString(separator = ",\n                    ") { "${it.exportedAs}(${it.configuration ?: ""})" }
			}
                ],
                build: {
                    target: '${buildTarget.get()}',
                    outDir: '${outDir.get()}'
                }
            }

        """.trimIndent()
		)
	}
}
