package opensavvy.gradle.vite.kotlin

import org.gradle.api.Plugin
import org.gradle.api.Project

class KotlinVitePlugin : Plugin<Project> {

	override fun apply(target: Project) {
		check(target.pluginManager.hasPlugin("org.jetbrains.kotlin.multiplatform")) { """The kotlin("multiplatform") plugin must be applied before the Vite for Kotlin plugin""" }

		target.tasks.register("dumpViteConfiguration") {
			group = GROUP
			description = "Prints the configuration of the Vite plugin"

			doLast {
				println("Hello world")
			}
		}
	}

	companion object {
		const val GROUP = "Vite for Kotlin"
	}
}
