package control

import display.DisplayPictures
import display.Scale
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import main.Mode
import util.*
import java.awt.BorderLayout
import java.awt.Dimension
import java.io.File
import javax.swing.BorderFactory
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
    private val picturePanel = object : PicturePanel(thumbnailFolder) {
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

            runBlocking {
                folder.listFilesInOrder().filter {
                    it.hasImageExtension()
                }.map {
                    async { picturePanel.add(picturePanel.createPPButton(it)) }
                }.forEach {
                    it.await()
                }
            }

            repaint()
            revalidate()
            display.removeAllImages()
            picturePanel.rememberThumbnails()
        }
    }

    override fun onClosing() {}
}