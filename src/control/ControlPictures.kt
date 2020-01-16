package control

import display.DisplayPictures
import display.Scale
import util.Settings
import util.Colors
import util.Resources
import util.createButton
import java.awt.BorderLayout
import java.awt.Dimension
import java.io.File
import javax.swing.BorderFactory
import javax.swing.JComboBox
import javax.swing.JScrollPane

/**
 * a `Control` for the Layer and Image Utility
 * @param folder the folder that images are loaded from
 * @param display the display to post images to
 * @param allowList if more than 1 image should be allowed
 * - true will be for Layer Utility
 * - false will be for Image Utility
 * @author McAJBen@gmail.com
 * @since 2.0
 */
class ControlPictures(
    private val folder: File,
    private val display: DisplayPictures,
    allowList: Boolean
) : Control() {

    companion object {
        private const val serialVersionUID = -1679600820663944136L
    }

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

        picturePanel = object : PicturePanel() {
            override fun select(name: String) {
                if (!allowList) {
                    display.removeAllImages()
                    for (c in components) {
                        c.background = Colors.DISABLE_COLOR
                    }
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

    override fun setMainControl(b: Boolean) {
        if (b) {
            picturePanel.rememberThumbnails(folder)
        } else {
            picturePanel.forgetThumbnails()
        }
    }

    override fun load() {
        if (folder.exists()) {
            for (file in Settings.folderToDataFolder(folder).listFiles()!!) {
                val fileFromThumbnail = Settings.thumbToFile(file)
                if (!fileFromThumbnail.exists()) {
                    file.delete()
                }
            }
            picturePanel.clearButtons()
            PPButtonCreator(picturePanel, folder).run()
            repaint()
            revalidate()
            display.removeAllImages()
            picturePanel.rememberThumbnails(folder)
        }
    }

    override fun onClosing() {}
}