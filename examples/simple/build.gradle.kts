plugins {
	kotlin("multiplatform") version "1.9.10"

	// List of releases: https://gitlab.com/opensavvy/kotlin-vite/-/releases
	id("opensavvy.vite.kotlin") // version "REPLACE THIS"
}

repositories {
	mavenCentral()
}

kotlin {
	js(IR) {
		browser()
		binaries.executable()
	}
}

// Currently, the Webpack task from the Kotlin plugin conflicts with our own tasks.
// Hopefully, in the future, it should be possible to have them both for comparisons.
tasks.named("jsBrowserProductionWebpack") {
	onlyIf { false }
}
