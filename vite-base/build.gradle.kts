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
		create("base") {
			id = "dev.opensavvy.vite.base"
			implementationClass = "opensavvy.gradle.vite.base.BaseVitePlugin"
		}
	}
}

library {
	name.set("Vite for Gradle")
	description.set("Gradle plugin to use Vite")
	homeUrl.set("https://gitlab.com/opensavvy/automation/kotlin-vite")
}

kotlin {
	jvmToolchain(11)
}

java {
	withSourcesJar()
}

afterEvaluate {
	tasks.named("generatePomFileForBasePluginMarkerMavenPublication", GenerateMavenPom::class.java) {
		pom.name.set("Vite for Gradle")
		pom.description.set("Plugin marker for the Gradle plugin for Vite")
		pom.url.set("https://gitlab.com/opensavvy/automation/kotlin-vite")

		pom.scm {
			url.set("https://gitlab.com/opensavvy/automation/kotlin-vite")
		}
	}

	publishing {
		publications.named("basePluginMarkerMaven", MavenPublication::class.java) {
			from(components["java"])
		}
	}
}
