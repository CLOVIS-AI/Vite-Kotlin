plugins {
	base
	kotlin("multiplatform") version "2.1.20" apply false
}

repositories {
	mavenCentral()
}

project.plugins.withType<org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsPlugin> {
	project.the<org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsEnvSpec>().version = "22.12.0"
}
