package display

import main.Mode
import util.Settings
import util.Settings.DISPLAY_SIZE
import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.geom.AffineTransform
import java.awt.image.BufferedImage
import java.util.*

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
    private val images: LinkedList<AlphaImage> = LinkedList()

    /**
     * the image being displayed by `DisplayPictures`
     */
    private var image: BufferedImage? = null

    /**
     * the method of scale to show the image
     */
    private var scaleMode = Scale.UP_SCALE

    /**
     * the thread that is in charge of repainting `image`
     */
    private var compileThread: Thread? = null

    /**
     * tells if the image should be flipped
     * - true will rotate the image by 180 degrees before displaying
     * - false will display normally
     */
    private var flip = false

    init {
        image = BufferedImage(
            DISPLAY_SIZE.width,
            DISPLAY_SIZE.height,
            BufferedImage.TYPE_INT_ARGB
        )
        isVisible = true
    }

    /**
     * paints an image to the screen
     * @param g2d the graphics to draw onto
     * @param img the image to draw
     */
    fun paintImage(g2d: Graphics2D, img: BufferedImage) {
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
                val screenRatio = DISPLAY_SIZE.getWidth() / DISPLAY_SIZE.getHeight()
                val imageRatio = img.width.toDouble() / img.height
                val imageScale = if (imageRatio > screenRatio) { // width > height
                    Dimension(DISPLAY_SIZE.width, (DISPLAY_SIZE.width / imageRatio).toInt())
                } else { // width < height
                    Dimension((DISPLAY_SIZE.height * imageRatio).toInt(), DISPLAY_SIZE.height)
                }
                g2d.drawImage(
                    img,
                    (DISPLAY_SIZE.width - imageScale.width) / 2,
                    (DISPLAY_SIZE.height - imageScale.height) / 2,
                    imageScale.width,
                    imageScale.height,
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
            val at = AffineTransform()
            at.rotate(Math.PI, width / 2.toDouble(), height / 2.toDouble())
            g2d.transform = at
            g2d.drawImage(img, 0, 0, null)
            g2d.transform = oldAT
        } else {
            g2d.drawImage(img, 0, 0, null)
        }
    }

    /**
     * adds an image to be drawn on the panel
     * @param name the name of the file to load an image from
     */
    fun addImage(name: String) {
        val ai = AlphaImage(mode, name)
        stopCompile()
        images.add(ai)
        compileImage()
        repaint()
    }

    /**
     * stops painting to image so another thread can be made
     */
    private fun stopCompile() {
        if (compileThread != null && compileThread!!.isAlive) {
            compileThread!!.interrupt()
        }
    }

    /**
     * turns the `AlphaImages` into a single image to be displayed
     */
    private fun compileImage() {
        compileThread = object : Thread("compileImage") {
            override fun run() {
                val img = BufferedImage(
                    DISPLAY_SIZE.width,
                    DISPLAY_SIZE.height,
                    BufferedImage.TYPE_INT_ARGB
                )
                val g2d = img.createGraphics()
                if (images.size == 0) {
                    fillBackground(g2d, Color.BLACK)
                } else {
                    fillBackground(g2d, images.first.bGColor)
                    try {
                        for (image in images) {
                            paintImage(g2d, image.image!!)
                        }
                    } catch (e: NullPointerException) {
                        return
                    } catch (e: ConcurrentModificationException) {
                        return
                    }
                }
                g2d.dispose()
                if (isInterrupted) {
                    return
                }
                image = img
                repaint()
            }
        }
        compileThread!!.start()
    }

    /**
     * removes an image by the name of the file
     * @param name the name of the file that was used to load the image from
     */
    fun removeImage(name: String) {
        stopCompile()
        images.removeIf { it.name == name }
        compileImage()
        repaint()
    }

    /**
     * changes the scale mode
     * @param selectedItem the index of the new scale mode
     */
    fun setScaleMode(selectedItem: Scale) {
        stopCompile()
        scaleMode = selectedItem
        compileImage()
        repaint()
    }

    /**
     * removes all images from the screen
     */
    fun removeAllImages() {
        stopCompile()
        images.clear()
        compileImage()
        repaint()
    }

    /**
     * toggles the flip of the images. Rotates them by 180 degrees
     */
    fun flip() {
        flip = !flip
        repaint()
    }

    override fun paint(g: Graphics) {
        super.paint(g)
        val g2d = g as Graphics2D
        drawImage(g2d, image!!)
        paintMouse(g2d)
        g2d.dispose()
    }

    @Synchronized
    override fun setMainDisplay(b: Boolean) {
        if (b) {
            stopCompile()
            compileImage()
        } else {
            image = null
        }
    }
}