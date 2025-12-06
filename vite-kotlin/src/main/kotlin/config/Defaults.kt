package opensavvy.gradle.vite.kotlin.config

import opensavvy.gradle.vite.base.config.ViteConfig

internal fun ViteConfig.defaultConfiguration() {
	setDefaults()

	plugin("vite-plugin-commonjs", "viteCommonjs", "0.10.4")
	plugin("@rollup/plugin-commonjs", "commonjs", "28.0.6")
	plugin("vite-plugin-restart", "ViteRestart", "1.0.0", "{ reload: ['*.wasm'] }")
	plugin("vite-plugin-top-level-await", "topLevelAwait", "1.6.0")

	// Build
	build.target.convention("es2015")
	build.modulePreload.convention(true)
}
