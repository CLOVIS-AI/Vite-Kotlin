package opensavvy.gradle.vite.kotlin.tasks

import opensavvy.gradle.vite.base.config.ViteConfig
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByName
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinJsCompilation
import org.jetbrains.kotlin.gradle.plugin.mpp.fileExtension

@OptIn(ExperimentalWasmDsl::class)
internal fun configureFiltering(project: Project, vitePrefix: String, target: KotlinTarget, config: ViteConfig) {
	if (config.autoRewriteIndex.get()) {
		val kotlin = project.extensions.findByType(KotlinMultiplatformExtension::class.java)
			?: error("Could not find the 'kotlin' block")
		val moduleName = if (target.targetName == "wasmJs")
			kotlin.wasmJs().outputModuleName.get()
		else kotlin.js().outputModuleName.get()
		val jsFileExtension = (target.compilations.getByName("main") as KotlinJsCompilation).fileExtension.get()
		val jsFileName = "$moduleName.$jsFileExtension"
		project.tasks.getByName("${vitePrefix}CompileKotlinDev", CustomIncrementalSyncTask::class) {
			addFilter {
				if (name == "index.html") {
					filter { s ->
						s.replace(Regex("<script src=\"$moduleName\\.js\"></script>"),
							"<script type=\"module\">import './$jsFileName'; if (import.meta.hot) { import.meta.hot.accept('./$jsFileName'); }</script>")
					}
				}
			}
		}
		project.tasks.getByName("${vitePrefix}CompileKotlinProd", CustomIncrementalSyncTask::class) {
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
