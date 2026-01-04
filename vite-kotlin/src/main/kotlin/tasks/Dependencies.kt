package opensavvy.gradle.vite.kotlin.tasks

import opensavvy.gradle.vite.base.config.ViteConfig
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

internal fun configureDependencies(project: Project, config: ViteConfig, targetName: String) {
	val kotlin = project.extensions.findByType(KotlinMultiplatformExtension::class.java)
		?: error("Could not find the 'kotlin' block")

	kotlin.sourceSets
		.matching { it.name == "${targetName}Main" }
		.configureEach {
			dependencies {
				implementation(devNpm("vite", config.version.get()))

				for (plugin in config.plugins.get().filterNot { it.isLocal }) {
					implementation(devNpm(plugin.packageName, plugin.version))
				}
			}
		}
}
