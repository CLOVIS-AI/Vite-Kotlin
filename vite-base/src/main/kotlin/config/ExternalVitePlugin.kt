package opensavvy.gradle.vite.base.config

import org.intellij.lang.annotations.Language
import java.io.Serializable

/**
 * An external plugin for Vite downloaded via [NPM](https://npmjs.com).
 *
 * Multiple lists of plugins are available:
 * - [First-party plugins](https://vitejs.dev/plugins/),
 * - Third-party plugins listed by the [awesome-vite project](https://github.com/vitejs/awesome-vite#plugins).
 *
 * ### Examples
 *
 * To understand how to create an instance of this class, read the configuration example provided by the plugin.
 * Generally, a plugin is installed like so:
 * ```javascript
 * // vite.config.js
 *
 * import vue from '@vitejs/plugin-vue'
 *
 * export default {
 *     plugins: [vue()],
 * }
 * ```
 * The declaration should be:
 * ```kotlin
 * ExternalVitePlugin(
 *     exportedAs = "vue",
 *     packageName = "@vitejs/plugin-vue",
 *     version = "4.1.0",
 * )
 * ```
 *
 * If additional configuration is needed, for example like so:
 * ```javascript
 * // vite.config.js
 * import vue from '@vitejs/plugin-vue'
 *
 * export default {
 *   plugins: [
 *     vue({
 *       template: {
 *         compilerOptions: {
 *           // ...
 *         },
 *       },
 *     }),
 *   ],
 * }
 * ```
 * The declaration should be:
 * ```kotlin
 * ExternalVitePlugin(
 *     exportedAs = "vue",
 *     packageName = "@vitejs/plugin-vue",
 *     version = "4.1.0",
 *     configuration = """
 *         {
 *             template: {
 *                 compilerOptions: {
 *                     // ...
 *                 }
 *             }
 *         }
 *     """.trimIndent()
 * )
 * ```
 */
data class ExternalVitePlugin(
	/**
	 * The name of the function exported by this plugin.
	 *
	 * For example, for the [Vue plugin](https://github.com/vitejs/vite-plugin-vue/tree/main/packages/plugin-vue),
	 * which is natively configured like so:
	 * ```javascript
	 * import vue from '@vitejs/plugin-vue'
	 *
	 * export default {
	 *     plugins: [vue()],
	 * }
	 * ```
	 * the [exportedAs] is `vue`.
	 */
	val exportedAs: String,

	/**
	 * The name of the NPM package which contains this plugin.
	 *
	 * For example, for the [Vue plugin](https://github.com/vitejs/vite-plugin-vue/tree/main/packages/plugin-vue),
	 * which is natively configured like so:
	 * ```javascript
	 * import vue from '@vitejs/plugin-vue'
	 *
	 * export default {
	 *     plugins: [vue()],
	 * }
	 * ```
	 * the [packageName] is `@vitejs/plugin-vue`.
	 */
	val packageName: String,

	/**
	 * The version of the project, as it appears on [NPM](https://www.npmjs.com/).
	 */
	val version: String,

	/**
	 * Any additional configuration provided to the plugin.
	 */
	@param:Language("JavaScript", prefix = "const a = ")
	val configuration: String? = null,

	/**
	 * If `true`, the configuration will be generated as `import {name} from 'foo'`, whereas if `false` the
	 * configuration will be generated as `import name from 'foo'`.
	 */
	val isNamedExport: Boolean = false,
) : Serializable {

	override fun toString() = "$packageName $version"
}
