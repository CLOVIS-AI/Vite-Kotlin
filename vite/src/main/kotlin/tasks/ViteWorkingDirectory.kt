package opensavvy.gradle.vite.kotlin.tasks

import opensavvy.gradle.vite.kotlin.viteBuildDevDir
import opensavvy.gradle.vite.kotlin.viteBuildProdDir
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input

interface ViteWorkingDirectory {

	/**
	 * The root directory in which all Vite commands are executed, and where the configuration is created.
	 *
	 * This option is added by the Gradle plugin, and doesn't exist in Vite.
	 * It corresponds to the folder in which you would run Vite.
	 */
	@get:Input
	val buildRoot: Property<String>

	/**
	 * The project root directory, in which the `index.html` is located.
	 *
	 * For more information, see [the official documentation](https://vitejs.dev/config/shared-options.html#root) and
	 * the [official guide](https://vitejs.dev/guide/#index-html-and-project-root).
	 */
	@get:Input
	val root: Property<String>

	/**
	 * The base public path from which assets are served during development and production.
	 *
	 * For more information, see the [official documentation](https://vitejs.dev/config/shared-options.html#base) and
	 * the [official guide](https://vitejs.dev/guide/build.html#public-base-path).
	 */
	@get:Input
	val base: Property<String>

}

fun ViteWorkingDirectory.defaultLayoutForKotlin() {
	root.convention(buildRoot.map { "$it/kotlin" })
	base.convention(buildRoot)
}

fun ViteWorkingDirectory.defaultDevFor(project: Project) {
	buildRoot.convention(project.viteBuildDevDir.map { it.asFile.absolutePath })
}

fun ViteWorkingDirectory.defaultProdFor(project: Project) {
	buildRoot.convention(project.viteBuildProdDir.map { it.asFile.absolutePath })
}
