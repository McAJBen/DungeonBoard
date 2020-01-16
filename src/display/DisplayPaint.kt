package display

import control.ControlPaintListener
import util.Settings
import java.awt.*
import java.awt.image.BufferedImage

/**
 * `JPanel` for displaying Paint Utility
 * @param window callback to `DisplayWindow`
 * @author McAJBen@gmail.com
 * @since 1.0
 */
class DisplayPaint(window: DisplayWindow) : Display(window), ControlPaintListener {

    companion object {
        private const val serialVersionUID = -8389531693546434519L
    }

    /**
     * the image used as a mask for the player's display
     * each pixel should be either Color.BLACK or transparent
     */
    private var mask: BufferedImage? = null

    /**
     * the zoomed size of the image
     */
    private var imageSize: Dimension? = null

    /**
     * the top left corner of the window
     * the negative of this will be the position to start drawing
     */
    private var windowPos = Point(0, 0)

    /**
     * the zoom scale of the image
     * - larger means zoomed out and a smaller image
     * - smaller means zoomed in and a larger image
     */
    private var scale = 1.0

    init {
        isVisible = true
    }

    override fun paint(g: Graphics) {
        super.paint(g)
        val g2d = g as Graphics2D
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)
        g2d.color = Color.BLACK
        g2d.fillRect(0, 0, Settings.DISPLAY_SIZE!!.width, Settings.DISPLAY_SIZE!!.height)
        if (Settings.PAINT_IMAGE != null && mask != null && imageSize != null) {
            g2d.drawImage(Settings.PAINT_IMAGE, -windowPos.x, -windowPos.y, imageSize!!.width, imageSize!!.height, null)
            g2d.drawImage(mask, -windowPos.x, -windowPos.y, imageSize!!.width, imageSize!!.height, null)
        }
        paintMouse(g2d)
        g2d.dispose()
    }

    override fun setMask(mask: BufferedImage) {
        this.mask = mask
        repaint()
    }

    override fun setImageSize() {
        imageSize = Dimension(
            (Settings.PAINT_IMAGE!!.width / scale).toInt(),
            (Settings.PAINT_IMAGE!!.height / scale).toInt()
        )
    }

    override fun setMainDisplay(b: Boolean) {
        if (b) {
            repaint()
        }
    }

    override fun setWindow(scale: Double, windowPos: Point) {
        this.scale = scale
        if (Settings.PAINT_IMAGE != null) {
            setImageSize()
            this.windowPos = windowPos
            if (imageSize!!.width < Settings.DISPLAY_SIZE!!.width) {
                this.windowPos.x = (imageSize!!.width - Settings.DISPLAY_SIZE!!.width) / 2
            }
            if (imageSize!!.height < Settings.DISPLAY_SIZE!!.height) {
                this.windowPos.y = (imageSize!!.height - Settings.DISPLAY_SIZE!!.height) / 2
            }
        }
        repaint()
    }

    override fun setWindowPos(windowPos: Point) {
        this.windowPos = windowPos
        if (imageSize != null) {
            if (imageSize!!.width < size.width) {
                this.windowPos.x = (imageSize!!.width - size.width) / 2
            }
            if (imageSize!!.height < size.height) {
                this.windowPos.y = (imageSize!!.height - size.height) / 2
            }
        }
        repaint()
    }
}