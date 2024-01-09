plugins {
	alias(opensavvyConventions.plugins.base)
	alias(opensavvyConventions.plugins.plugin)
	alias(opensavvyConventions.plugins.kotlin.abstractLibrary)

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

	license.set {
		name.set("Apache 2.0")
		url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
	}
}

kotlin {
	jvmToolchain(11)
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
}
