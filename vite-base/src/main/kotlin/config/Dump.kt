package opensavvy.gradle.vite.base.config

import opensavvy.gradle.vite.base.dump.DumpDsl

fun DumpDsl.dumpViteConfig(config: ViteConfig) {
	section("Top-level") {
		value("Vite version", config.version)
		value("Plugins", config.plugins)

		value("Root", config.root)
		value("Base", config.base)
	}

	section("Build") {
		value("Target", config.build.target)
	}

	section("Transitive resource dependencies") {
		value("Projects", config.resources.projects)
	}
}
