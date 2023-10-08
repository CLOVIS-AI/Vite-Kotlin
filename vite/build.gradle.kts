plugins {
	id("conventions.base")
	id("conventions.library")

	`kotlin-dsl`
}

repositories {
	mavenCentral()
}

gradlePlugin {
	plugins {
		create("vite") {
			id = "dev.opensavvy.vite.kotlin"
			implementationClass = "opensavvy.gradle.vite.kotlin.KotlinVitePlugin"
		}
	}
}

dependencies {
	compileOnly("org.jetbrains.kotlin:kotlin-gradle-plugin:${libs.versions.kotlin.get()}")
}

library {
	name.set("Vite for Kotlin")
	description.set("Gradle plugin to use Vite instead of Webpack in Kotlin Multiplatform projects")
	homeUrl.set("https://gitlab.com/opensavvy/kotlin-vite")
}
