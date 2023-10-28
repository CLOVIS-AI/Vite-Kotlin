package opensavvy.gradle.vite.kotlin.config

internal fun ViteConfig.defaultConfiguration() {
	// Root
	version.convention("4.4.9")

	plugin("@originjs/vite-plugin-commonjs", "viteCommonjs", "1.0.3", isNamedExport = true)
	plugin("@rollup/plugin-commonjs", "commonjs", "24.0.1")

	// Build
	build.target.convention("modules")
}
