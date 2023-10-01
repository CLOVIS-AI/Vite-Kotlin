package opensavvy.gradle.vite.kotlin.config

import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Nested
import org.intellij.lang.annotations.Language

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

	/**
	 * The list of Vite plugins from NPM imported by this project.
	 *
	 * To easily add values of this property, we provide the [plugin] helper function.
	 *
	 * For more information on plugins, see [ExternalVitePlugin].
	 */
	val plugins: ListProperty<ExternalVitePlugin>

	/**
	 * Imports a plugin from NPM.
	 *
	 * This is a helper function to add an element to [plugins].
	 * For more information on the different parameters, see [ExternalVitePlugin].
	 *
	 * ### Example
	 *
	 * ```kotlin
	 * vite {
	 *     plugin("@originjs/vite-plugin-commonjs", "viteCommonJs", "1.0.3")
	 * }
	 * ```
	 */
	fun plugin(
		/** The NPM package name. See [ExternalVitePlugin.packageName]. */
		packageName: String,
		/** The JS symbol exported by the plugin. See [ExternalVitePlugin.exportedAs]. */
		exportedAs: String,
		/** The version of the plugin on NPM. See [ExternalVitePlugin.version]. */
		version: String,
		/** Optional additional configuration. See [ExternalVitePlugin.configuration]. */
		@Language("JavaScript") configuration: String? = null,
		/** Whether the package uses default or named exports. See [ExternalVitePlugin.isNamedExport]. */
		isNamedExport: Boolean = false,
	) {
		plugins.add(
			ExternalVitePlugin(
				exportedAs = exportedAs,
				packageName = packageName,
				version = version,
				configuration = configuration,
				isNamedExport = isNamedExport
			)
		)
	}

	/**
	 * Configuration for Rollup to build the production bundle.
	 */
	@get:Nested
	val build: ViteBuildConfig
}
