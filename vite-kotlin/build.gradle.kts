import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
	alias(opensavvyConventions.plugins.base)
	alias(opensavvyConventions.plugins.plugin)
	alias(opensavvyConventions.plugins.kotlin.abstractLibrary)
	alias(libsCommon.plugins.testBalloon)
	id("org.jetbrains.kotlin.plugin.power-assert")

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
	implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:${libsCommon.versions.kotlin.get()}")

	api(projects.viteBase)

	testImplementation(libsCommon.opensavvy.prepared.testBalloon)
	testImplementation(libsCommon.opensavvy.prepared.gradle)
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

@OptIn(ExperimentalKotlinGradlePluginApi::class)
powerAssert {
	functions = listOf(
		// Standard library
		"kotlin.assert",
		"kotlin.require",
		"kotlin.requireNotNull",
		"kotlin.check",
		"kotlin.checkNotNull",

		// Standard test library
		"kotlin.test.assertTrue",
		"kotlin.test.assertFalse",
		"kotlin.test.assertEquals",
		"kotlin.test.assertNull",

		// Prepared
		"opensavvy.prepared.suite.assertions.log",
	)
}
