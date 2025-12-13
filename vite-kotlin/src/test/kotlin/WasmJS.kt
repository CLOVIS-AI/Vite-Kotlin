package opensavvy.gradle.vite.kotlin

import opensavvy.prepared.compat.filesystem.div
import opensavvy.prepared.compat.gradle.buildKts
import opensavvy.prepared.compat.gradle.gradle
import opensavvy.prepared.runner.testballoon.preparedSuite
import opensavvy.prepared.suite.config.CoroutineTimeout
import opensavvy.prepared.suite.prepared
import kotlin.io.path.*
import kotlin.time.Duration.Companion.minutes

val WasmJS by preparedSuite(preparedConfig = CoroutineTimeout(10.minutes)) {

	val simpleBuild by createBuild("app")
	val app by prepared { gradle.project("app") }

	test("Pure WasmJS") {
		simpleBuild()

		val indexHtml = app().dir / "src" / "wasmJsMain" / "resources" / "index.html"
		val main = app().dir / "src" / "wasmJsMain" / "kotlin" / "Main.kt"
		val dist = app().buildDir / "vite" / "wasmJs" / "dist"

		// language="kotlin"
		app().buildKts("""
			plugins {
				kotlin("multiplatform")
				id("dev.opensavvy.vite.kotlin")
			}
			
			kotlin {
				wasmJs {
					browser()
					binaries.executable()
				}
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
				<script src="gradle-test-app.mjs" type="module"></script>
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
		val assets = dist().resolve("assets")
		check(assets.listDirectoryEntries("gradle-test-app-*.wasm").isNotEmpty())
		check(assets.listDirectoryEntries("index-*.js").isNotEmpty())
	}

	test("Mixed JS and WasmJS") {
		simpleBuild()

		val main = app().dir / "src" / "commonMain" / "kotlin" / "Main.kt"
		val jsIndexHtml = app().dir / "src" / "jsMain" / "resources" / "index.html"
		val wasmIndexHtml = app().dir / "src" / "wasmJsMain" / "resources" / "index.html"
		val jsDist = app().buildDir / "vite" / "js" / "dist"
		val wasmDist = app().buildDir / "vite" / "wasmJs" / "dist"

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
				wasmJs {
					browser()
					binaries.executable()
				}
			}
		""".trimIndent())


		// language="HTML"
		jsIndexHtml().createParentDirectories()
		jsIndexHtml().writeText("""
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

		// language="HTML"
		wasmIndexHtml().createParentDirectories()
		wasmIndexHtml().writeText("""
			<!DOCTYPE html>
			<html lang="end">
			<head>
				<meta charset="UTF-8">
			</head>
			<body>
				<div id="root"></div>
				<script src="gradle-test-app.mjs" type="module"></script>
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
			.withArguments("app:jsViteBuild", "app:wasmJsViteBuild")
			.withPluginClasspath()
			.build()

		println(result.output)

		run {
			check(jsDist().exists())
			check(jsDist().listDirectoryEntries().map { it.name }.sorted() == listOf("assets", "index.html"))
			val assets = jsDist().resolve("assets")
			check(assets.listDirectoryEntries("gradle-test-app-*.wasm").isEmpty())
			check(assets.listDirectoryEntries("index-*.js").isNotEmpty())
		}

		run {
			check(wasmDist().exists())
			check(wasmDist().listDirectoryEntries().map { it.name }.sorted() == listOf("assets", "index.html"))
			val assets = wasmDist().resolve("assets")
			check(assets.listDirectoryEntries("gradle-test-app-*.wasm").isNotEmpty())
			check(assets.listDirectoryEntries("index-*.js").isNotEmpty())
		}
	}

}
