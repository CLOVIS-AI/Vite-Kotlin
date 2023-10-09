package opensavvy.gradle.vite.kotlin.config

import org.gradle.api.provider.Property

interface ViteBuildConfig {

	/**
	 * Browser compatibility target for the final bundle.
	 *
	 * For more information, see [the official documentation](https://vitejs.dev/config/build-options.html#build-target).
	 */
	val target: Property<String>

}
