package opensavvy.gradle.vite.base

import opensavvy.gradle.vite.base.config.ViteConfig
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType

class BaseVitePlugin : Plugin<Project> {

	override fun apply(target: Project) {
		target.extensions.create("vite", ViteConfig::class.java)
	}
}

/**
 * Accesses the project's [ViteConfig] extension.
 */
val Project.viteConfig: ViteConfig
	get() = extensions.getByType<ViteConfig>()
