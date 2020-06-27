package util

import main.Mode
import java.awt.Dimension
import java.io.File
import java.lang.Integer.max

/**
 * Contains variables that are used through Dungeon Board
 * @author McAJBen@gmail.com
 */
object Settings {

	private const val NAME = "Dungeon Board"

	/**
	 * The Dungeon Board directory that contains all images
	 */
	private val FOLDER =
		File(
			System.getProperty("user.dir"),
			NAME
		)

	/**
	 * an array of all the sub folders in the Dungeon Board folder by their `Mode`
	 */
	private val FOLDERS = arrayOf(
		File(
			FOLDER,
			"Layer"
		),
		File(
			FOLDER,
			"Image"
		),
		File(
			FOLDER,
			"Paint"
		),
		File(
			FOLDER,
			"Loading"
		)
	)

	/**
	 * a sub folder in Dungeon Board to store data separate from sessions
	 */
	private val DATA_FOLDER =
		File(
			FOLDER,
			"Data"
		)

	/**
	 * an array of all the sub folders that contain meta data by their `Mode`
	 */
	private val DATA_FOLDERS = arrayOf(
		File(
			DATA_FOLDER,
			"Layer"
		),
		File(
			DATA_FOLDER,
			"Image"
		),
		File(
			DATA_FOLDER,
			"Paint"
		),
		File(
			DATA_FOLDER,
			"Loading"
		)
	)

	/**
	 * number of pixels on the Paint image that are being covered by a single pixel on the mask.
	 * - higher number means the shadows will be more block-like
	 * - lower number means the shadows will be more fine, but will use more memory and CPU time
	 */
	const val PIXELS_PER_MASK = 5

	/**
	 * The title of windows
	 */
	val APP_TITLE: String
		get() = "$NAME ${Build.VERSION}"

	/**
	 * the default size of the `ControlWindow`
	 */
	val CONTROL_SIZE =
		Dimension(
			900,
			700
		)

	/**
	 * the default size of the `GridMenu`
	 */
	val GRID_MODAL_SIZE =
		Dimension(
			500,
			300
		)

	/**
	 * the size of the display that players see
	 */
	var DISPLAY_SIZE =
		Dimension(
			1,
			1
		)

	/**
	 * the number of threads this computer has
	 */
	val SYS_THREADS = max(
		1,
		Runtime.getRuntime().availableProcessors() - 1
	)

	/**
	 * initializes the `FOLDERS` and creates them if they do not exist
	 * @throws SecurityException when the program is unable to create the folders
	 */
	@Throws(SecurityException::class)
	fun load() {
		listOf(
			*FOLDERS,
			*DATA_FOLDERS
		).forEach {
			if (!it.exists()) {
				it.mkdirs()
			}
		}
	}

	/**
	 * gets the folder based on `Mode`
	 * @param mode which folder to return
	 * @return a directory for the given `Mode`
	 */
	fun getFolder(mode: Mode): File {
		return FOLDERS[mode.ordinal]
	}

	/**
	 * gets the `Mode` that this file belongs to
	 * @param file a file which is in one of the Dungeon Board folders
	 * @return The `Mode` this file belongs to or null
	 */
	fun getFileMode(file: File): Mode? {
		val ordinal = FOLDERS.indexOf(file.parentFile)
		return Mode.values().getOrNull(ordinal)
	}

	/**
	 * gets the folder that contains data for the specific mode
	 * @param mode the `Mode`
	 * @return the folder that contains data for the mode
	 */
	fun getDataFolder(mode: Mode): File {
		return DATA_FOLDERS[mode.ordinal]
	}
}