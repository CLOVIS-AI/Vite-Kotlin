package opensavvy.gradle.vite.base.config

import opensavvy.gradle.vite.base.dump.DumpDsl

fun DumpDsl.dumpViteConfig(config: ViteConfig) {
	section("Top-level") {
		value("Vite version", config.version.get())
		value("Plugins", config.plugins.get())
	}

	section("Build") {
		value("Target", config.build.target.get())
	}

	section("Transitive resource dependencies") {
		value("Projects", config.resources.projects.get())
	}
}
