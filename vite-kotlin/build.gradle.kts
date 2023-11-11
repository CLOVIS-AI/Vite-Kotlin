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
		create("kotlin") {
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
	homeUrl.set("https://gitlab.com/opensavvy/automation/kotlin-vite")
}

kotlin {
	jvmToolchain(11)
}

java {
	withSourcesJar()
}

afterEvaluate {
	tasks.named("generatePomFileForKotlinPluginMarkerMavenPublication", GenerateMavenPom::class.java) {
		pom.name.set("Vite for Kotlin")
		pom.description.set("Plugin marker for the Gradle plugin Vite for Kotlin")
		pom.url.set("https://gitlab.com/opensavvy/automation/kotlin-vite")

		pom.scm {
			url.set("https://gitlab.com/opensavvy/automation/kotlin-vite")
		}
	}

	publishing {
		publications.named("kotlinPluginMarkerMaven", MavenPublication::class.java) {
			from(components["java"])

			// The plugins fight to declare dependencies
			// Since this is a Gradle plugin, we don't care about Maven users at all, so
			// we just remove all dependency declarations
			pom.withXml {
				val data = this.asElement()

				val dependencies = data.childNodes.asSequence()
					.filter { it.nodeName == "dependencies" }
					.toList()

				for (dependency in dependencies) {
					data.removeChild(dependency)
				}
			}
		}
	}
}

fun org.w3c.dom.NodeList.asSequence() = sequence<org.w3c.dom.Node> {
	repeat(length) {
		yield(item(it))
	}
}
