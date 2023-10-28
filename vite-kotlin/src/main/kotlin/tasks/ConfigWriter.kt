package opensavvy.gradle.vite.kotlin.tasks

import opensavvy.gradle.vite.base.tasks.WriteConfig
import opensavvy.gradle.vite.kotlin.KotlinVitePlugin
import opensavvy.gradle.vite.kotlin.viteBuildDevDir
import opensavvy.gradle.vite.kotlin.viteBuildDistDir
import opensavvy.gradle.vite.kotlin.viteBuildProdDir
import org.gradle.api.Project
import org.gradle.configurationcache.extensions.capitalized

/** Name of the default configuration task for the dev build. */
internal const val VITE_CONFIGURE_DEV_NAME = "viteConfigureDev"
/** Name of the default configuration task for the production build. */
internal const val VITE_CONFIGURE_PROD_NAME = "viteConfigureProd"

internal fun createConfigWriterTasks(project: Project) {
	project.tasks.register(VITE_CONFIGURE_DEV_NAME, WriteConfig::class.java) {
		dependsOn("viteCompileDev")
		group = KotlinVitePlugin.GROUP

		outDir.convention(project.viteBuildDistDir.map { it.asFile.absolutePath })
		buildRoot.convention(project.viteBuildDevDir.map { it.asFile.absolutePath })
	}

	project.tasks.register(VITE_CONFIGURE_PROD_NAME, WriteConfig::class.java) {
		dependsOn("viteCompileProd")
		group = KotlinVitePlugin.GROUP

		outDir.convention(project.viteBuildDistDir.map { it.asFile.absolutePath })
		buildRoot.convention(project.viteBuildProdDir.map { it.asFile.absolutePath })
	}

	project.tasks.named("clean") {
		dependsOn("clean${VITE_CONFIGURE_DEV_NAME.capitalized()}", "clean${VITE_CONFIGURE_PROD_NAME.capitalized()}")
	}
}
