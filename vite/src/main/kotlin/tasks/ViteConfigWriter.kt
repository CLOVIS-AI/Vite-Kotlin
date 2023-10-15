package opensavvy.gradle.vite.kotlin.tasks

import opensavvy.gradle.vite.kotlin.KotlinVitePlugin
import opensavvy.gradle.vite.kotlin.config.ExternalVitePlugin
import opensavvy.gradle.vite.kotlin.config.ViteBuildConfig
import opensavvy.gradle.vite.kotlin.config.ViteConfig
import opensavvy.gradle.vite.kotlin.viteBuildDevDir
import opensavvy.gradle.vite.kotlin.viteBuildDistDir
import opensavvy.gradle.vite.kotlin.viteBuildProdDir
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
import org.gradle.work.DisableCachingByDefault
import java.io.File

/**
 * Generates the `vite.config.js` file from the values configured in the [vite block][ViteConfig].
 */
@Suppress("LeakingThis")
@DisableCachingByDefault(because = "Not worth caching")
abstract class ViteConfigWriter : DefaultTask() {

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
                    outDir: '${project.viteBuildDistDir.get()}'
                }
            }

        """.trimIndent()
		)
	}

	companion object {
		/** Name of the default configuration task for the dev build. */
		const val NAME_DEV = "viteConfigureDev"
		/** Name of the default configuration task for the production build. */
		const val NAME_PROD = "viteConfigureProd"
	}
}

internal fun createConfigWriterTasks(project: Project) {
	project.tasks.register(ViteConfigWriter.NAME_DEV, ViteConfigWriter::class.java) {
		dependsOn("viteCompileDev")

		buildRoot.convention(project.viteBuildDevDir.map { it.asFile.absolutePath })
	}

	project.tasks.register(ViteConfigWriter.NAME_PROD, ViteConfigWriter::class.java) {
		dependsOn("viteCompileProd")

		buildRoot.convention(project.viteBuildProdDir.map { it.asFile.absolutePath })
	}

	project.tasks.named("clean") {
		dependsOn("clean${ViteConfigWriter.NAME_DEV.capitalized()}", "clean${ViteConfigWriter.NAME_PROD.capitalized()}")
	}
}
