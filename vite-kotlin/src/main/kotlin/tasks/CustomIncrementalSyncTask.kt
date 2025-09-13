package opensavvy.gradle.vite.kotlin.tasks

/*
 * Original code copied from org.jetbrains.kotlin.gradle.targets.js.ir.DefaultIncrementalSyncTask
 *
 * Added addFilter method to allow registering filtering callbacks.
 */

import org.gradle.api.DefaultTask
import org.gradle.api.file.CopySpec
import org.gradle.api.file.FileCopyDetails
import org.gradle.api.file.FileSystemOperations
import org.gradle.api.model.ObjectFactory
import org.gradle.api.tasks.TaskAction
import org.gradle.work.ChangeType
import org.gradle.work.DisableCachingByDefault
import org.gradle.work.InputChanges
import org.jetbrains.kotlin.gradle.targets.js.internal.RewriteSourceMapFilterReader
import org.jetbrains.kotlin.gradle.tasks.IncrementalSyncTask
import java.io.File
import javax.inject.Inject

@DisableCachingByDefault
abstract class CustomIncrementalSyncTask : DefaultTask(), IncrementalSyncTask {

	@get:Inject
	abstract val fs: FileSystemOperations

	@get:Inject
	abstract val objectFactory: ObjectFactory

	private val rootDir = project.rootDir

	private val filters = mutableListOf<FileCopyDetails.() -> Unit>()

	fun addFilter(filter: FileCopyDetails.() -> Unit) {
		filters.add(filter)
	}

	@TaskAction
	fun doCopy(inputChanges: InputChanges) {
		val destinationDir = destinationDirectory.get()
		val commonAction: CopySpec.() -> Unit = {
			into(destinationDir)
			duplicatesStrategy = this@CustomIncrementalSyncTask.duplicatesStrategy
			// Rewrite relative paths in sourcemaps in the target directory
			eachFile {
				if (name.endsWith(".js.map")) {
					filter(
						mapOf(
							RewriteSourceMapFilterReader::srcSourceRoot.name to file.parentFile,
							RewriteSourceMapFilterReader::targetSourceRoot.name to destinationDir
						),
						RewriteSourceMapFilterReader::class.java
					)
				}

				if (name.endsWith(".wasm.map")) {
					filter(
						mapOf(
							RewriteSourceMapFilterReader::srcSourceRoot.name to file.parentFile,
							RewriteSourceMapFilterReader::targetSourceRoot.name to rootDir
						),
						RewriteSourceMapFilterReader::class.java
					)
				}
				filters.forEach { it() }
			}
		}

		val work = if (!inputChanges.isIncremental) {
			fs.copy {
				from(from)
				commonAction()
			}.didWork
		} else {
			val changedFiles = inputChanges.getFileChanges(from)

			val modified = changedFiles
				.filter {
					it.changeType == ChangeType.ADDED || it.changeType == ChangeType.MODIFIED
				}
				.map { it.file }
				.toSet()

			val forCopy = from.asFileTree
				.matching {
					exclude {
						it.file.isFile && it.file !in modified
					}
				}

			val nonRemovingFiles = mutableSetOf<File>()

			from.asFileTree
				.visit {
					nonRemovingFiles.add(relativePath.getFile(destinationDir))
				}

			val removingFiles = objectFactory.fileTree()
				.from(destinationDir)
				.also { fileTree ->
					fileTree.exclude {
						it.file.isFile && it.file in nonRemovingFiles
					}
				}

			val deleteWork = fs.delete {
				delete(removingFiles)
			}

			val copyWork = fs.copy {
				from(forCopy)
				commonAction()
			}

			deleteWork.didWork || copyWork.didWork
		}

		didWork = work
	}
}
