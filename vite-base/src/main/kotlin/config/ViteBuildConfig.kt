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
	 *         target.set("modules")
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
