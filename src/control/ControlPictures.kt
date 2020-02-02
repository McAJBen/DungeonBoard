package control

import display.DisplayPictures
import display.Scale
import main.Mode
import util.*
import java.awt.BorderLayout
import java.awt.Dimension
import java.io.File
import java.util.concurrent.Executors
import javax.swing.BorderFactory
import javax.swing.JButton
import javax.swing.JComboBox
import javax.swing.JScrollPane

/**
 * a `Control` for the Layer and Image Utility
 * @param display the display to post images to
 * @param mode which mode this panel is running
 * @author McAJBen@gmail.com
 * @since 2.0
 */
class ControlPictures(
    private val display: DisplayPictures,
    mode: Mode
) : Control() {

    companion object {
        private const val serialVersionUID = -1679600820663944136L
    }

    /**
     * the folder that images are loaded from
     */
    private val folder = Settings.getFolder(mode)

    /**
     * data folder that contains cached thumbnails
     */
    private val thumbnailFolder = Settings.getDataFolder(mode)

    /**
     * if more than 1 image should be allowed
     */
    private val allowList = mode == Mode.LAYER

    /**
     * the scroll menu of images inside the folder
     */
    private val picturePanel: PicturePanel

    init {
        val scaleComboBox: JComboBox<Scale> = JComboBox<Scale>(Scale.values()).apply {
            background = Colors.CONTROL_BACKGROUND
            maximumSize = Dimension(100, 5000)
            selectedItem = Scale.UP_SCALE
            addActionListener { display.setScaleMode(selectedItem as Scale) }
        }

        val flipButton = createButton(Resources.ICON_FLIP).apply {
            background = Colors.CONTROL_BACKGROUND
            addActionListener { display.flip() }
        }

        picturePanel = object : PicturePanel(thumbnailFolder) {
            override fun select(name: String) {
                if (!allowList) {
                    display.removeAllImages()
                    components.forEach { it.background = Colors.DISABLE_COLOR }
                }
                display.addImage(name)
            }

            override fun deselect(name: String) {
                display.removeImage(name)
            }
        }

        add(
            northPanel.apply {
                add(scaleComboBox)
                add(flipButton)
            },
            BorderLayout.NORTH
        )
        add(
            JScrollPane(picturePanel).apply {
                background = Colors.CONTROL_BACKGROUND
                border = BorderFactory.createEmptyBorder()
            },
            BorderLayout.CENTER
        )
        load()
        picturePanel.forgetThumbnails()
        isVisible = true
    }

    /**
     * turns a thumbnail file into its normal file equal
     * @return a file in the normal folders
     */
    private fun File.thumbToFile(): File {
        return File(folder, name)
    }

    override fun setMainControl(b: Boolean) {
        if (b) {
            picturePanel.rememberThumbnails()
        } else {
            picturePanel.forgetThumbnails()
        }
    }

    override fun load() {
        if (folder.exists()) {

            // remove any thumbnails for files that don't exist
            thumbnailFolder.listFiles()!!.filterNot {
                it.thumbToFile().exists()
            }.forEach {
                it.delete()
            }

            picturePanel.clearButtons()

            val executor = Executors.newFixedThreadPool(Settings.SYS_THREADS)
            folder.listFilesInOrder().filter {
                it.extension.equals("PNG", ignoreCase = true)
                        || it.extension.equals("JPG", ignoreCase = true)
                        || it.extension.equals("JPEG", ignoreCase = true)
            }.map {
                executor.submit<JButton> {
                    picturePanel.createPPButton(it)
                }
            }.forEach {
                picturePanel.add(it.get())
            }
            executor.shutdown()

            repaint()
            revalidate()
            display.removeAllImages()
            picturePanel.rememberThumbnails()
        }
    }

    override fun onClosing() {}
}