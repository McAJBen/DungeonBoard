package util

import main.Mode
import java.awt.Dimension
import java.awt.image.BufferedImage
import java.io.File
import java.lang.Integer.max

/**
 * Contains variables that are used through Dungeon Board
 * @author McAJBen@gmail.com
 * @since 1.6
 */
object Settings {

    private const val NAME = "Dungeon Board"

    /**
     * The Dungeon Board directory that contains all images
     */
    private val FOLDER = File(System.getProperty("user.dir"), NAME)

    /**
     * an array of all the sub folders in the Dungeon Board folder by their `Mode`
     */
    private val FOLDERS = arrayOf(
        File(FOLDER, "Layer"),
        File(FOLDER, "Image"),
        File(FOLDER, "Paint"),
        File(FOLDER, "Loading")
    )

    /**
     * a sub folder in Dungeon Board to store data separate from sessions
     */
    private val DATA_FOLDER = File(FOLDER, "Data")

    /**
     * The folder containing image thumbnails
     */
    private val IMAGE_THUMB_FOLDER = File(DATA_FOLDER, "Layer")

    /**
     * The folder containing layer thumbnails
     */
    private val LAYER_THUMB_FOLDER = File(DATA_FOLDER, "Image")

    /**
     * how much to scale down the image for `ControlPaint`
     */
    const val PAINT_GUIDE_SCALE = 5

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
        get() = "$NAME ${Build.versionText}"

    /**
     * The folder currently in use on the paint layer
     */
    var PAINT_FOLDER: File? = null

    /**
     * The Guide file inside of PAINT_FOLDER
     */
    val PAINT_GUIDE_FILE: File
        get() {
            return File(PAINT_FOLDER, "Guide.png")
        }

    /**
     * The folder containing saved paint masks
     */
    val PAINT_MASK_FOLDER = File(DATA_FOLDER, "Paint")

    /**
     * the image used by `DisplayPaint`
     */
    var PAINT_IMAGE: BufferedImage? = null

    /**
     * the image used by `ControlPaint`
     */
    var PAINT_CONTROL_IMAGE: BufferedImage? = null

    /**
     * the default size of the `ControlWindow`
     */
    val CONTROL_SIZE = Dimension(900, 700)

    /**
     * the size of the display that players see
     */
    var DISPLAY_SIZE: Dimension? = null

    /**
     * The active layers in `DisplayPaint`
     */
    var PAINT_IMAGES: BooleanArray? = null

    /**
     * the number of threads this computer has
     */
    val SYS_THREADS = max(1, Runtime.getRuntime().availableProcessors() - 1)

    /**
     * number of files in the current PAINT_FOLDER
     */
    var PAINT_FOLDER_SIZE = 0

    /**
     * initializes the `FOLDERS` and creates them if they do not exist
     * @throws SecurityException when the program is unable to create the folders
     */
    @Throws(SecurityException::class)
    fun load() {
        listOf(
            *FOLDERS,
            DATA_FOLDER,
            IMAGE_THUMB_FOLDER,
            LAYER_THUMB_FOLDER,
            PAINT_MASK_FOLDER
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
     * turns a file into its thumbnail equal
     * @param f the file referring to an image
     * @return a file in the thumbnail folder
     */
    fun fileToThumb(f: File): File {
        return File(File(DATA_FOLDER, f.parentFile.name), f.name)
    }

    /**
     * turns a thumbnail file into its normal file equal
     * @param f the file referring to the thumbnail
     * @return a file in the normal folders
     */
    fun thumbToFile(f: File): File {
        return File(File(FOLDER, f.parentFile.name), f.name)
    }

    /**
     * turns a folder into its data folder equal
     * @param f the folder referring to one in Settings.FOLDERS
     * @return a folder in the data folder
     */
    fun folderToDataFolder(f: File): File {
        return File(DATA_FOLDER, f.name)
    }

    /**
     * turns a file/folder into its data folder equal to store a mask for Paint
     * @param f the file referring to one in Settings.PAINT_FOLDER
     * @return a file in the data folder
     */
    fun fileToMaskFile(f: File): File {
        val filename = if (f.isDirectory) {
            "${f.name}.f"
        } else {
            f.name
        }
        return File(File(DATA_FOLDER, "Paint"), filename)
    }
}