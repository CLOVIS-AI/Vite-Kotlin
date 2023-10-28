package opensavvy.gradle.vite.base.dump

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
				appendLine("$title${spacing}${values.firstOrNull() ?: "(none)"}")

				for (value in values.drop(1)) {
					appendLine("$padding$value")
				}
			}

			appendLine()
		}
	}

	inner class SectionDsl internal constructor(internal val name: String) {
		internal val stored = HashMap<String, List<String>>()

		fun value(title: String, value: String) {
			stored[title] = listOf(value)
		}

		fun value(title: String, values: Iterable<Any>) {
			stored[title] = values.map { it.toString() }.toList()
		}

		fun value(title: String, vararg value: Any) =
			value(title, value.asIterable())

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
