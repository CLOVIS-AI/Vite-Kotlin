package opensavvy.gradle.vite.base.config

import org.gradle.api.Project
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.Internal

/**
 * Declares which resources from other Kotlin Multiplatform projects should be imported.
 *
 * ```kotlin
 * vite {
 *     resources {
 *         // …
 *     }
 * }
 * ```
 */
interface ViteResourceConfig {

	/**
	 * Local Gradle projects from which resources should be imported (identified by their [path][Project.getPath]).
	 *
	 * The projects in this list should have the Kotlin Multiplatform plugin and the JS platform enabled.
	 * They do not need to have the Vite plugin.
	 *
	 * For convenience, you may use the [from] method.
	 */
	@get:Internal
	val projects: ListProperty<String>

	/**
	 * Adds a project's resources to the current project's directory.
	 *
	 * ### Example with typesafe project accessors
	 *
	 * ```kotlin
	 * vite {
	 *     resources {
	 *         from(projects.core)
	 *     }
	 * }
	 * ```
	 *
	 * Learn more about [typesafe project accessors](https://docs.gradle.org/current/userguide/declaring_dependencies.html#sec:type-safe-project-accessors).
	 *
	 * ### Example with the 'project' accessor
	 *
	 * ```kotlin
	 * vite {
	 *     resources {
	 *         from(project(":core"))
	 *     }
	 * }
	 * ```
	 */
	fun from(project: Project) {
		projects.add(project.path)
	}

}
