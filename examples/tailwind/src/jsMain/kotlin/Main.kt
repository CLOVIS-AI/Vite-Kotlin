package opensavvy.gradle.vite.kotlin.example.tailwind

import kotlinx.browser.document
import kotlinx.browser.window
import kotlin.js.Date

fun main() {
	println("Hello world!")
	createOrModifyDiv("Hello from Tailwind !")
}

private fun createOrModifyDiv(content: String){
	val div = document.querySelector("div")
		?: document.createElement("div").also { document.body?.appendChild(it) }
	div.innerHTML = content
	div.className = "w-48 flex justify-center items-center m-2 p-3 text-gray-200 rounded-xl bg-indigo-600 hover:bg-indigo-800 hover:cursor-not-allowed select-none transition-colors ease-in-out"
}
