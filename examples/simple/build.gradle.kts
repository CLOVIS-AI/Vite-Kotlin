plugins {
	kotlin("multiplatform") version "1.9.10"

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
	}
}
