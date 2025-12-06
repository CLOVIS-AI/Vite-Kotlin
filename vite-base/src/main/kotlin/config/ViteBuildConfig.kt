package opensavvy.gradle.vite.base.config

import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Internal

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
	 *         target.set("es2015")
	 *     }
	 * }
	 * ```
	 *
	 * ### External resources
	 *
	 * - [Official documentation](https://vitejs.dev/config/build-options.html#build-target)
	 */
	@get:Internal
	val target: Property<String>

	/**
	 * Whether to automatically inject a module preload polyfill.
	 *
	 * Currently, does not support the `resolveDependencies` function.
	 *
	 * ### Example
	 *
	 * ```kotlin
	 * vite {
	 *     build {
	 *         modulePreload.set(false)
	 *     }
	 * }
	 * ```
	 *
	 * ### External resources
	 *
	 * - [Official documentation](https://vitejs.dev/config/build-options.html#build-modulepreload)
	 */
	@get:Internal
	val modulePreload: Property<Boolean>

	/**
	 * Output directory of the production build.
	 *
	 * ### Example
	 *
	 * ```kotlin
	 * vite {
	 *     build {
	 *         outDir.set(project.layout.buildDirectory.dir("dist"))
	 *     }
	 * }
	 * ```
	 *
	 * ### External resources
	 *
	 * - [Official documentation](https://vitejs.dev/config/build-options.html#build-outdir)
	 */
	@get:Internal
	val outDir: DirectoryProperty

}
