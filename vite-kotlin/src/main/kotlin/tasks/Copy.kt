package opensavvy.gradle.vite.kotlin.tasks

import opensavvy.gradle.vite.kotlin.KotlinVitePlugin
import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.file.DuplicatesStrategy
import org.gradle.api.provider.Provider
import org.gradle.internal.extensions.stdlib.capitalized
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget
import org.jetbrains.kotlin.gradle.targets.js.ir.DefaultIncrementalSyncTask
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.deleteExisting
import kotlin.io.path.exists
import kotlin.io.path.isSymbolicLink
import kotlin.io.path.readSymbolicLink

internal fun createCopyTask(project: Project, name: String, sourceTask: String, destination: Provider<Directory>, target: KotlinTarget) {
	val rootBuildDir = project.rootProject.layout.buildDirectory
	val targetName = target.name
	val targetType = if (target.targetName == "wasmJs") "wasm" else "js"

	project.tasks.register(name, CustomIncrementalSyncTask::class.java) {
		group = KotlinVitePlugin.GROUP
		description = "Prepares the Vite working directory"

		val kotlinNodeJsSetup = if (targetType == "js") "kotlinNodeJsSetup" else "kotlinWasmNodeJsSetup"
		dependsOn("${targetName}PackageJson", kotlinNodeJsSetup, sourceTask, "${targetName}ProcessResources")
		mustRunAfter(sourceTask)

		from.from(project.tasks.named(sourceTask))
		from.from(project.tasks.named("${targetName}ProcessResources"))
		destinationDirectory.set(destination.map { it.dir("kotlin").asFile })
		duplicatesStrategy = DuplicatesStrategy.WARN

		doLast {
			val nodeModules = Path.of(destination.get().asFile.absolutePath, "node_modules")
			val realNodeModules = Path.of(rootBuildDir.dir("${targetType}/node_modules").get().asFile.absolutePath)

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
