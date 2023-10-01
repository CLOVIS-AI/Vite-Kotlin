package opensavvy.gradle.vite.kotlin.tasks

import opensavvy.gradle.vite.kotlin.KotlinVitePlugin
import opensavvy.gradle.vite.kotlin.config.ViteConfig
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
					Build root    ${config.buildRoot.get()}
					Project root  ${config.root.get()}
					Asset base    ${config.base.get()}
					Plugins       $plugins       
				""".trimIndent())
		}
	}
}
