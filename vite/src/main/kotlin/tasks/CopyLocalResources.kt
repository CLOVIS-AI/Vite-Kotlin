package opensavvy.gradle.vite.kotlin.tasks

import opensavvy.gradle.vite.kotlin.KotlinVitePlugin
import opensavvy.gradle.vite.kotlin.config.ViteConfig
import opensavvy.gradle.vite.kotlin.viteBuildDevDir
import opensavvy.gradle.vite.kotlin.viteBuildProdDir
import org.gradle.api.Project
import org.gradle.api.tasks.Copy
import org.gradle.configurationcache.extensions.capitalized

internal fun copyLocalResources(project: Project, config: ViteConfig) {
	project.afterEvaluate {
		for (dependentProject in config.resources.projects.get()) {
			val environments = listOf(
				"Dev" to project.viteBuildDevDir,
				"Prod" to project.viteBuildProdDir,
			)

			for ((envName, envDir) in environments) {
				val dependentPath = dependentProject.path.split(":").joinToString(separator = "") { it.capitalized() }

				val copy = project.tasks.register("copyResourcesFrom$dependentPath$envName", Copy::class.java) {
					group = KotlinVitePlugin.GROUP
					description = "Copies the resources of project ${dependentProject.path} into the current project's working directory"

					dependsOn("viteCompileKotlin$envName")

					from(dependentProject.tasks.named("jsProcessResources"))
					into(envDir.map { it.dir("kotlin/" + dependentProject.path.removePrefix(":").replace(":", "-")) })
				}

				project.tasks.named("viteCompile$envName") {
					dependsOn(copy)
				}
			}
		}
	}
}
