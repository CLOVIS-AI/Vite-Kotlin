package opensavvy.gradle.vite.kotlin.example.simple

import kotlinx.browser.document
import kotlinx.browser.window
import kotlin.js.Date

fun main() {
	console.log("Hello world!")
	ping()
	window.setInterval({
		ping()
	}, 5000)
}

private fun createOrModifyDiv(content: String){
	val div = document.querySelector("div")
		?: document.createElement("div").also { document.body?.appendChild(it) }
	div.innerHTML = content
}

private fun ping() {
	val startTime = Date.now()
	window.fetch("https://jsonplaceholder.typicode.com").then {
		val finishTime = Date.now()
		createOrModifyDiv("Ping: ${finishTime - startTime} ms")
	}
}
