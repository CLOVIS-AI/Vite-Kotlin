package opensavvy.gradle.vite.kotlin.tasks

import opensavvy.gradle.vite.base.config.ViteConfig
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByName
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinJsCompilation
import org.jetbrains.kotlin.gradle.plugin.mpp.fileExtension

internal fun configureFiltering(project: Project, config: ViteConfig) {
	/*
	 * afterEvaluate is used to access configuration options and to make sure
	 * Kotlin/JS plugin is configured
	 */
	project.afterEvaluate {
		if (config.autoRewriteIndex.get()) {
			val kotlin = project.extensions.findByType(KotlinMultiplatformExtension::class.java)
				?: error("Could not find the 'kotlin' block")
			val moduleName = kotlin.js().outputModuleName.get()
			val jsFileExtension = (kotlin.targets.getByName("js").compilations.getByName("main") as KotlinJsCompilation).fileExtension.get()
			val jsFileName = "$moduleName.$jsFileExtension"
			project.tasks.getByName("viteCompileKotlinDev", CustomIncrementalSyncTask::class) {
				addFilter {
					if (name == "index.html") {
						filter { s ->
							s.replace(Regex("<script src=\"$moduleName\\.js\"></script>"),
								"<script type=\"module\">import './$jsFileName'; if (import.meta.hot) { import.meta.hot.accept('./$jsFileName'); }</script>")
						}
					}
				}
			}
			project.tasks.getByName("viteCompileKotlinProd", CustomIncrementalSyncTask::class) {
				addFilter {
					if (name == "index.html") {
						filter { s ->
							s.replace(Regex("<script src=\"$moduleName\\.js\"></script>"),
								"<script src=\"$jsFileName\" type=\"module\"></script>")
						}
					}
				}
			}
		}
	}
}
