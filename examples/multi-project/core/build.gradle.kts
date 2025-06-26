plugins {
	kotlin("multiplatform")
}

repositories {
	mavenCentral()
}

kotlin {
	js(IR) {
		browser()
	}
}
