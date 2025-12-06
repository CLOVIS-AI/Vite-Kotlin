package opensavvy.gradle.vite.kotlin.tasks

import opensavvy.gradle.vite.base.config.ViteConfig
import opensavvy.gradle.vite.base.config.dumpViteConfig
import opensavvy.gradle.vite.base.dump.dump
import opensavvy.gradle.vite.kotlin.KotlinVitePlugin
import opensavvy.gradle.vite.kotlin.viteBuildDevDir
import opensavvy.gradle.vite.kotlin.viteBuildProdDir
import org.gradle.api.Project

internal fun createDumpTask(project: Project, name: String, config: ViteConfig, targetName: String) {
	val viteBuildDev = project.viteBuildDevDir(targetName)
	val viteBuildProd = project.viteBuildProdDir(targetName)

	project.tasks.register(name) {
		group = KotlinVitePlugin.GROUP
		description = "Prints the configuration of the Vite plugin"

		doLast {
			println(dump {
				dumpViteConfig(config)

				section("Destinations") {
					value("Development", viteBuildDev)
					value("Production", viteBuildProd)
				}
			})
		}
	}
}
