package opensavvy.gradle.vite.kotlin.config

import opensavvy.gradle.vite.base.config.ViteConfig

internal fun ViteConfig.defaultConfiguration() {
	setDefaults()

	// Needed for commonJS output (default in KGP)
	// https://www.npmjs.com/package/vite-plugin-commonjs
	plugin("vite-plugin-commonjs", "viteCommonjs", "0.10.4")

	// Needed for WasmJS output, to reload the page on Wasm file changes
	// https://www.npmjs.com/package/vite-plugin-restart
	plugin("vite-plugin-restart", "ViteRestart", "1.0.0", "{ reload: ['*.wasm'] }")

	// Cache directory inside node_modules directory of the current target
	cacheDir.convention("../node_modules/.vite")

	// Build
	build.target.convention("es2015")
	build.modulePreload.convention(true)
}
