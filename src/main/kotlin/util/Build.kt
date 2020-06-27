package util

import java.util.*

/**
 * contains build information
 * @author McAJBen@gmail.com
 */
object Build {

	val VERSION: String

	init {
		val properties = Properties()
		properties.load(this.javaClass.getResourceAsStream("/version.properties"))
		VERSION = properties.getProperty("version")
	}
}