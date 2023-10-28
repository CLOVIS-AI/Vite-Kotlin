package opensavvy.gradle.vite.base.config

import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Nested
import org.intellij.lang.annotations.Language

/**
 * The main configuration block, exposed by the `vite` extension:
 *
 * ```kotlin
 * plugins {
 *     // …
 * }
 *
 * vite {
 *     // …
 * }
 * ```
 */
interface ViteConfig {

	/**
	 * Sets the default values for all configuration fields.
	 */
	fun setDefaults() {
		// Root
		version.convention("4.4.9")
		base.convention("/")
	}

	/**
	 * The version of the Vite package used by this build.
	 *
	 * ### Example
	 *
	 * ```kotlin
	 * vite {
	 *     version.set("3.0.0")
	 * }
	 * ```
	 *
	 * ### External resources
	 *
	 * - [List of versions](https://www.npmjs.com/package/vite?activeTab=versions).
	 */
	val version: Property<String>

	/**
	 * Project root directory, where `index.html` is located.
	 *
	 * ### Example
	 *
	 * ```kotlin
	 * vite {
	 *     root.set(project.layout.projectDirectory.file("index.html"))
	 * }
	 * ```
	 *
	 * ### External resources
	 *
	 * - [Official documentation](https://vitejs.dev/config/shared-options.html#root)
	 */
	val root: DirectoryProperty

	/**
	 * Base public path when served in development or production.
	 *
	 * Valid values include:
	 * - Absolute URL pathname, e.g. `/foo/`,
	 * - Full URL, e.g. `https://foo.com/`,
	 * - Empty string or `./` (for embedded deployment).
	 *
	 * ### Example
	 *
	 * To expose the generate website to the `/ui` path instead of the website root, use:
	 * ```kotlin
	 * vite {
	 *     base.set("/ui")
	 * }
	 * ```
	 *
	 * ### External resources
	 *
	 * - [Official documentation](https://vitejs.dev/config/shared-options.html#base)
	 * - [Setting the public base path](https://vitejs.dev/guide/build.html#public-base-path)
	 */
	val base: Property<String>

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
	 *
	 * ### Example
	 *
	 * ```kotlin
	 * vite {
	 *     build {
	 *         // …
	 *     }
	 * }
	 * ```
	 */
	@get:Nested
	val build: ViteBuildConfig

	/**
	 * Declare where transitive resources should be imported from.
	 *
	 * ### Example
	 *
	 * ```kotlin
	 * vite {
	 *     resources {
	 *         // …
	 *     }
	 * }
	 * ```
	 */
	@get:Nested
	val resources: ViteResourceConfig

	fun build(block: ViteBuildConfig.() -> Unit) = build.apply(block)
	fun resources(block: ViteResourceConfig.() -> Unit) = resources.apply(block)
}
