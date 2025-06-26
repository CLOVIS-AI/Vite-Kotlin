package opensavvy.gradle.vite.kotlin.tasks

import opensavvy.gradle.vite.kotlin.KotlinVitePlugin
import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Sync
import org.gradle.configurationcache.extensions.capitalized
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.deleteExisting
import kotlin.io.path.exists
import kotlin.io.path.isSymbolicLink
import kotlin.io.path.readSymbolicLink

internal fun createCopyTask(project: Project, name: String, sourceTask: String, destination: Provider<Directory>) {
	val rootBuildDir = project.rootProject.layout.buildDirectory

	project.tasks.register(name, Sync::class.java) {
		group = KotlinVitePlugin.GROUP
		description = "Prepares the Vite working directory"

		dependsOn("jsPackageJson", "kotlinNodeJsSetup")

		from(project.tasks.named(sourceTask))
		into(destination.map { it.dir("kotlin") })
		exclude("node_modules")

		doLast {
			val nodeModules = Path.of(destination.get().asFile.absolutePath, "node_modules")
			val realNodeModules = Path.of(rootBuildDir.dir("js/node_modules").get().asFile.absolutePath)

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
