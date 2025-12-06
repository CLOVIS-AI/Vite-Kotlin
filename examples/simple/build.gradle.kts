plugins {
	kotlin("multiplatform") version "2.1.20"

	// List of releases: https://gitlab.com/opensavvy/kotlin-vite/-/releases
	id("dev.opensavvy.vite.kotlin") // version "REPLACE THIS"
}

repositories {
	mavenCentral()
}

kotlin {
	js(IR) {
		browser()
		binaries.executable()
		useEsModules()

		compilerOptions {
			target.set("es2015")
		}
	}
}

vite {
	server {
		proxy("/api", "http://localhost:4567")
	}
}

project.plugins.withType<org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsPlugin> {
	project.the<org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsEnvSpec>().version = "22.12.0"
}
