package opensavvy.gradle.vite.kotlin.config

import opensavvy.gradle.vite.base.config.ViteConfig

internal fun ViteConfig.defaultConfiguration() {
	setDefaults()

	plugin("vite-plugin-commonjs", "viteCommonjs", "0.10.4")
	plugin("@rollup/plugin-commonjs", "commonjs", "28.0.6")

	// Build
	build.target.convention("modules")
	build.modulePreload.convention(true)
}
