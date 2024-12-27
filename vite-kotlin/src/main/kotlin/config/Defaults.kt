package opensavvy.gradle.vite.kotlin.config

import opensavvy.gradle.vite.base.config.ViteConfig

internal fun ViteConfig.defaultConfiguration() {
	setDefaults()

	plugin("@originjs/vite-plugin-commonjs", "viteCommonjs", "1.0.3", isNamedExport = true)
	plugin("@rollup/plugin-commonjs", "commonjs", "25.0.7")

	// Build
	build.target.convention("modules")
	build.modulePreload.convention(true)
}
