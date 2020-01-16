package paint

import util.*
import java.awt.*
import java.awt.event.*
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import java.lang.Integer.min
import javax.imageio.ImageIO
import javax.swing.JComponent
import kotlin.math.*

/**
 * Similar to `PicturePanel` this is used by the `ControlPaint` to handle user inputs
 * This is also the area that the user draws to update the `DisplayPaint`
 * @param listener callback for `ControlPaint`
 * @author McAJBen@gmail.com
 * @since 1.0
 */
class DrawPanel(
    private val listener: DrawPanelListener
) : JComponent(), MouseListener, MouseMotionListener, ComponentListener {

    companion object {
        private const val serialVersionUID = -3142625453462827948L
    }

    /**
     * the radius of the users Pen. Or in the case of a square, half the width
     */
    private var radius = 0

    /**
     * the diameter of the users Pen. Or in the case of a square this will be the width
     */
    private var diameter = 0

    /**
     * the drawing utensil in use (CIRCLE, SQUARE)
     */
    private var penType = Pen.CIRCLE

    /**
     * the actual `BufferedImage` that is being drawn onto and the mask is created from
     */
    private var drawingLayer: BufferedImage? = null

    /**
     * the `Graphics2D` for `drawingLayer`
     */
    private var g2: Graphics2D? = null

    /**
     * the size of the `DrawPanel`, used to find the size that the image needs to be drawn to
     */
    private var controlSize: Dimension? = null

    /**
     * the zoom of the `DisplayPaint` for the players to see
     */
    private var displayZoom = 1.0

    /**
     * the position of the previous click inside the `drawingLayer`
     */
    private lateinit var lastP: Point

    /**
     * the current position of the mouse inside of (@code drawingLayer}
     */
    private var mousePos = Point(Int.MIN_VALUE, Int.MIN_VALUE)

    /**
     * true if the pen is drawing, false if you ignore it
     */
    private var canDraw = false

    /**
     * true if an image is being loaded and the `drawingLayer` should not be displayed
     */
    private var loading = false

    /**
     * true if the mouse has yet to be released
     */
    private var dragging = false

    /**
     * the state of the vertical or horizontal lock on the pen
     */
    private var styleLock: Direction = Direction.NONE

    /**
     * the state of the pen used for touch pads
     */
    var drawMode: DrawMode = DrawMode.ANY
        private set

    /**
     * the position of the previous click that changed the window position
     */
    private var lastWindowClick = Point(0, 0)

    /**
     * the position of the actual window on the `drawingLayer`
     */
    private val windowPos = Point(0, 0)

    /**
     * the position of the start of a click
     */
    private var startOfClick: Point? = null

    /**
     * the `JButton` that causes the mask to be displayed to the players.
     * It only becomes enabled when the mask has been changed
     */
    val updateButton = createButton(Labels.UPDATE_SCREEN).apply {
        addActionListener {
            if (hasImage()) {
                try {
                    listener.setMask(mask)
                } catch (error: OutOfMemoryError) {
                    Log.error(Labels.CANNOT_UPDATE_IMAGE, error)
                }
                isEnabled = false
                background = Colors.CONTROL_BACKGROUND
            }
        }
    }

    init {
        isDoubleBuffered = false

        setRadius(25)
        addMouseListener(this)
        addMouseMotionListener(this)
        addComponentListener(this)
        repaint()
    }

    /**
     * sets the `displayZoom`, moves the display window and changes the window position based on edges
     * @param zoom the number of pixels in the image per the pixel on the output display.
     * - a higher number will zoom out
     * - a lower number will zoom in
     */
    fun setZoom(zoom: Double) {
        displayZoom = zoom
        setWindowPos(lastWindowClick)
        listener.setWindow(zoom, getWindowPos())
        repaint()
    }

    /**
     * sets the zoom and position of the players view
     * @param zoom the number of pixels in the image per pixel on the output display
     * @param p the point of the click (middle point of window)
     */
    fun setWindow(zoom: Double, p: Point) {
        displayZoom = zoom
        setWindowPos(p)
        listener.setWindow(zoom, getWindowPos())
        repaint()
    }

    /**
     * creates the `drawingLayer` based on `Settings.PAINT_IMAGE`.
     * Also sets `g2` and clears everything for a new image.
     */
    @Synchronized
    fun setImage() {
        if (Settings.PAINT_IMAGE != null) {
            val maskFile = Settings.fileToMaskFile(Settings.PAINT_FOLDER!!)
            if (maskFile.exists() && maskFile.lastModified() > Settings.PAINT_FOLDER!!.lastModified()) {
                try {
                    drawingLayer = ImageIO.read(maskFile)
                    g2 = drawingLayer!!.graphics as Graphics2D
                    g2!!.composite = AlphaComposite.getInstance(AlphaComposite.SRC, 0.6f)
                    g2!!.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED)
                } catch (e: IOException) {
                    Log.error("Cannot load Mask, file is probably too large", e)
                }
            } else {
                drawingLayer = BufferedImage(
                    Settings.PAINT_IMAGE!!.width / Settings.PIXELS_PER_MASK,
                    Settings.PAINT_IMAGE!!.height / Settings.PIXELS_PER_MASK,
                    BufferedImage.TYPE_INT_ARGB
                )
                g2 = drawingLayer!!.graphics as Graphics2D
                g2!!.composite = AlphaComposite.getInstance(AlphaComposite.SRC, 0.6f)
                g2!!.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED)
                hideAll()
            }
        }
    }

    /**
     * changes the size of the user's pen
     * @param value the radius of the pen
     */
    fun setRadius(value: Int) {
        radius = value
        diameter = radius * 2
        repaint()
    }

    /**
     * sets the paint image to null, and removes any settings for it
     */
    fun resetImage() {
        Settings.PAINT_IMAGE = null
        Settings.PAINT_CONTROL_IMAGE = null
        g2 = null
        drawingLayer = null
        loading = false
    }

    /**
     * toggles the pen from Circle to Square or vice versa
     */
    fun togglePen() {
        penType = Pen.values()[(penType.ordinal + 1) % Pen.values().size]
        repaint()
    }

    /**
     * toggles the style of vertical or horizontal lock
     */
    fun toggleStyle() {
        styleLock = Direction.values()[(styleLock.ordinal + 1) % Direction.values().size]
    }

    /**
     * toggles the draw mode for touch pads
     */
    fun toggleDrawMode() {
        drawMode = DrawMode.values()[(drawMode.ordinal + 1) % DrawMode.values().size]
        if (g2 != null) {
            when (drawMode) {
                DrawMode.ANY -> {
                }
                DrawMode.VISIBLE -> {
                    g2!!.paint = Colors.CLEAR
                    canDraw = true
                }
                DrawMode.INVISIBLE -> {
                    g2!!.paint = Colors.OPAQUE
                    canDraw = true
                }
                DrawMode.WINDOW -> canDraw = false
            }
        }
    }

    /**
     * sets if the `DrawPanel` is loading right now or not
     * @param b 
     * - true if loading
     * - false if done loading
     */
    fun setImageLoading(b: Boolean) {
        loading = b
        repaint()
    }

    /**
     * gets the `penType` either Circle, or Square
     * @return
     */
    val pen: Int
        get() = penType.ordinal

    /**
     * gets the `styleLock` either vertical, horizontal, or none
     * @return
     */
    val style: Int
        get() = styleLock.ordinal

    /**
     * gets a black and transparent mask from the `drawingLayer`
     * @return `BufferedImage` of type `TYPE_INT_ARGB`
     * with every pixel either being Color.BLACK or completely transparent.
     * @throws OutOfMemoryError if the JVM runs out of memory
     */
    @get:Throws(OutOfMemoryError::class)
    val mask: BufferedImage
        get() {
            val mask = BufferedImage(
                drawingLayer!!.width,
                drawingLayer!!.height,
                BufferedImage.TYPE_INT_ARGB
            )
            for (i in 0 until drawingLayer!!.width) {
                for (j in 0 until drawingLayer!!.height) {
                    val dl = drawingLayer!!.getRGB(i, j)
                    if (dl == -1721434268) { // CLEAR
                        mask.setRGB(i, j, 0)
                    } else if (dl == -1711315868) { // OPAQUE
                        mask.setRGB(i, j, -16777215)
                    }
                }
            }
            return mask
        }

    /**
     * gets the window position based on `displayZoom`
     * @return the position of the window by the top left corner of it in the `drawingLayer`
     */
    private fun getWindowPos(): Point {
        return Point((windowPos.x / displayZoom).toInt(), (windowPos.y / displayZoom).toInt())
    }

    /**
     * tells if the `DrawPanel` currently has an image
     * @return - true if there is an image
     * - false if there is not an image
     */
    private fun hasImage(): Boolean {
        return drawingLayer != null
    }

    /**
     * Draws a rectangle on the area of a `DrawPanel` to tell what the players can see
     * @param g2d the graphics component to draw to
     */
    private fun drawPlayerView(g2d: Graphics2D) {
        val w =
            (Settings.DISPLAY_SIZE!!.width * displayZoom * controlSize!!.width / Settings.PAINT_IMAGE!!.width).toInt()
        val h =
            (Settings.DISPLAY_SIZE!!.height * displayZoom * controlSize!!.height / Settings.PAINT_IMAGE!!.height).toInt()
        val x: Int
        val y: Int
        x = if (w > controlSize!!.width) {
            -(w - controlSize!!.width) / 2
        } else {
            windowPos.x * controlSize!!.width / Settings.PAINT_IMAGE!!.width
        }
        y = if (h > controlSize!!.height) {
            -(h - controlSize!!.height) / 2
        } else {
            windowPos.y * controlSize!!.height / Settings.PAINT_IMAGE!!.height
        }
        g2d.drawRect(x, y, w, h)
        g2d.drawLine(x, y, x + w, y + h)
        g2d.drawLine(x + w, y, x, y + h)
        g2d.color = Colors.PINK_CLEAR
        g2d.fillRect(x, y, w, h)
    }

    /**
     * converts a point on the `drawingLayer` to a point on the actual image
     * @param p a point based on the placement in `drawingLayer`
     * @return a point based on the placement in `Settings.PAINT_IMAGE`
     */
    private fun toDrawingPoint(p: Point?): Point {
        return Point(
            p!!.x * drawingLayer!!.width / controlSize!!.width,
            p.y * drawingLayer!!.height / controlSize!!.height
        )
    }

    /**
     * sets the position of the window on `drawingLayer`
     * @param p the point of the click (middle point of window)
     */
    private fun setWindowPos(p: Point) {
        lastWindowClick = p
        windowPos.x = (p.x * Settings.PIXELS_PER_MASK - Settings.DISPLAY_SIZE!!.width * displayZoom / 2).toInt()
        windowPos.y = (p.y * Settings.PIXELS_PER_MASK - Settings.DISPLAY_SIZE!!.height * displayZoom / 2).toInt()
        if (Settings.PAINT_IMAGE != null) {
            if (windowPos.x > Settings.PAINT_IMAGE!!.width - Settings.DISPLAY_SIZE!!.width * displayZoom) {
                windowPos.x = (Settings.PAINT_IMAGE!!.width - Settings.DISPLAY_SIZE!!.width * displayZoom).toInt()
            }
            if (windowPos.x < 0) {
                windowPos.x = 0
            }
            if (windowPos.y > Settings.PAINT_IMAGE!!.height - Settings.DISPLAY_SIZE!!.height * displayZoom) {
                windowPos.y = (Settings.PAINT_IMAGE!!.height - Settings.DISPLAY_SIZE!!.height * displayZoom).toInt()
            }
            if (windowPos.y < 0) {
                windowPos.y = 0
            }
        }
    }

    /**
     * uses the pen to draw onto the `drawingLayer`
     * @param newP a point based on the placement on `Settings.PAINT_IMAGE`
     * use `toDrawingPoint` to convert to the correct point
     */
    private fun addPoint(newP: Point) {
        if (g2 != null) {
            when (styleLock) {
                Direction.HORIZONTAL -> newP.y = lastP.y
                Direction.VERTICAL -> newP.x = lastP.x
                else -> {}
            }
            val widthMod = drawingLayer!!.width.toDouble() / controlSize!!.width
            val heightMod = drawingLayer!!.height.toDouble() / controlSize!!.height
            val radiusWidth = radius * widthMod
            val radiusHeight = radius * heightMod
            val diameterWidth = (diameter * widthMod).toInt()
            val diameterHeight = (diameter * heightMod).toInt()
            when (penType) {
                Pen.CIRCLE -> {
                    g2!!.fillPolygon(getCirclePolygon(newP, lastP, radiusWidth, radiusHeight))
                    g2!!.fillOval(
                        newP.x - radiusWidth.toInt(),
                        newP.y - radiusHeight.toInt(),
                        diameterWidth,
                        diameterHeight
                    )
                }
                Pen.SQUARE -> {
                    g2!!.fillPolygon(getSquarePolygon(newP, lastP, radiusWidth.toInt(), radiusHeight.toInt()))
                    g2!!.fillRect(
                        newP.x - radiusWidth.toInt(),
                        newP.y - radiusHeight.toInt(),
                        diameterWidth,
                        diameterHeight
                    )
                }
                Pen.RECT -> {
                }
            }
            lastP = newP
            updateButton.isEnabled = true
            updateButton.background = Colors.ACTIVE
        }
    }

    /**
     * returns a polygon for a rectangle connecting two circles
     * @param newP the center of one circle
     * @param oldP the center of another circle
     * points based on the placement on `Settings.PAINT_IMAGE`
     * use `toDrawingPoint` to convert to the correct point
     * @param radiusWidth the radius of the circle in the x direction
     * @param radiusHeight the radius of the circle in the y direction
     * @return a `Polygon` with 4 points
     */
    private fun getCirclePolygon(
        newP: Point?,
        oldP: Point?,
        radiusWidth: Double,
        radiusHeight: Double
    ): Polygon {
        val angle = -atan2(newP!!.getY() - oldP!!.getY(), newP.getX() - oldP.getX())
        val anglePos = angle + Math.PI / 2
        val angleNeg = angle - Math.PI / 2
        val cosP = (cos(anglePos) * radiusWidth).toInt()
        val cosN = (cos(angleNeg) * radiusWidth).toInt()
        val sinP = (sin(anglePos) * radiusHeight).toInt()
        val sinN = (sin(angleNeg) * radiusHeight).toInt()
        return Polygon(
            intArrayOf(
                newP.x + cosP,
                newP.x + cosN,
                oldP.x + cosN,
                oldP.x + cosP
            ),
            intArrayOf(
                newP.y - sinP,
                newP.y - sinN,
                oldP.y - sinN,
                oldP.y - sinP
            ),
            4
        )
    }

    /**
     * returns a polygon for a rectangle connecting two squares
     * @param newP the center of one square
     * @param oldP the center of another square
     * points based on the placement on `Settings.PAINT_IMAGE`
     * use `toDrawingPoint` to convert to the correct point
     * @param radiusWidth the radius of the square in the x direction
     * @param radiusHeight the radius of the square in the y direction
     * @return a `Polygon` with 4 points
     */
    private fun getSquarePolygon(newP: Point, oldP: Point, radiusWidth: Int, radiusHeight: Int): Polygon {
        var newRadiusHeight = radiusHeight
        if (newP.x > oldP.x && newP.y > oldP.y || newP.x < oldP.x && newP.y < oldP.y) {
            newRadiusHeight *= -1
        }
        return Polygon(
            intArrayOf(
                newP.x - radiusWidth,
                newP.x + radiusWidth,
                oldP.x + radiusWidth,
                oldP.x - radiusWidth
            ),
            intArrayOf(
                newP.y - newRadiusHeight,
                newP.y + newRadiusHeight,
                oldP.y + newRadiusHeight,
                oldP.y - newRadiusHeight
            ),
            4
        )
    }

    /**
     * fills all of the `drawingLayer` with a color
     * @param c the color to paint with
     */
    private fun fillAll(c: Color?) {
        if (g2 != null) {
            g2!!.paint = c
            g2!!.fillRect(0, 0, drawingLayer!!.width, drawingLayer!!.height)
            repaint()
            updateButton.isEnabled = true
            updateButton.background = Colors.ACTIVE
        }
    }

    /**
     * sets all of `drawingLayer` to Opaque and unseen to players
     */
    fun hideAll() {
        fillAll(Colors.OPAQUE)
    }

    /**
     * sets all of `drawingLayer` to Clear and seen to players
     */
    fun showAll() {
        fillAll(Colors.CLEAR)
    }

    /**
     * saves the mask to file
     */
    fun saveMask() {
        if (Settings.PAINT_FOLDER != null) {
            val f = Settings.fileToMaskFile(Settings.PAINT_FOLDER!!)
            try {
                ImageIO.write(drawingLayer, "png", f)
                val dataFile = File(Settings.PAINT_MASK_FOLDER, "${f.name}.data")
                dataFile.writeText("$displayZoom ${lastWindowClick.x} ${lastWindowClick.y}")
            } catch (e: IOException) {
                Log.error("Cannot save Mask", e)
            }
        }
    }

    override fun paintComponent(g: Graphics) {
        val g2d = g as Graphics2D
        when {
            loading -> {
                g2d.drawString("Loading...", controlSize!!.width / 2, controlSize!!.height / 2)
            }
            Settings.PAINT_CONTROL_IMAGE != null -> {
                g2d.drawImage(Settings.PAINT_CONTROL_IMAGE, 0, 0, controlSize!!.width, controlSize!!.height, null)
                g2d.drawImage(drawingLayer, 0, 0, controlSize!!.width, controlSize!!.height, null)
                g2d.color = Colors.PINK
                when (penType) {
                    Pen.CIRCLE -> g2d.drawOval(mousePos.x - radius, mousePos.y - radius, diameter, diameter)
                    Pen.SQUARE -> g2d.drawRect(mousePos.x - radius, mousePos.y - radius, diameter, diameter)
                    Pen.RECT -> {
                        if (dragging) {
                            g2d.drawRect(
                                min(mousePos.x, startOfClick!!.x),
                                min(mousePos.y, startOfClick!!.y),
                                abs(mousePos.x - startOfClick!!.x),
                                abs(mousePos.y - startOfClick!!.y)
                            )
                        }
                        g2d.drawLine(mousePos.x, mousePos.y - 10, mousePos.x, mousePos.y + 10)
                        g2d.drawLine(mousePos.x - 10, mousePos.y, mousePos.x + 10, mousePos.y)
                    }
                }
                drawPlayerView(g2d)
            }
            controlSize != null -> {
                g2d.drawString("No image loaded", controlSize!!.width / 2, controlSize!!.height / 2)
            }
        }
    }

    override fun mousePressed(e: MouseEvent) {
        if (Settings.PAINT_IMAGE != null) {
            lastP = toDrawingPoint(e.point)
            when (drawMode) {
                DrawMode.ANY -> {
                    if (e.button == MouseEvent.BUTTON2) {
                        setWindowPos(lastP)
                        listener.setWindowPos(getWindowPos())
                        canDraw = false
                    } else {
                        if (e.button == MouseEvent.BUTTON1) {
                            g2!!.paint = Colors.CLEAR
                            canDraw = true
                        } else if (e.button == MouseEvent.BUTTON3) {
                            g2!!.paint = Colors.OPAQUE
                            canDraw = true
                        }
                        startOfClick = e.point
                        dragging = true
                        addPoint(lastP)
                    }
                }
                DrawMode.INVISIBLE, DrawMode.VISIBLE -> {
                    startOfClick = e.point
                    dragging = true
                    addPoint(lastP)
                }
                DrawMode.WINDOW -> {
                    setWindowPos(lastP)
                    listener.setWindowPos(getWindowPos())
                }
            }
            repaint()
        }
    }

    override fun mouseReleased(e: MouseEvent) {
        if (Settings.PAINT_IMAGE != null && canDraw) {
            when (penType) {
                Pen.RECT -> {
                    val p = toDrawingPoint(e.point)
                    val p2 = toDrawingPoint(startOfClick)
                    g2!!.fillRect(
                        min(p.x, p2.x),
                        min(p.y, p2.y),
                        abs(p.x - p2.x),
                        abs(p.y - p2.y)
                    )
                }
                else -> {}
            }
        }
        dragging = false
        repaint()
    }

    override fun mouseDragged(e: MouseEvent) {
        if (Settings.PAINT_IMAGE != null) {
            if (canDraw) {
                addPoint(toDrawingPoint(e.point))
            } else {
                setWindowPos(toDrawingPoint(e.point))
                listener.setWindowPos(getWindowPos())
            }
            mousePos = e.point
            repaint()
        }
    }

    override fun mouseMoved(e: MouseEvent) {
        mousePos = e.point
        repaint()
    }

    override fun componentResized(e: ComponentEvent) {
        controlSize = size
        repaint()
    }

    override fun mouseEntered(e: MouseEvent?) {}

    override fun mouseClicked(e: MouseEvent?) {}

    override fun mouseExited(e: MouseEvent?) {}

    override fun componentMoved(e: ComponentEvent) {}

    override fun componentHidden(e: ComponentEvent) {}

    override fun componentShown(e: ComponentEvent) {}
}