package main

import main.Main.controlWindow
import java.awt.Color
import java.awt.Dimension
import java.awt.image.BufferedImage
import java.io.File
import java.lang.Error
import java.lang.Integer.max
import javax.swing.ImageIcon
import javax.swing.JButton
import javax.swing.JOptionPane

/**
 * Contains static final variables that are used through Dungeon Board
 * @author McAJBen@gmail.com
 * @since 1.6
 */
object Settings {

    const val NAME = "Dungeon Board"
    /**
     * The Dungeon Board directory that contains all images
     */
    private val FOLDER = File(System.getProperty("user.dir"), NAME)
    /**
     * The folder currently in use on the paint layer
     */
    var PAINT_FOLDER: File? = null
    /**
     * an array of all the sub folders in the Dungeon Board folder by their `Mode`
     */
    val FOLDERS = arrayOf(
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
     * The folder containing saved paint masks
     */
    val PAINT_MASK_FOLDER = File(DATA_FOLDER, "Paint")
    /**
     * the main `ImageIcon` for Dungeon Board
     */
    val ICON = loadResource("icon.gif")
    /**
     * the `ImageIcon` for a refresh `JButton`
     */
    val ICON_REFRESH = loadResource("refresh.gif")
    /**
     * the `ImageIcon` for a flip `JButton`
     */
    val ICON_FLIP = loadResource("flip.gif")
    /**
     * the `ImageIcon` for a settings `JButton`
     */
    val ICON_SETTINGS = loadResource("settings.gif")
    /**
     * the `ImageIcon` that floats around the `DisplayLoading`
     */
    val ICON_DVD = loadResource("dvdlogo.gif")
    /**
     * the `ImageIcon` that sticks to the corner of `DisplayLoading`
     */
    val ICON_DVD2 = loadResource("dvdlogo2.gif")
    /**
     * an array of `ImageIcons` visualizing the drawing `Direction` state on a `JButton`
     */
    val DRAW_STYLE = arrayOf(
        loadResource("squigle.gif"),
        loadResource("vertical.gif"),
        loadResource("horizontal.gif")
    )
    /**
     * an array of `ImageIcons` visualizing the `DrawMode` state on a `JButton`
     */
    val DRAW_MODE = arrayOf(
        loadResource("mouse.gif"),
        loadResource("visible.gif"),
        loadResource("invisible.gif"),
        loadResource("move.gif")
    )
    /**
     * an array of `ImageIcons` visualizing the `Pen` state on a `JButton`
     */
    val PEN_TYPE = arrayOf(
        loadResource("circle.gif"),
        loadResource("square.gif"),
        loadResource("rect.gif")
    )
    /**
     * a blank 3x3 `BufferedImage` for displaying an invisible cursor, or as a placeholder for an image;
     */
    val BLANK_CURSOR = BufferedImage(3, 3, BufferedImage.TYPE_INT_ARGB)
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
     * the color of an active button - (153, 255, 187)
     */
    val ACTIVE = Color(153, 255, 187)
    /**
     * the color of an inactive button - (255, 128, 128)
     */
    val INACTIVE = Color(255, 128, 128)
    /**
     * the color of an enabled button - Color.GREEN
     */
    val ENABLE_COLOR = Color(0, 255, 0)
    /**
     * the color of an disabled button - Color.GRAY
     */
    val DISABLE_COLOR = Color(128, 128, 128)
    /**
     * the green color used on `DrawPanel` to show a visible part of the map
     */
    val CLEAR = Color(100, 255, 100)
    /**
     * the red color used on `DrawPanel` to show a blocked part of the map
     */
    val OPAQUE = Color(255, 100, 100)
    /**
     * the pink color used on `DrawPanel` to show the cursor and outline of player's view
     */
    val PINK = Color(255, 0, 255)
    /**
     * the semi-transparent pink color used on `DrawPanel`
     */
    val PINK_CLEAR = Color(255, 0, 255, 25)
    /**
     * the background color used through Dungeon Board - (153, 153, 153)
     */
    val BACKGROUND = Color(153, 153, 153)
    /**
     * the background for user controls like `JButtons` and `JComboBoxs` - (200, 200, 200)
     */
    val CONTROL_BACKGROUND = Color(200, 200, 200)
    /**
     * true if the environment's Operating System is Window, false if not (currently unused)
     */
    val IS_WINDOWS = System.getProperty("os.name").startsWith("Windows")
    /**
     * The active layers in `DisplayPaint`
     */
    var PAINT_IMAGES: BooleanArray? = null
    /**
     * the number of threads this computer has
     */
    val SYS_THREADS = max(1, Runtime.getRuntime().availableProcessors() - 1)
    /**
     * how much to scale down the image for `ControlPaint`
     */
    const val PAINT_GUIDE_SCALE = 5
    /**
     * number of pixels on the Paint image that are being covered by a single pixel on the mask.<br></br>
     * - higher number means the shadows will be more blocky<br></br>
     * - lower number means the shadows will be more fine, but will use more memory and CPU time
     */
    const val PIXELS_PER_MASK = 5
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
        fun ensureDirExists(dir: File) {
            if (!dir.exists()) {
                dir.mkdirs()
            }
        }
        FOLDERS.forEach { ensureDirExists(it) }
        ensureDirExists(DATA_FOLDER)
        ensureDirExists(IMAGE_THUMB_FOLDER)
        ensureDirExists(LAYER_THUMB_FOLDER)
        ensureDirExists(PAINT_MASK_FOLDER)
    }

    /**
     * method used to load resources by file name
     * @param res name of the file to be loaded
     * @return an `ImageIcon` from the resources folder
     */
    fun loadResource(res: String): ImageIcon {
        return ImageIcon(Settings::class.java.getResource("/resources/$res"))
    }

    /**
     * creates and returns a `JButton` with the proper look
     * @param label text to be shown on the `JButton`
     * @return a `JButton` that looks like the standard for Dungeon Board
     */
    fun createButton(label: String): JButton {
        return JButton(label).apply {
            isFocusPainted = false
            isRolloverEnabled = false
        }
    }

    /**
     * creates and returns a `JButton` with the proper look
     * @param imageIcon to be shown on the `JButton`
     * @return a `JButton` that looks like the standard for Dungeon Board
     */
    fun createButton(imageIcon: ImageIcon): JButton {
        return JButton(imageIcon).apply {
            isFocusPainted = false
            isRolloverEnabled = false
        }
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

    /**
     * shows a `JOptionPane` to display an error
     * @param message custom text to be displayed
     */
    fun showError(message: String) {
        JOptionPane.showMessageDialog(controlWindow, message, "Error", JOptionPane.ERROR_MESSAGE)
    }

    /**
     * shows a `JOptionPane` to display an error
     * @param message custom text to be displayed
     * @param error the error that was thrown
     */
    fun showError(message: String, error: Error?) {
        error?.printStackTrace()
        JOptionPane.showMessageDialog(controlWindow, message + "\n" + error?.message)
    }

    /**
     * shows a `JOptionPane` to display an error
     * @param message custom text to be displayed
     * @param error the error that was thrown
     */
    fun showError(message: String, error: Exception) {
        error.printStackTrace()
        JOptionPane.showMessageDialog(controlWindow, message + "\n" + error.message)
    }

    /**
     * An alternate to `File.listFiles()` which returns in alphabetical order
     * @param folder The folder to look for files in
     * @return a `LinkedList<File>` of all files in `folder` in alphabetical order
     */
    fun listFilesInOrder(folder: File): List<File> {
        return folder.listFiles()?.sortedWith(compareBy { it.absolutePath }) ?: listOf()
    }
}