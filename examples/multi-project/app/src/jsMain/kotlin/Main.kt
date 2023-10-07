package opensavvy.gradle.vite.kotlin.example.multi.app

import opensavvy.gradle.vite.kotlin.example.multi.core.*
import kotlinx.browser.document
import kotlinx.browser.window

fun main() {
	createOrModifyDiv(helloWorld())
	window.setInterval({
		createOrModifyDiv(helloWorld())
	}, 1000)
}

private fun createOrModifyDiv(content: String){
	val div = document.querySelector("div")
		?: document.createElement("div").also { document.body?.appendChild(it) }
	div.innerHTML = content
}
