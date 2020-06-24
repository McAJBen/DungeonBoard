package display

import control.PictureButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import main.Mode
import util.Settings.DISPLAY_SIZE
import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.geom.AffineTransform
import java.awt.image.BufferedImage
import java.util.concurrent.LinkedBlockingQueue
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * `JPanel` for displaying Image and Layer Utility
 * @param window callback to `DisplayWindow`
 * @param mode used to determine the folder that contains images
 * @author McAJBen@gmail.com
 * @since 2.0
 */
class DisplayPictures(
    window: DisplayWindow,
    private val mode: Mode
) : Display(window) {

    companion object {
        private const val serialVersionUID = 350995921778402576L
    }

    /**
     * a list of the images to be painted
     */
    private val images: LinkedBlockingQueue<PictureButton> = LinkedBlockingQueue()

    /**
     * the image being displayed by `DisplayPictures`
     */
    private var image: BufferedImage? = BufferedImage(
        DISPLAY_SIZE.width,
        DISPLAY_SIZE.height,
        BufferedImage.TYPE_INT_ARGB
    )

    /**
     * the method of scale to show the image
     */
    private var scaleMode = Scale.UP_SCALE

    /**
     * the last job that started to repaint `image`
     */
    private var compileJob: Job? = null

    /**
     * tells if the image should be flipped
     * - true will rotate the image by 180 degrees before displaying
     * - false will display normally
     */
    private var flip = false

    init {
        isVisible = true
    }

    /**
     * adds an image to be drawn on the panel
     * @param image the file to load an image from
     */
    fun addImage(image: PictureButton) {
        images.add(image)
        compileImage()
    }

    /**
     * removes an image by the name of the file
     * @param image the file that was used to load the image from
     */
    fun removeImage(image: PictureButton) {
        images.remove(image)
        compileImage()
    }

    /**
     * changes the scale mode
     * @param selectedItem the index of the new scale mode
     */
    fun setScaleMode(selectedItem: Scale) {
        scaleMode = selectedItem
        compileImage()
    }

    /**
     * removes all images from the screen
     */
    fun removeAllImages() {
        images.clear()
        compileImage()
    }

    /**
     * toggles the flip of the images. Rotates them by 180 degrees
     */
    fun flip() {
        flip = !flip
        repaint()
    }

    /**
     * turns the `AlphaImages` into a single image to be displayed
     */
    private fun compileImage() {
        compileJob?.cancel()
        compileJob = GlobalScope.launch(Dispatchers.Default) {
            val img = BufferedImage(
                DISPLAY_SIZE.width,
                DISPLAY_SIZE.height,
                BufferedImage.TYPE_INT_ARGB
            )
            val g2d = img.createGraphics()
            if (images.size == 0) {
                fillBackground(g2d, Color.BLACK)
            } else {
                fillBackground(g2d, images.peek().getBackgroundColor())
                for (image in images) {
                    paintImage(g2d, image.readImage())
                }
            }
            g2d.dispose()
            image = img
            repaint()
        }
    }

    /**
     * paints an image to the screen
     * @param g2d the graphics to draw onto
     * @param img the image to draw
     */
    private fun paintImage(g2d: Graphics2D, img: BufferedImage) {
        when (scaleMode) {
            Scale.FILL -> g2d.drawImage(
                img,
                0,
                0,
                DISPLAY_SIZE.width,
                DISPLAY_SIZE.height,
                null
            )
            Scale.REAL_SIZE -> g2d.drawImage(
                img,
                (DISPLAY_SIZE.width - img.width) / 2,
                (DISPLAY_SIZE.height - img.height) / 2,
                img.width,
                img.height,
                null
            )
            Scale.UP_SCALE -> {
                val imageScale = min(
                    DISPLAY_SIZE.getWidth() / img.width,
                    DISPLAY_SIZE.getHeight() / img.height
                )
                val imageSize = Dimension(
                    (img.width * imageScale).roundToInt(),
                    (img.height * imageScale).roundToInt()
                )
                g2d.drawImage(
                    img,
                    (DISPLAY_SIZE.width - imageSize.width) / 2,
                    (DISPLAY_SIZE.height - imageSize.height) / 2,
                    imageSize.width,
                    imageSize.height,
                    null
                )
            }
        }
    }

    /**
     * fills the whole background with a color
     * @param g2d the graphics to draw to
     * @param c the color to draw
     */
    private fun fillBackground(g2d: Graphics2D, c: Color) {
        g2d.color = c
        g2d.fillRect(0, 0, DISPLAY_SIZE.width, DISPLAY_SIZE.height)
    }

    /**
     * draws an image to the graphics with the given position and dimensions
     * @param g2d the graphics to draw to
     * @param img the image to draw
     */
    private fun drawImage(g2d: Graphics2D, img: BufferedImage) {
        if (flip) {
            val oldAT = g2d.transform
            g2d.transform = AffineTransform().apply {
                rotate(Math.PI, width / 2.toDouble(), height / 2.toDouble())
            }
            g2d.drawImage(img, 0, 0, null)
            g2d.transform = oldAT
        } else {
            g2d.drawImage(img, 0, 0, null)
        }
    }

    override fun paint(g: Graphics) {
        super.paint(g)
        val g2d = g as Graphics2D
        image?.let { drawImage(g2d, it) }
        paintMouse(g2d)
        g2d.dispose()
    }

    @Synchronized
    override fun setMainDisplay(b: Boolean) {
        if (b) {
            compileImage()
        } else {
            image = null
        }
    }
}