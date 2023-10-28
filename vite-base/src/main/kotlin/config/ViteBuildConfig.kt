package opensavvy.gradle.vite.base.config

import org.gradle.api.provider.Property

/**
 * Rollup configuration.
 *
 * ```kotlin
 * vite {
 *     build {
 *         // …
 *     }
 * }
 * ```
 */
interface ViteBuildConfig {

	/**
	 * Browser compatibility target for the final bundle.
	 *
	 * For more information, see [the official documentation](https://vitejs.dev/config/build-options.html#build-target).
	 *
	 * ### Example
	 *
	 * ```kotlin
	 * vite {
	 *     build {
	 *         target.set("modules")
	 *     }
	 * }
	 * ```
	 */
	val target: Property<String>

}
