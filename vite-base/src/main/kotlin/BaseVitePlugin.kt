package opensavvy.gradle.vite.base

import org.gradle.api.Plugin
import org.gradle.api.Project

class BaseVitePlugin : Plugin<Project> {

	override fun apply(target: Project) {
		println("Applied the Base Vite plugin")
	}
}
