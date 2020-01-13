package control

import main.Settings
import java.awt.Dimension
import java.awt.GridLayout
import java.awt.Insets
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import javax.imageio.ImageIO
import javax.swing.*

/**
 * a scroll menu to display images on as buttons
 * @author McAJBen@gmail.com
 * @since 1.0
 */
abstract class PicturePanel : JPanel() {

    companion object {
        private const val serialVersionUID = 2972394170217781329L
        /**
         * The number of picture per row of a picture panel
         */
        private const val GRID_WIDTH = 4
        /**
         * The size of the `ImageIcon` in each of the buttons
         */
        private val IMAGE_ICON_SIZE = Dimension(100, 60)

        /**
         * re-sizes an image and saves a lower quality version as a thumbnail
         * @param file the file input of the full size image
         */
        private fun createThumbnail(file: File) {
            val tFile = Settings.fileToThumb(file)
            if (!tFile.exists() || file.lastModified() > tFile.lastModified()) {
                try {
                    val bufferedImage = BufferedImage(
                        IMAGE_ICON_SIZE.width,
                        IMAGE_ICON_SIZE.height,
                        BufferedImage.TYPE_INT_RGB
                    )
                    bufferedImage.graphics.drawImage(
                        ImageIO.read(file).getScaledInstance(
                            IMAGE_ICON_SIZE.width,
                            IMAGE_ICON_SIZE.height,
                            BufferedImage.SCALE_SMOOTH
                        ),
                        0, 0, null
                    )
                    ImageIO.write(bufferedImage, "GIF", tFile)
                } catch (e: OutOfMemoryError) {
                    Settings.showError("Cannot create Thumbnail, file is probably too large", e)
                    e.printStackTrace()
                } catch (e: IOException) {
                    Settings.showError("Cannot create Thumbnail, file is probably too large", e)
                    e.printStackTrace()
                }
            }
        }
    }

    init {
        layout = GridLayout(0, GRID_WIDTH)
        border = BorderFactory.createEmptyBorder()
    }

    /**
     * creates a button for a `PicturePanel` by loading an image from file
     * @param file the file of an image to add
     * @return a button with the proper settings for a `PicturePanel`
     */
    fun createPPButton(file: File): JButton {
        createThumbnail(file)

        return JButton(file.name).apply {
            margin = Insets(0, 0, 0, 0)
            isFocusPainted = false
            verticalTextPosition = SwingConstants.TOP
            horizontalTextPosition = SwingConstants.CENTER
            background = Settings.DISABLE_COLOR
            addActionListener {
                val button = it.source as JButton
                val name = button.text
                if (button.background === Settings.DISABLE_COLOR) {
                    select(name)
                    button.background = Settings.ENABLE_COLOR
                } else {
                    deselect(name)
                    button.background = Settings.DISABLE_COLOR
                }
            }
        }
    }

    /**
     * removes all images
     */
    fun clearButtons() {
        for (c in components) {
            if (c.javaClass == JButton::class.java) {
                remove(c)
            }
        }
    }

    /**
     * called when an image is selected
     * @param name the name of the image
     */
    protected abstract fun select(name: String)

    /**
     * called when an image is deselected
     * @param name the name of the image
     */
    protected abstract fun deselect(name: String)

    /**
     * loads the thumbnails from file
     * @param folder the folder that the original image was in
     */
    fun rememberThumbnails(folder: File) {
        for (c in components) {
            if (c.javaClass == JButton::class.java) {
                val b = c as JButton
                var f = File(folder, b.text)
                f = Settings.fileToThumb(f)
                try {
                    b.icon = ImageIcon(ImageIO.read(f))
                } catch (e: OutOfMemoryError) {
                    Settings.showError("Cannot load Thumbnail, file is probably too large", e)
                    e.printStackTrace()
                } catch (e: IOException) {
                    Settings.showError("Cannot load Thumbnail, file is probably too large", e)
                    e.printStackTrace()
                }
            }
        }
    }

    /**
     * removes the thumbnails from local memory
     */
    fun forgetThumbnails() {
        for (c in components) {
            if (c.javaClass == JButton::class.java) {
                val b = c as JButton
                b.icon = null
            }
        }
    }
}