package opensavvy.gradle.vite.kotlin.tasks

import opensavvy.gradle.vite.kotlin.KotlinVitePlugin
import opensavvy.gradle.vite.kotlin.config.ViteConfig
import opensavvy.gradle.vite.kotlin.viteBuildDevDir
import opensavvy.gradle.vite.kotlin.viteBuildProdDir
import org.gradle.api.Project

internal fun createDumpTask(project: Project, config: ViteConfig) {
	project.tasks.register("dumpViteConfiguration") {
		group = KotlinVitePlugin.GROUP
		description = "Prints the configuration of the Vite plugin"

		doLast {
			val plugins = config.plugins.get()
				.takeIf { it.isNotEmpty() }
				?.joinToString(separator = "\n                   ")
				?: "(none)"

			println("""
					» Top-level
					Vite version  ${config.version.get()}
					Plugins       ${config.plugins.get().dump()}
					
					» Build
					Target        ${config.build.target.get()}
					
					» Destinations
					Dev           ${project.viteBuildDevDir.get()}
					Production    ${project.viteBuildProdDir.get()}
					
					» Transitive resource dependencies
					Projects      ${config.resources.projects.get().dump { it.path }}
				""".trimIndent())
		}
	}
}

private fun <T> Collection<T>.dump(transform: (T) -> CharSequence = { it.toString() }) = this
	.takeIf { it.isNotEmpty() }
	?.joinToString(separator = "\n                   ", transform = transform)
	?: "(none)"
