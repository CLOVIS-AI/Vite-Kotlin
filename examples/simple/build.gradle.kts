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
	}
}

vite {
	server {
		proxy("/api", "http://localhost:4567")
	}
}
