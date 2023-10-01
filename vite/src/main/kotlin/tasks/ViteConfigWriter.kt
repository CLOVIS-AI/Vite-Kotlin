package opensavvy.gradle.vite.kotlin.tasks

import opensavvy.gradle.vite.kotlin.KotlinVitePlugin
import opensavvy.gradle.vite.kotlin.config.ExternalVitePlugin
import opensavvy.gradle.vite.kotlin.config.ViteBuildConfig
import opensavvy.gradle.vite.kotlin.config.ViteConfig
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.configurationcache.extensions.capitalized

@Suppress("LeakingThis")
abstract class ViteConfigWriter : DefaultTask() {

	/** See [ViteConfig.buildRoot]. */
	@get:InputDirectory
	abstract val buildRoot: DirectoryProperty

	/** See [ViteConfig.root]. */
	@get:InputDirectory
	abstract val root: DirectoryProperty

	/** See [ViteConfig.base]. */
	@get:InputDirectory
	abstract val base: DirectoryProperty

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
		buildRoot.convention(config.buildRoot)
		root.convention(config.root)
		base.convention(config.base)
		plugins.convention(config.plugins)
		buildTarget.convention(config.build.target)
		configurationFile.convention(buildRoot.file("vite.config.js"))
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
                root: '${root.get().asFile}',
                base: '${base.get().asFile}',
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
