package opensavvy.gradle.vite.kotlin.tasks

import opensavvy.gradle.vite.kotlin.KotlinVitePlugin
import opensavvy.gradle.vite.kotlin.config.ExternalVitePlugin
import opensavvy.gradle.vite.kotlin.config.ViteBuildConfig
import opensavvy.gradle.vite.kotlin.config.ViteConfig
import opensavvy.gradle.vite.kotlin.kotlinViteExtension
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.file.RegularFile
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.configurationcache.extensions.capitalized
import java.io.File

@Suppress("LeakingThis")
abstract class ViteConfigWriter : DefaultTask() {

	/** See [ViteConfig.buildRoot]. */
	@get:Input
	abstract val buildRoot: Property<String>

	/** See [ViteConfig.root]. */
	@get:Input
	abstract val root: Property<String>

	/** See [ViteConfig.base]. */
	@get:Input
	abstract val base: Property<String>

	/** See [ViteConfig.plugins]. */
	@get:Input
	abstract val plugins: ListProperty<ExternalVitePlugin>

	/** See [ViteBuildConfig.target]. */
	@get:Input
	abstract val buildTarget: Property<String>

	/**
	 * The path to the `vite.config.js` file which will be created by this task.
	 *
	 * By default, it is created in the [buildRoot] directory.
	 */
	@get:OutputFile
	abstract val configurationFile: RegularFileProperty

	init {
		description = "Generates the vite.config.js file"
		group = KotlinVitePlugin.GROUP

		val config = project.extensions.getByType(ViteConfig::class.java)
		buildRoot.convention(config.buildRoot.asFile.map { it.absolutePath })
		root.convention(config.root.asFile.map { it.absolutePath })
		base.convention(config.base.asFile.map { it.absolutePath })
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
		output.writeText( //language=JavaScript
			"""
            ${
				plugins.get().joinToString(separator = "\n            ") { pluginImport(it) }
			}

            /** @type {import('vite').UserConfig} */
            export default {
                root: '${root.get()}',
                base: '${base.get()}',
                plugins: [
                    ${
				plugins.get()
					.joinToString(separator = ",\n                    ") { "${it.exportedAs}(${it.configuration ?: ""})" }
			}
                ],
                build: {
                    target: '${buildTarget.get()}'
                }
            }

        """.trimIndent()
		)
	}

	companion object {
		const val DEFAULT_TASK_NAME = "configureVite"
	}
}

internal fun createConfigWriterTask(project: Project) {
	project.tasks.register(ViteConfigWriter.DEFAULT_TASK_NAME, ViteConfigWriter::class.java) {
		dependsOn("jsProductionExecutableCompileSync")
	}

	project.tasks.named("clean") {
		dependsOn("clean${ViteConfigWriter.DEFAULT_TASK_NAME.capitalized()}")
	}
}
