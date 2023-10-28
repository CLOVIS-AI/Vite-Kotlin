package opensavvy.gradle.vite.kotlin.tasks

import opensavvy.gradle.vite.base.config.ViteConfig
import opensavvy.gradle.vite.kotlin.KotlinVitePlugin
import opensavvy.gradle.vite.kotlin.viteBuildDevDir
import opensavvy.gradle.vite.kotlin.viteBuildProdDir
import org.gradle.api.Project

internal fun createDumpTask(project: Project, config: ViteConfig) {
	val viteBuildDev = project.viteBuildDevDir
	val viteBuildProd = project.viteBuildProdDir

	project.tasks.register("dumpViteConfiguration") {
		group = KotlinVitePlugin.GROUP
		description = "Prints the configuration of the Vite plugin"

		doLast {
			println("""
					» Top-level
					Vite version  ${config.version.get()}
					Plugins       ${config.plugins.get().dump()}
					
					» Build
					Target        ${config.build.target.get()}
					
					» Destinations
					Dev           ${viteBuildDev.get()}
					Production    ${viteBuildProd.get()}
					
					» Transitive resource dependencies
					Projects      ${config.resources.projects.get().dump()}
				""".trimIndent())
		}
	}
}

private fun <T> Collection<T>.dump(transform: (T) -> CharSequence = { it.toString() }) = this
	.takeIf { it.isNotEmpty() }
	?.joinToString(separator = "\n                   ", transform = transform)
	?: "(none)"
