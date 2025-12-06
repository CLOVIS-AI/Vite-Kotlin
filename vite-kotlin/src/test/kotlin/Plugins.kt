package opensavvy.gradle.vite.kotlin

import opensavvy.prepared.compat.filesystem.div
import opensavvy.prepared.compat.gradle.buildKts
import opensavvy.prepared.compat.gradle.gradle
import opensavvy.prepared.runner.testballoon.preparedSuite
import opensavvy.prepared.suite.config.CoroutineTimeout
import opensavvy.prepared.suite.prepared
import kotlin.io.path.*
import kotlin.time.Duration.Companion.minutes

val PluginsTest by preparedSuite(preparedConfig = CoroutineTimeout(10.minutes)) {

	val simpleBuild by createBuild("app")
	val app by prepared { gradle.project("app") }

	test("Stylelint plugin") {
		simpleBuild()

		val indexHtml = app().dir / "src" / "jsMain" / "resources" / "index.html"
		val main = app().dir / "src" / "jsMain" / "kotlin" / "Main.kt"
		val dist = app().buildDir / "vite" / "js" / "dist"

		// language="kotlin"
		app().buildKts("""
			plugins {
				kotlin("multiplatform")
				id("dev.opensavvy.vite.kotlin")
			}
			
			kotlin {
				js {
					browser()
					binaries.executable()
				}

				sourceSets.jsMain.dependencies {
					implementation(devNpm("stylelint", "16"))
				}
			}

			vite {
				plugin("vite-plugin-stylelint", "stylelint", "6.0.2")
			}
		""".trimIndent())

		// language="HTML"
		indexHtml().createParentDirectories()
		indexHtml().writeText("""
			<!DOCTYPE html>
			<html lang="end">
			<head>
				<meta charset="UTF-8">
			</head>
			<body>
				<div id="root"></div>
				<script src="gradle-test-app.js" type="module"></script>
			</body>
			</html>
		""".trimIndent())

		// language="kotlin"
		main().createParentDirectories()
		main().writeText("""
			fun main() {
				println("Hello world!")
			}
		""".trimIndent())

		val result = gradle.runner()
			.withArguments("app:viteBuild")
			.withPluginClasspath()
			.build()

		println(result.output)

		check(dist().exists())
		check(dist().listDirectoryEntries().map { it.name }.sorted() == listOf("assets", "index.html"))
		check("stylelint()" in app().buildDir().resolve("vite/js/prod/vite.config.mjs").readText())
		check("vite-plugin-stylelint" in app().buildDir().resolve("vite/js/prod/node_modules").listDirectoryEntries().map { it.name })
	}

}
