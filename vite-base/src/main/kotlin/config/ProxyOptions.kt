package opensavvy.gradle.vite.base.config

import org.intellij.lang.annotations.Language
import java.io.Serializable

/**
 * Configures the proxies started by the Vite development server.
 *
 * ### Example
 *
 * ```kotlin
 * vite {
 *     server {
 *         proxy("/foo", "http://localhost:4567")
 *     }
 * }
 * ```
 *
 * ### External resources
 *
 * - [Official documentation](https://vite.dev/config/server-options.html#server-proxy)
 *
 * @see ViteServerConfig.proxy
 */
data class ProxyOptions(
	/**
	 * The path that should be intercepted by the proxy.
	 *
	 * ### Example
	 *
	 * ```kotlin
	 * vite {
	 *     server {
	 *         proxy("/foo", "http://localhost:4567")
	 *
	 *         // The URL can also be declared as a regex:
	 *         proxy("^/fallback/.*", "http://jsonplaceholder.typicode.com", changeOrigin = true)
	 *     }
	 * }
	 * ```
	 *
	 * ### External resources
	 *
	 * - [Official documentation](https://github.com/http-party/node-http-proxy#options)
	 */
	@param:Language("JSRegexp")
	val url: String,

	/**
	 * The URL that the proxy should redirect to.
	 *
	 * ### Example
	 *
	 * ```kotlin
	 * vite {
	 *     server {
	 *         proxy("/foo", "http://localhost:4567")
	 *     }
	 * }
	 * ```
	 *
	 * ### External resources
	 *
	 * - [Official documentation](https://github.com/http-party/node-http-proxy#options)
	 */
	val target: String,

	/**
	 * Changes the origin of the host header to the target URL.
	 *
	 * ### Example
	 *
	 * ```kotlin
	 * vite {
	 *     server {
	 *         proxy("/foo", "http://localhost:4567", changeOrigin = true)
	 *     }
	 * }
	 * ```
	 *
	 * ### External resources
	 *
	 * - [Official documentation](https://github.com/http-party/node-http-proxy#options)
	 */
	val changeOrigin: Boolean,

	/**
	 * `true` to proxy WebSockets.
	 *
	 * ### Example
	 *
	 * ```kotlin
	 * vite {
	 *     server {
	 *         proxy("/foo", "http://localhost:4567", ws = true)
	 *     }
	 * }
	 * ```
	 *
	 * ### External resources
	 *
	 * - [Official documentation](https://github.com/http-party/node-http-proxy#options)
	 */
	val ws: Boolean,

	/**
	 * A path section that replaces the captured [url].
	 *
	 * ### Example
	 *
	 * ```kotlin
	 * vite {
	 *     server {
	 *         proxy("/foo", "http://localhost:4567", replacePrefixBy = "bar")
	 *     }
	 * }
	 * ```
	 *
	 * Then, a `GET /foo` is proxied to `GET http://localhost:4567/bar`.
	 */
	val replacePrefixBy: String?,
) : Serializable
