package opensavvy.gradle.vite.kotlin.config

import org.gradle.api.Project

internal fun ViteConfig.defaultConfigurationFor(project: Project) {
	// Root
	version.convention("4.4.9")
	buildRoot.convention(project.rootProject.layout.buildDirectory.dir("js/packages/${project.name}"))
	root.convention(buildRoot.dir("kotlin"))
	base.convention(buildRoot)
}
