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

	sourceSets {
		val jsMain by getting {
			dependencies {
				implementation(devNpm("tailwindcss", "3.4.0"))
				implementation(devNpm("postcss", "8.4.33"))
				implementation(devNpm("autoprefixer", "10.4.16"))
			}
		}
	}
}
