package opensavvy.gradle.vite.kotlin.tasks

import opensavvy.gradle.vite.base.tasks.WriteConfig
import opensavvy.gradle.vite.kotlin.KotlinVitePlugin
import opensavvy.gradle.vite.kotlin.viteBuildDevDir
import opensavvy.gradle.vite.kotlin.viteBuildDistDir
import opensavvy.gradle.vite.kotlin.viteBuildProdDir
import org.gradle.api.Project
import org.gradle.internal.extensions.stdlib.capitalized

internal fun createConfigWriterTasks(project: Project, vitePrefix: String, targetName: String) {
	project.tasks.register("${vitePrefix}ConfigureDev", WriteConfig::class.java) {
		dependsOn("${vitePrefix}CompileDev")
		group = KotlinVitePlugin.GROUP

		config {
			root.set(project.viteBuildDevDir(targetName))
			publicDir.convention("../../../../..")

			build {
				outDir.set(project.viteBuildDistDir(targetName))
			}
		}
	}

	project.tasks.register("${vitePrefix}ConfigureProd", WriteConfig::class.java) {
		dependsOn("${vitePrefix}CompileProd")
		group = KotlinVitePlugin.GROUP

		config {
			root.convention(project.viteBuildProdDir(targetName))

			build {
				outDir.convention(project.viteBuildDistDir(targetName))
			}
		}
	}

	project.tasks.named("clean") {
		dependsOn("clean${vitePrefix.capitalized()}ConfigureDev", "clean${vitePrefix.capitalized()}ConfigureProd")
	}
}
