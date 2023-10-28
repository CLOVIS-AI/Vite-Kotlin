package opensavvy.gradle.vite.kotlin.tasks

import opensavvy.gradle.vite.base.tasks.ViteExec
import opensavvy.gradle.vite.kotlin.KotlinVitePlugin
import opensavvy.gradle.vite.kotlin.viteBuildDevDir
import opensavvy.gradle.vite.kotlin.viteBuildDistDir
import opensavvy.gradle.vite.kotlin.viteBuildProdDir
import org.gradle.api.Project
import org.gradle.api.file.RegularFile
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootPlugin.Companion.kotlinNodeJsExtension
import org.jetbrains.kotlin.gradle.targets.js.npm.NpmProject
import java.io.File

private fun ViteExec.execConventions() {
	dependsOn(":kotlinNpmInstall")
	group = KotlinVitePlugin.GROUP

	val kotlinEnvironment = project.rootProject.kotlinNodeJsExtension
	nodePath.set(
		kotlinEnvironment
			.requireConfigured()
			.let { RegularFile { File(it.nodeExecutable) } }
	)

	vitePath.set(
		kotlinEnvironment
			.projectPackagesDir.parentFile.resolve(NpmProject.NODE_MODULES)
			.let { RegularFile { File("$it/vite/bin/vite.js") } }
	)
}

internal fun createExecTasks(project: Project) {
	project.tasks.register("viteBuild", ViteExec::class.java) {
		description = "Builds the production variant of the project"
		dependsOn("viteConfigureProd", "viteCompileProd")
		execConventions()

		command.set("build")

		config.root.set(project.viteBuildProdDir.map { it.dir("kotlin") })
		configurationFile.set(project.viteBuildProdDir.map { it.file("vite.config.js") })
		outputs.dir(project.viteBuildDistDir)
	}

	project.tasks.register("viteRun", ViteExec::class.java) {
		description = "Hosts the development variant of the project"
		dependsOn("viteConfigureDev", "viteCompileDev")
		execConventions()

		config.root.set(project.viteBuildDevDir.map { it.dir("kotlin") })
		configurationFile.set(project.viteBuildDevDir.map { it.file("vite.config.js") })
	}

	project.tasks.named("assemble") {
		dependsOn("viteBuild")
	}

	project.tasks.named("clean") {
		dependsOn("cleanViteBuild", "cleanViteRun")
	}
}
