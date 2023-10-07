plugins {
	kotlin("multiplatform") version "1.9.10"
}

repositories {
	mavenCentral()
}

kotlin {
	js(IR) {
		browser()
	}
}
