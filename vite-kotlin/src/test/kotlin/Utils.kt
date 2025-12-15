package opensavvy.gradle.vite.kotlin

import opensavvy.prepared.compat.gradle.buildKts
import opensavvy.prepared.compat.gradle.gradle
import opensavvy.prepared.compat.gradle.settingsKts
import opensavvy.prepared.suite.prepared

fun createBuild(vararg projectName: String) = prepared {
	gradle.settingsKts("""
		rootProject.name = "gradle-test"
		
		dependencyResolutionManagement {
			repositories {
				mavenCentral()
			}
		}

		include(${projectName.joinToString { "\"$it\"" }})
	""".trimIndent())

	gradle.rootProject.buildKts("""
		plugins {
			kotlin("multiplatform") apply false
		}
		
		project.plugins.withType<org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsPlugin> {
			project.the<org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsEnvSpec>().version = "24.11.1"
		}
		
		project.plugins.withType<org.jetbrains.kotlin.gradle.targets.wasm.nodejs.WasmNodeJsPlugin> {
			project.the<org.jetbrains.kotlin.gradle.targets.wasm.nodejs.WasmNodeJsEnvSpec>().version = "24.11.1"
		}
	""".trimIndent())
}
