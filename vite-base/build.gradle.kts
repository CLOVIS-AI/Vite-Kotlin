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
			id = "dev.opensavvy.vite.base"
			implementationClass = "opensavvy.gradle.vite.base.BaseVitePlugin"
		}
	}
}

library {
	name.set("Vite for Gradle")
	description.set("Gradle plugin to use Vite")
	homeUrl.set("https://gitlab.com/opensavvy/kotlin-vite")
}

java {
	withSourcesJar()
}

afterEvaluate {
	tasks.named("generatePomFileForVitePluginMarkerMavenPublication", GenerateMavenPom::class.java) {
		pom.name.set("Vite for Gradle")
		pom.description.set("Plugin marker for the Gradle plugin for Vite")
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
