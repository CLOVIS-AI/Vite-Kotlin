package opensavvy.gradle.vite.kotlin.tasks

import opensavvy.gradle.vite.base.config.ViteConfig
import opensavvy.gradle.vite.base.config.dumpViteConfig
import opensavvy.gradle.vite.base.dump.dump
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
			println(dump {
				dumpViteConfig(config)

				section("Destinations") {
					value("Development", viteBuildDev.get())
					value("Production", viteBuildProd.get())
				}
			})
		}
	}
}
