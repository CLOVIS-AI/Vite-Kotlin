package opensavvy.gradle.vite.base.tasks

import opensavvy.gradle.vite.base.config.ExternalVitePlugin
import opensavvy.gradle.vite.base.config.ViteConfig
import opensavvy.gradle.vite.base.viteConfig
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFile
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.Nested
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
	 * The configuration to write to the file.
	 */
	@get:Nested
	abstract val config: ViteConfig

	/**
	 * The path to the `vite.config.js` file which will be created by this task.
	 *
	 * By default, it is created in the [root][ViteConfig.root] directory.
	 */
	@get:OutputFile
	abstract val configurationFile: RegularFileProperty

	init {
		description = "Generates the vite.config.js file"

		config.setDefaultsFrom(project.viteConfig)
		configurationFile.convention(config.root.map { "$it/vite.config.js" }.map { RegularFile { File(it) } })

		inputs.property("plugins", config.plugins)
		inputs.property("base", config.base)
		inputs.property("root", config.root.map { it.toString() })
		inputs.property("build.target", config.build.target)
		inputs.property("build.modulePreload", config.build.modulePreload)
		inputs.property("build.outDir", config.build.outDir.map { it.toString() })
	}

	fun config(block: ViteConfig.() -> Unit) = config.apply(block)

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
				config.plugins.get().joinToString(separator = "\n            ") { pluginImport(it) }
			}

			/** @type {import('vite').UserConfig} */
			export default {
				root: '${config.root.get().asFile.relativeTo(configurationFile.get().asFile.parentFile).invariantSeparatorsPath}',
				base: '${config.base.get()}',
				plugins: [
					${
						config.plugins.get()
							.joinToString(separator = ",\n                    ") { "${it.exportedAs}(${it.configuration ?: ""})" }
					},
				],
				build: {
					target: '${config.build.target.get()}',
					modulePreload: ${config.build.modulePreload.get()},
					outDir: '${config.build.outDir.get().asFile.invariantSeparatorsPath}',
				},
			}

        """.trimIndent()
		)
	}
}
