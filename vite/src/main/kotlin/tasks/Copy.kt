package opensavvy.gradle.vite.kotlin.tasks

import opensavvy.gradle.vite.kotlin.KotlinVitePlugin
import opensavvy.gradle.vite.kotlin.config.ViteConfig
import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Copy
import org.gradle.configurationcache.extensions.capitalized
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.deleteExisting
import kotlin.io.path.exists
import kotlin.io.path.isSymbolicLink
import kotlin.io.path.readSymbolicLink

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
		exclude("node_modules")

		doLast {
			val nodeModules = Path.of(destination.get().asFile.absolutePath, "node_modules")
			val realNodeModules = Path.of(project.rootProject.layout.buildDirectory.dir("js/node_modules").get().asFile.absolutePath)

			if (nodeModules.exists() && nodeModules.isSymbolicLink() && nodeModules.readSymbolicLink() != realNodeModules) {
				// it exists, but doesn't point to the correct place
				nodeModules.deleteExisting()
			}

			if (!nodeModules.exists()) {
				Files.createSymbolicLink(
					nodeModules,
					realNodeModules,
				)
			}
		}
	}

	project.tasks.named("clean") {
		dependsOn("clean${name.capitalized()}")
	}
}
