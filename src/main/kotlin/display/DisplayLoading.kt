package display

import main.Mode
import util.*
import util.Settings.DISPLAY_SIZE
import java.awt.AlphaComposite
import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.io.File
import java.lang.Long.max
import java.util.*
import javax.imageio.ImageIO

/**
 * `JPanel` for displaying Loading Utility
 * @param window callback to `DisplayWindow`
 * @author McAJBen@gmail.com
 * @since 1.0
 */
class DisplayLoading(window: DisplayWindow) : Display(window) {

    companion object {

        private const val serialVersionUID = -4364176757863161776L

        /**
         * number of milliseconds between repaints.
         * 20ms = ~50fps
         */
        private const val FRAME_TIME_MS = 20L

        /**
         * the number of milliseconds while the images are changing
         */
        private const val FADE_IN_MS = 1000L
    }

    /**
     * the total number of ms to display the image
     */
    private var totalWait = 8000L

    /**
     * a list of the cubes in the `DisplayLoading`
     */
    private val cubes = LinkedList<Cube>()

    /**
     * a list of the file names that haven't been shown this loop
     */
    private val fileNames = LinkedList<File>()

    /**
     * the previous image that is fading out
     */
    private var oldImage: BufferedImage? = null

    /**
     * the current image being displayed
     */
    private var currentImage: BufferedImage? = null

    /**
     * the thread that is repainting the `DisplayLoading`
     * and calculating motions and keeping track of time
     */
    private var paintThread = Thread()

    /**
     * tells if this is being shown and if we should be keeping track of time
     */
    private var mainDisplay = false

    /**
     * tells if the images should be up scaled
     */
    private var upScale = false

    /**
     * the next time that image has to be changed
     */
    private var nextSwapTime = 0L

    /**
     * the next time that image has to be changed
     */
    private var lastSwapTime = 0L

    init {
        loadNextImage()
        isVisible = true
    }

    /**
     * changes the amount of seconds between loading images
     * @param seconds the number of seconds between loading images
     */
    fun setTotalWait(seconds: Int) {
        totalWait = 1000L * seconds
    }

    /**
     * changes if the images are up scaled or not
     * @param b
     * - true if the image is scaled up
     * - false if the image is real size
     */
    fun setUpScale(b: Boolean) {
        upScale = b
        repaint()
    }

    /**
     * creates a cube and adds it to the list of displaying cubes
     */
    fun addCube() {
        cubes.add(Cube())
        repaint()
    }

    /**
     * removes all cubes
     */
    fun clearCubes() {
        synchronized(cubes) {
            cubes.clear()
        }
    }

    /**
     * changes images and loads a new one
     */
    private fun loadNextImage() {
        if (fileNames.isEmpty()) {
            rePop()
        }
        if (!fileNames.isEmpty()) {

            oldImage = currentImage
            currentImage = try {
                ImageIO.read(fileNames.removeFirst())
            } catch (e: Exception) {
                Log.error(Labels.CANNOT_LOAD_IMAGE, e)
                null
            }

            lastSwapTime = System.currentTimeMillis()
            nextSwapTime = lastSwapTime + totalWait + FADE_IN_MS
        }
    }

    /**
     * re loads the list of images in the loading folder
     */
    private fun rePop() {
        val folder = Settings.getFolder(Mode.LOADING)

        if (folder.exists()) {
            fileNames.addAll(
                folder.listFilesInOrder().filter { it.hasImageExtension() }.shuffled()
            )
        }
    }

    /**
     * restarts the screen when it starts displaying again or is disabled
     */
    private fun restart() {
        paintThread.interrupt()
        try {
            paintThread.join()
        } catch (e1: InterruptedException) {
            e1.printStackTrace()
        }
        paintThread = object : Thread("paintThread") {
            override fun run() {
                while (mainDisplay) {
                    try {
                        val nextFrame = System.currentTimeMillis() + FRAME_TIME_MS
                        repaint()

                        if (nextSwapTime < System.currentTimeMillis()) {
                            loadNextImage()
                        }
                        for (c in cubes) {
                            c.move()
                        }

                        sleep(max(0, nextFrame - System.currentTimeMillis()))
                    } catch (e: InterruptedException) {
                        break
                    }
                }
            }
        }
        paintThread.start()
    }

    /**
     * draws the given image to the graphics dependant on settings
     * @param g2d graphics to draw to
     * @param img image to draw
     */
    private fun drawImage(g2d: Graphics2D, img: BufferedImage?) {
        if (img == null) return
        if (upScale) {
            g2d.drawImage(
                img,
                0,
                0,
                DISPLAY_SIZE.width,
                DISPLAY_SIZE.height,
                null
            )
        } else {
            g2d.drawImage(
                img,
                (DISPLAY_SIZE.width - img.width) / 2,
                (DISPLAY_SIZE.height - img.height) / 2,
                null
            )
        }
    }

    override fun paint(g: Graphics) {
        super.paint(g)
        val g2d = g as Graphics2D

        g2d.color = Color(currentImage?.getRGB(0, 0) ?: Colors.BLACK.rgb)
        g2d.fillRect(0, 0, DISPLAY_SIZE.width, DISPLAY_SIZE.height)

        drawImage(g2d, currentImage)

        val cycleTime = System.currentTimeMillis() - lastSwapTime
        if (cycleTime < FADE_IN_MS) {
            val fade = 1 - cycleTime / FADE_IN_MS.toFloat()
            g2d.composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, fade)
            drawImage(g2d, oldImage)
        }

        g2d.composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f)
        for (c in cubes) {
            c.paint(g2d)
        }
        paintMouse(g2d)
        g2d.dispose()
    }

    override fun setMainDisplay(b: Boolean) {
        if (b) {
            restart()
            repaint()
        }
        mainDisplay = b
    }
}