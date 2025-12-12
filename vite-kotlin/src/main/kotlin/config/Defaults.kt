package opensavvy.gradle.vite.kotlin.config

import opensavvy.gradle.vite.base.config.ViteConfig

internal fun ViteConfig.defaultConfiguration() {
	setDefaults()

	plugin("vite-plugin-commonjs", "viteCommonjs", "0.10.4")
	plugin("vite-plugin-restart", "ViteRestart", "1.0.0", "{ reload: ['*.wasm'] }")

	// Build
	build.target.convention("es2015")
	build.modulePreload.convention(true)
}
