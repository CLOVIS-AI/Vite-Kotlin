plugins {
	id("conventions.base")
	id("conventions.library")

	`kotlin-dsl`
	`java-gradle-plugin`
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
	compileOnly("org.jetbrains.kotlin:kotlin-gradle-plugin:${playgroundLibs.versions.kotlin.get()}")

	api(projects.viteBase)
}

library {
	name.set("Vite for Kotlin")
	description.set("Gradle plugin to use Vite instead of Webpack in Kotlin Multiplatform projects")
	homeUrl.set("https://gitlab.com/opensavvy/kotlin-vite")
}

java {
	withSourcesJar()
}

afterEvaluate {
	tasks.named("generatePomFileForVitePluginMarkerMavenPublication", GenerateMavenPom::class.java) {
		pom.name.set("Vite for Kotlin")
		pom.description.set("Plugin marker for the Gradle plugin Vite for Kotlin")
		pom.url.set("https://gitlab.com/opensavvy/kotlin-vite")

		pom.scm {
			url.set("https://gitlab.com/opensavvy/kotlin-vite")
		}
	}

	publishing {
		publications.named("vitePluginMarkerMaven", MavenPublication::class.java) {
			from(components["java"])
		}
	}
}
