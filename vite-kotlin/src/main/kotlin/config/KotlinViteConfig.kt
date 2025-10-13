package opensavvy.gradle.vite.kotlin.config

import opensavvy.gradle.vite.base.config.ViteConfig
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.Internal

/**
 * The configuration block with options specific to the Kotlin Multiplatform projects.
 */
interface KotlinViteConfig : ViteConfig {

	/**
	 * A list of targets names that should not be configured by the Vite plugin.
	 *
	 * ### Example
	 *
	 * ```kotlin
	 * vite {
	 *     disabledTargets("wasmJs")
	 * }
	 * ```
	 */
	@get:Internal
	val disabledTargets: ListProperty<String>

	fun disabledTargets(vararg targets: String) = disabledTargets.addAll(*targets)
}
