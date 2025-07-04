plugins {
	kotlin("multiplatform")

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

	val jsMain by sourceSets.getting {
		dependencies {
			implementation(project(":core"))
		}
	}
}
