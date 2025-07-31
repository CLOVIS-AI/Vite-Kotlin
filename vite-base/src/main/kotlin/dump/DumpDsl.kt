package opensavvy.gradle.vite.base.dump

import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Provider

private const val notConfigured = "(none)"

/**
 * Creates an information dump. See [dump].
 */
class DumpDsl internal constructor() {
	private val sections = ArrayList<SectionDsl>()

	fun section(name: String, block: SectionDsl.() -> Unit) {
		sections += SectionDsl(name).apply(block)
	}

	override fun toString(): String = buildString {
		val paddingSize = 2 + sections
			.flatMap { it.stored.keys }
			.maxOf { it.length }
		val padding = " ".repeat(paddingSize)

		for (section in sections) {
			appendLine("» ${section.name}")

			for ((title, values) in section.stored) {
				val spacingSize = paddingSize - title.length
				val spacing = " ".repeat(spacingSize)
				appendLine("$title${spacing}${values.firstOrNull() ?: notConfigured}")

				for (value in values.drop(1)) {
					appendLine("$padding$value")
				}
			}

			appendLine()
		}
	}

	inner class SectionDsl internal constructor(internal val name: String) {
		internal val stored = HashMap<String, List<String>>()

		private fun Any?.customToString(): String = when (this) {
			is Provider<*> -> map { it.customToString() }.getOrElse(notConfigured)
			else -> toString()
		}

		fun value(title: String, value: Any?) {
			when (value) {
				is Iterable<*> -> stored[title] = value.map { it.customToString() }.toList()
				is ListProperty<*> -> value(title, value.getOrElse(emptyList()))
				null -> value(title, emptyList<Any>())
				else -> stored[title] = listOf(value.customToString())
			}
		}
	}
}

/**
 * Creates an information dump.
 *
 * ### Example
 *
 * ```kotlin
 * println(dump {
 *     section("Hello") {
 *         value("Path", "first")
 *         value("Other", "first", "second")
 *     }
 * })
 * ```
 */
fun dump(block: DumpDsl.() -> Unit): String = DumpDsl()
	.apply(block)
	.toString()
