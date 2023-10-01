package opensavvy.gradle.vite.kotlin.config

import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property

interface ViteConfig {

	/**
	 * The version of the Vite package used by this build.
	 *
	 * The list of versions is available [on the NPM website](https://www.npmjs.com/package/vite?activeTab=versions).
	 */
	val version: Property<String>

	/**
	 * The root directory in which all Vite commands are executed, and where the configuration is created.
	 *
	 * This option is added by the Gradle plugin, and doesn't exist in Vite.
	 * It corresponds to the folder in which you would run Vite.
	 */
	val buildRoot: DirectoryProperty

	/**
	 * The project root directory, in which the `index.html` is located.
	 *
	 * For more information, see [the official documentation](https://vitejs.dev/config/shared-options.html#root) and
	 * the [official guide](https://vitejs.dev/guide/#index-html-and-project-root).
	 */
	val root: DirectoryProperty

	/**
	 * The base public path from which assets are served during development and production.
	 *
	 * For more information, see the [official documentation](https://vitejs.dev/config/shared-options.html#base) and
	 * the [official guide](https://vitejs.dev/guide/build.html#public-base-path).
	 */
	val base: DirectoryProperty


}
