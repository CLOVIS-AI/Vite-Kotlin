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
		create("kotlin") {
			id = "dev.opensavvy.vite.kotlin"
			implementationClass = "opensavvy.gradle.vite.kotlin.KotlinVitePlugin"
		}
	}
}

dependencies {
	compileOnly("org.jetbrains.kotlin:kotlin-gradle-plugin:${libsCommon.versions.kotlin.get()}")

	api(projects.viteBase)
}

library {
	name.set("Vite for Kotlin")
	description.set("Gradle plugin to use Vite instead of Webpack in Kotlin Multiplatform projects")
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
	tasks.named("generatePomFileForKotlinPluginMarkerMavenPublication", GenerateMavenPom::class.java) {
		pom.name.set("Vite for Kotlin")
		pom.description.set("Plugin marker for the Gradle plugin Vite for Kotlin")
		pom.url.set("https://gitlab.com/opensavvy/automation/kotlin-vite")

		pom.scm {
			url.set("https://gitlab.com/opensavvy/automation/kotlin-vite")
		}
	}
}
