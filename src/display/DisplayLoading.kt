package display

import main.Mode
import main.Settings
import java.awt.AlphaComposite
import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.io.File
import java.util.*
import javax.imageio.ImageIO

/**
 * `JPanel` for displaying Loading Utility
 * @author McAJBen@gmail.com
 * @since 1.0
 */
class DisplayLoading : Display() {
    /**
     * the total number of ticks to display the image
     */
    private var totalWait = 400
    /**
     * a list of the cubes in the `DisplayLoading`
     */
    private val cubePositions: LinkedList<Cube>
    /**
     * a list of the file names that haven't been shown this loop
     */
    private val fileNames: LinkedList<String>
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
    private var paintThread: Thread
    /**
     * tells if this is being shown and if we should be keeping track of time
     */
    private var mainDisplay = false
    /**
     * tells if the images should be up scaled
     */
    private var upScale: Boolean
    /**
     * the count of how many ticks since the image has been changed
     */
    private var timer: Short
    /**
     * the alpha part of how faded the images are
     */
    private var fade: Float

    override fun paint(g: Graphics) {
        super.paint(g)
        val g2d = g as Graphics2D
        if (currentImage != null) {
            if (upScale) {
                if (timer <= FADE_IN) {
                    g2d.drawImage(oldImage, 0, 0, Settings.DISPLAY_SIZE!!.width, Settings.DISPLAY_SIZE!!.height, null)
                }
                g2d.composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, fade)
                g2d.drawImage(currentImage, 0, 0, Settings.DISPLAY_SIZE!!.width, Settings.DISPLAY_SIZE!!.height, null)
            } else {
                g2d.color = Color(currentImage!!.getRGB(0, 0))
                g2d.fillRect(0, 0, Settings.DISPLAY_SIZE!!.width, Settings.DISPLAY_SIZE!!.height)
                if (timer <= FADE_IN && oldImage != null) {
                    g2d.composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1 - fade)
                    g2d.drawImage(
                        oldImage, (Settings.DISPLAY_SIZE!!.width - oldImage!!.width) / 2,
                        (Settings.DISPLAY_SIZE!!.height - oldImage!!.height) / 2, null
                    )
                }
                g2d.composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, fade)
                g2d.drawImage(
                    currentImage, (Settings.DISPLAY_SIZE!!.width - currentImage!!.width) / 2,
                    (Settings.DISPLAY_SIZE!!.height - currentImage!!.height) / 2, null
                )
            }
        }
        g2d.composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f)
        for (c in cubePositions) {
            c.paint(g2d)
        }
        paintMouse(g2d)
        g2d.dispose()
    }

    override fun setMainDisplay(b: Boolean) {
        if (b) {
            restart(false)
            repaint()
        }
        mainDisplay = b
    }

    /**
     * changes the amount of seconds between loading images
     * @param seconds the number of seconds between loading images
     */
    fun setTotalWait(seconds: Int) {
        totalWait = seconds * 20
    }

    /**
     * changes if the images are up scaled or not
     * @param b <br></br>
     * - true if the image is scaled up<br></br>
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
        cubePositions.add(Cube())
        repaint()
    }

    /**
     * removes all cubes
     */
    fun clearCubes() {
        synchronized(cubePositions) { cubePositions.clear() }
    }

    /**
     * progresses one tick forward
     */
    private fun motion() {
        timer++
        repaint()
        if (timer <= FADE_IN) {
            fade = timer.toFloat() / FADE_IN
        } else if (timer > totalWait) {
            timer = 0
            image
        }
        for (c in cubePositions) {
            c.move()
        }
    }

    /**
     * changes images and loads a new one
     */
    private val image: Unit
        get() {
            if (fileNames.isEmpty()) {
                rePop()
            }
            if (!fileNames.isEmpty()) {
                oldImage = currentImage
                val file =
                    Settings.FOLDERS[Mode.LOADING.ordinal].toString() + "/" + fileNames.removeFirst()
                try {
                    currentImage = ImageIO.read(File(file))
                } catch (e: Exception) {
                    currentImage = null
                    e.printStackTrace()
                }
            }
        }

    /**
     * re loads the list of images in the loading folder
     */
    private fun rePop() {
        val folder = Settings.FOLDERS[Mode.LOADING.ordinal]
        if (folder.exists()) {
            val rand = Random()
            for (f in folder.listFiles()) {
                val name = f.name
                val suffix = name.substring(name.lastIndexOf('.') + 1)
                if (suffix.equals("PNG", ignoreCase = true) || suffix.equals("JPG", ignoreCase = true) || suffix.equals(
                        "JPEG",
                        ignoreCase = true
                    )
                ) {
                    val index = rand.nextInt(fileNames.size + 1)
                    if (index == fileNames.size) {
                        fileNames.add(name)
                    } else {
                        fileNames.add(index, name)
                    }
                }
            }
        }
    }

    /**
     * restarts the screen when it starts displaying again or is disabled
     * @param changeImage whether the image should be changed first or not
     */
    private fun restart(changeImage: Boolean) {
        paintThread.interrupt()
        try {
            paintThread.join()
        } catch (e1: InterruptedException) {
            e1.printStackTrace()
        }
        if (changeImage) {
            image
        }
        paintThread = object : Thread("paintThread") {
            override fun run() {
                while (mainDisplay) {
                    try {
                        motion()
                        sleep(50)
                    } catch (e: InterruptedException) {
                        break
                    }
                }
            }
        }
        paintThread.start()
    }

    companion object {
        private const val serialVersionUID = -4364176757863161776L
        /**
         * the number of ticks while the images are changing
         * 50ms tick time makes 20 ticks per second
         */
        private const val FADE_IN = 20
    }

    /**
     * creates a instance of `DisplayLoading`
     */
    init {
        cubePositions = LinkedList()
        paintThread = Thread()
        fileNames = LinkedList()
        upScale = false
        timer = 20
        fade = 1f
        image
        isVisible = true
    }
}