package opensavvy.gradle.vite.kotlin.tasks

import opensavvy.gradle.vite.kotlin.KotlinVitePlugin
import opensavvy.gradle.vite.kotlin.config.ViteConfig
import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Copy
import org.gradle.configurationcache.extensions.capitalized

internal fun createCopyTask(project: Project, name: String, sourceTask: String, destination: Provider<Directory>, config: ViteConfig) {
	project.tasks.register(name, Copy::class.java) {
		group = KotlinVitePlugin.GROUP
		description = "Prepares the Vite working directory"

		dependsOn(sourceTask)

		val projectName =
			if (project.rootProject === project) project.name
			else project.rootProject.name + "-" + project.name

		from(project.rootProject.layout.buildDirectory.dir("js/packages/$projectName"))
		into(destination)
	}

	project.tasks.named("clean") {
		dependsOn("clean${name.capitalized()}")
	}
}
