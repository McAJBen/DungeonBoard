package paint

import util.Colors
import util.Labels
import util.Resources
import util.Settings.DISPLAY_SIZE
import java.awt.*
import java.awt.event.*
import java.lang.Integer.min
import javax.swing.ImageIcon
import javax.swing.JComponent
import kotlin.math.*

/**
 * Similar to `PicturePanel` this is used by the `ControlPaint` to handle user inputs
 * This is also the area that the user draws to update the `DisplayPaint`
 * @param parent callback for `ControlPaint`
 * @author McAJBen@gmail.com
 * @since 1.0
 */
class DrawPanel(
    private val parent: DrawPanelListener
) : JComponent(), MouseListener, MouseMotionListener, ComponentListener {

    companion object {
        private const val serialVersionUID = -3142625453462827948L
    }

    /**
     * the radius of the users Pen. Or in the case of a square, half the width
     */
    private var radius = 0

    /**
     * the drawing utensil in use
     */
    private var penType = Pen.CIRCLE

    /**
     * the position of the previous click inside the `paintRef.controlMask`
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
    private var drawMode: DrawMode = DrawMode.ANY

    /**
     * the position of the start of a click
     */
    private var startOfClick: Point? = null

    init {
        isDoubleBuffered = false

        addMouseListener(this)
        addMouseMotionListener(this)
        addComponentListener(this)
        setRadius(25)
    }

    /**
     * changes the size of the user's pen
     * @param value the radius of the pen
     */
    fun setRadius(value: Int) {
        radius = value
        repaint()
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
        when (drawMode) {
            DrawMode.ANY -> {
            }
            DrawMode.VISIBLE -> {
                parent.paintRef?.run{
                    maskGraphics.paint = Colors.CLEAR
                }
                canDraw = true
            }
            DrawMode.INVISIBLE -> {
                parent.paintRef?.run{
                    maskGraphics.paint = Colors.OPAQUE
                }
                canDraw = true
            }
            DrawMode.WINDOW -> canDraw = false
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
     * gets the `penType` resource
     * @return the icon for the current `penType`
     */
    fun getPenResource(): ImageIcon {
        return Resources.PEN_TYPE[penType.ordinal]
    }

    /**
     * gets the `styleLock` resource
     * @return the icon for the current `style`
     */
    fun getStyleResource(): ImageIcon {
        return Resources.DRAW_STYLE[styleLock.ordinal]
    }

    /**
     * gets the `drawMode` resource
     * @return the icon for the current `drawMode`
     */
    fun getDrawModeResource(): ImageIcon {
        return Resources.DRAW_MODE[drawMode.ordinal]
    }

    /**
     * Draws a rectangle on the area of a `DrawPanel` to tell what the players can see
     * @param g2d the graphics component to draw to
     */
    private fun drawPlayerView(g2d: Graphics2D) {
        parent.paintRef?.run {
            val w = (DISPLAY_SIZE.width * displayZoom * size.width / displayImage.width).roundToInt()
            val h = (DISPLAY_SIZE.height * displayZoom * size.height / displayImage.height).roundToInt()
            val x = if (w > size.width) {
                -(w - size.width) / 2
            } else {
                windowOffset.x * size.width / displayImage.width
            }
            val y = if (h > size.height) {
                -(h - size.height) / 2
            } else {
                windowOffset.y * size.height / displayImage.height
            }
            g2d.drawRect(x, y, w, h)
            g2d.drawLine(x, y, x + w, y + h)
            g2d.drawLine(x + w, y, x, y + h)
            g2d.color = Colors.PINK_CLEAR
            g2d.fillRect(x, y, w, h)
        }
    }

    /**
     * converts a point on the `drawingLayer` to a point on the actual image
     * @param p a point based on the placement in `drawingLayer`
     * @return a point based on the placement in `paintRef.controlMask`
     */
    private fun toDrawingPoint(p: Point): Point {
        parent.paintRef?.run {
            return Point(
                p.x * controlMask.width / size.width,
                p.y * controlMask.height / size.height
            )
        }
        return Point(
            p.x,
            p.y
        )
    }

    /**
     * uses the pen to draw onto the `drawingLayer`
     * @param newP a point based on the placement on `paintRef.controlMask`
     * use `toDrawingPoint` to convert to the correct point
     */
    private fun addPoint(newP: Point) {
        parent.paintRef?.run {
            when (styleLock) {
                Direction.HORIZONTAL -> newP.y = lastP.y
                Direction.VERTICAL -> newP.x = lastP.x
                Direction.NONE -> {
                }
            }
            val widthMod = controlMask.width.toDouble() / size.width
            val heightMod = controlMask.height.toDouble() / size.height
            val radiusWidth = radius * widthMod
            val radiusHeight = radius * heightMod
            val diameterWidth = (2 * radius * widthMod).roundToInt()
            val diameterHeight = (2 * radius * heightMod).roundToInt()
            when (penType) {
                Pen.CIRCLE -> {
                    maskGraphics.fillPolygon(getCircleDragPolygon(newP, lastP, radiusWidth, radiusHeight))
                    maskGraphics.fillOval(
                        newP.x - radiusWidth.roundToInt(),
                        newP.y - radiusHeight.roundToInt(),
                        diameterWidth,
                        diameterHeight
                    )
                }
                Pen.SQUARE -> {
                    maskGraphics.fillPolygon(getSquareDragPolygon(newP, lastP, radiusWidth.roundToInt(), radiusHeight.roundToInt()))
                    maskGraphics.fillRect(
                        newP.x - radiusWidth.roundToInt(),
                        newP.y - radiusHeight.roundToInt(),
                        diameterWidth,
                        diameterHeight
                    )
                }
                Pen.HEX -> {
                    // TODO create the polygon for dragging
                    maskGraphics.fillPolygon(getHexPolygon(newP, radiusWidth.roundToInt(), radiusHeight.roundToInt()))
                }
                Pen.RECT -> {
                }
            }
            lastP = newP
            parent.maskHasChanged()
        }
    }

    /**
     * returns a polygon for a rectangle connecting two circles
     * @param newP the center of one circle
     * @param oldP the center of another circle
     * points based on the placement on `paintRef.controlMask`
     * use `toDrawingPoint` to convert to the correct point
     * @param radiusWidth the radius of the circle in the x direction
     * @param radiusHeight the radius of the circle in the y direction
     * @return a `Polygon` with 4 points
     */
    private fun getCircleDragPolygon(
        newP: Point?,
        oldP: Point?,
        radiusWidth: Double,
        radiusHeight: Double
    ): Polygon {
        val angle = -atan2(newP!!.getY() - oldP!!.getY(), newP.getX() - oldP.getX())
        val anglePos = angle + Math.PI / 2
        val angleNeg = angle - Math.PI / 2
        val cosP = (cos(anglePos) * radiusWidth).roundToInt()
        val cosN = (cos(angleNeg) * radiusWidth).roundToInt()
        val sinP = (sin(anglePos) * radiusHeight).roundToInt()
        val sinN = (sin(angleNeg) * radiusHeight).roundToInt()
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
     * points based on the placement on `paintRef.controlMask`
     * use `toDrawingPoint` to convert to the correct point
     * @param radiusWidth the radius of the square in the x direction
     * @param radiusHeight the radius of the square in the y direction
     * @return a `Polygon` with 4 points
     */
    private fun getSquareDragPolygon(newP: Point, oldP: Point, radiusWidth: Int, radiusHeight: Int): Polygon {
        val newRadiusHeight = if (newP.x > oldP.x && newP.y > oldP.y || newP.x < oldP.x && newP.y < oldP.y) {
            -radiusHeight
        } else {
            radiusHeight
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
     * returns a polygon for a hex cursor
     * @param center the center of one hex
     * points based on the placement on `paintRef.controlMask`
     * use `toDrawingPoint` to convert to the correct point
     * @param radiusWidth the radius of the hex in the x direction
     * @param radiusHeight the radius of the hex in the y direction
     * @return a `Polygon` with 6 points
     */
    private fun getHexPolygon(center: Point, radiusWidth: Int, radiusHeight: Int): Polygon {
        return Polygon(
            (0 until 6).map { center.x + (radiusWidth * cos(PI / 3 * it)).roundToInt() }.toIntArray(),
            (0 until 6).map { center.y + (radiusHeight * sin(PI / 3 * it)).roundToInt() }.toIntArray(),
            6
        )
    }

    /**
     * fills all of the `paintRef.controlMask` with a color
     * @param c the color to paint with
     */
    private fun fillAll(c: Color) {
        parent.paintRef?.run {
            maskGraphics.paint = c
            maskGraphics.fillRect(0, 0, controlMask.width, controlMask.height)
            repaint()
            parent.maskHasChanged()
        }
    }

    /**
     * sets all of `paintRef.controlMask` to Opaque and unseen to players
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

    override fun paintComponent(g: Graphics) {
        val g2d = g as Graphics2D
        when {
            loading -> {
                g2d.drawString(Labels.LOADING, size.width / 2, size.height / 2)
            }
            parent.paintRef != null -> {
                g2d.drawImage(parent.paintRef?.controlImage, 0, 0, size.width, size.height, null)
                g2d.drawImage(parent.paintRef?.controlMask, 0, 0, size.width, size.height, null)
                g2d.color = Colors.PINK
                when (penType) {
                    Pen.CIRCLE -> g2d.drawOval(mousePos.x - radius, mousePos.y - radius, 2 * radius, 2 * radius)
                    Pen.SQUARE -> g2d.drawRect(mousePos.x - radius, mousePos.y - radius, 2 * radius, 2 * radius)
                    Pen.HEX -> g2d.drawPolygon(getHexPolygon(mousePos, radius, radius))
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
            else -> {
                g2d.drawString(Labels.NO_IMAGE_LOADED, size.width / 2, size.height / 2)
            }
        }
    }

    override fun mousePressed(e: MouseEvent) {
        lastP = toDrawingPoint(e.point)
        when (drawMode) {
            DrawMode.ANY -> {
                if (e.button == MouseEvent.BUTTON2) {
                    parent.paintRef?.setWindowPosition(lastP)
                    parent.repaintDisplay()
                    canDraw = false
                } else {
                    if (e.button == MouseEvent.BUTTON1) {
                        parent.paintRef?.maskGraphics?.paint = Colors.CLEAR
                        canDraw = true
                    } else if (e.button == MouseEvent.BUTTON3) {
                        parent.paintRef?.maskGraphics?.paint = Colors.OPAQUE
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
                parent.paintRef?.setWindowPosition(lastP)
                parent.repaintDisplay()
            }
        }
        repaint()
    }

    override fun mouseReleased(e: MouseEvent) {
        if (canDraw) {
            when (penType) {
                Pen.RECT -> {
                    val p = toDrawingPoint(e.point)
                    val p2 = toDrawingPoint(startOfClick!!)
                    parent.paintRef?.maskGraphics?.fillRect(
                        min(p.x, p2.x),
                        min(p.y, p2.y),
                        abs(p.x - p2.x),
                        abs(p.y - p2.y)
                    )
                }
                else -> {
                }
            }
        }
        dragging = false
        repaint()
    }

    override fun mouseDragged(e: MouseEvent) {
        if (canDraw) {
            addPoint(toDrawingPoint(e.point))
        } else {
            parent.paintRef?.setWindowPosition(toDrawingPoint(e.point))
            parent.repaintDisplay()
        }
        mousePos = e.point
        repaint()
    }

    override fun mouseMoved(e: MouseEvent) {
        mousePos = e.point
        repaint()
    }

    override fun componentResized(e: ComponentEvent) {
        repaint()
    }

    override fun mouseEntered(e: MouseEvent?) {}

    override fun mouseClicked(e: MouseEvent?) {}

    override fun mouseExited(e: MouseEvent?) {}

    override fun componentMoved(e: ComponentEvent) {}

    override fun componentHidden(e: ComponentEvent) {}

    override fun componentShown(e: ComponentEvent) {}
}