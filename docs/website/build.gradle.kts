plugins {
	alias(opensavvyConventions.plugins.base)
	id("dev.opensavvy.dokka-mkdocs")
}

dependencies {
	// List the 'library' projects
	dokka(projects.viteBase)
	dokka(projects.viteKotlin)
}
