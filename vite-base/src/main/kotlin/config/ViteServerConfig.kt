package opensavvy.gradle.vite.base.config

import org.gradle.api.provider.Property
import org.gradle.api.tasks.Internal

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

}
