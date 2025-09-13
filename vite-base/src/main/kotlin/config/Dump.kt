package opensavvy.gradle.vite.base.config

import opensavvy.gradle.vite.base.dump.DumpDsl

fun DumpDsl.dumpViteConfig(config: ViteConfig) {
	section("Top-level") {
		value("Vite version", config.version)
		value("Plugins", config.plugins)

		value("Root", config.root)
		value("Auto rewrite index", config.autoRewriteIndex)
		value("Base", config.base)
		value("Public dir", config.publicDir)
	}

	section("Build") {
		value("Target", config.build.target)
		value("ModulePreload", config.build.modulePreload)
	}

	section("Server") {
		value("Host", config.server.host)
		value("Port", config.server.port)
		value("StrictPort", config.server.strictPort)

		if (config.server.proxies.isPresent) {
			for (proxy in config.server.proxies.get()) {
				section("Proxy ${proxy.url}") {
					value("Target", proxy.target)
					value("Change origin", proxy.changeOrigin)
					value("WebSocket", proxy.ws)
					value("Replace prefix by", proxy.replacePrefixBy)
				}
			}
		}
	}
}
