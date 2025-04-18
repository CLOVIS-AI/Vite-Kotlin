package opensavvy.gradle.vite.kotlin.tasks

import opensavvy.gradle.vite.base.tasks.ViteExec
import opensavvy.gradle.vite.kotlin.KotlinVitePlugin
import opensavvy.gradle.vite.kotlin.viteBuildDevDir
import opensavvy.gradle.vite.kotlin.viteBuildDistDir
import opensavvy.gradle.vite.kotlin.viteBuildProdDir
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsPlugin
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootPlugin
import java.io.File

private fun ViteExec.execConventions() {
	dependsOn(":kotlinNpmInstall")
	group = KotlinVitePlugin.GROUP

	fun File.child(name: String) = File(this, name)

	nodePath.set {
		File(NodeJsPlugin.apply(project).executable.get())
	}

	vitePath.set {
		NodeJsRootPlugin.apply(project.rootProject)
			.projectPackagesDirectory.get()
			.asFile.parentFile
			.child("node_modules")
			.child(".bin")
			.child("vite")
	}
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
