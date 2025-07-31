package opensavvy.gradle.vite.base.config

import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Internal
import org.intellij.lang.annotations.Language

/**
 * Development server options.
 *
 * ```kotlin
 * vite {
 *    server {
 *        // …
 *    }
 * }
 * ```
 */
interface ViteServerConfig {

	/**
	 * Specify which IP addresses the server should listen on.
	 *
	 * Set this to `0.0.0.0` to listen on all addresses, including LAN and public addresses.
	 *
	 * ### Example
	 *
	 * ```kotlin
	 * vite {
	 *     server {
	 *         host = "localhost"
	 *     }
	 * }
	 * ```
	 *
	 * ### External resources
	 *
	 * - [Official documentation](https://vite.dev/config/server-options.html#server-host)
	 * - When using the WSL, see [the documentation](https://learn.microsoft.com/en-us/windows/wsl/networking#accessing-a-wsl-2-distribution-from-your-local-area-network-lan).
	 */
	@get:Internal
	val host: Property<String>

	/**
	 * Specify server port.
	 *
	 * Note if the port is already being used, Vite will automatically try the next available port
	 * so this may not be the actual port the server ends up listening on.
	 *
	 * ### Example
	 *
	 * ```kotlin
	 * vite {
	 *     server {
	 *         port = 5173
	 *     }
	 * }
	 * ```
	 *
	 * ### External resources
	 *
	 * - [Official documentation](https://vite.dev/config/server-options.html#server-port)
	 */
	@get:Internal
	val port: Property<Int>

	/**
	 * Set to true to exit if port is already in use, instead of automatically trying the next available port.
	 *
	 * ### Example
	 *
	 * ```kotlin
	 * vite {
	 *     server {
	 *         strictPort = false
	 *     }
	 * }
	 * ```
	 *
	 * ### External resources
	 *
	 * - [Official documentation](https://vite.dev/config/server-options.html#server-strictport)
	 */
	@get:Internal
	val strictPort: Property<Boolean>

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
	@get:Internal
	val proxies: ListProperty<ProxyOptions>

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
	 */
	fun proxy(
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
		@Language("JSRegexp")
		url: String,

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
		target: String,

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
		changeOrigin: Boolean = false,

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
		ws: Boolean = false,

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
		replacePrefixBy: String? = null,
	) {
		proxies.add(
			ProxyOptions(
				url = url,
				target = target,
				changeOrigin = changeOrigin,
				ws = ws,
				replacePrefixBy = replacePrefixBy,
			)
		)
	}
}
