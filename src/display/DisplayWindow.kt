package display

import util.Resources
import java.awt.Graphics2D
import java.awt.Point
import java.awt.Rectangle
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import java.awt.event.MouseMotionListener
import javax.swing.JFrame

/**
 * `JFrame` for displaying players screen
 * @param r the position and dimensions of the `JFrame`
 * @author McAJBen@gmail.com
 * @since 1.0
 */
class DisplayWindow(r: Rectangle) : JFrame(), MouseListener, MouseMotionListener {

    companion object {

        private const val serialVersionUID = -251787008359029888L

        /**
         * the offsets used to display the cursor hand
         */
        private val HANDS_OFFSET = intArrayOf(-5, -100, -45, 0)

        /**
         * the position a cursor is placed when not on screen
         */
        private val NULL_POS = Point(Int.MIN_VALUE, Int.MIN_VALUE)
    }

    /**
     * the position that the mouse is on the screen
     * used to place the hand cursor
     */
    private var mousePos = NULL_POS

    /**
     * the direction that the hand cursor is facing
     */
    private var handDirection = Direction.UP

    /**
     * handler for displaying a timer created from `DisplayLoading`
     */
    private val displayTimer = DisplayTimer(this, r.size)

    /**
     * the currently active display's panel
    */
    private var displayPanel: Display? = null

    init {
        title = "Display"
        isUndecorated = true
        iconImage = Resources.ICON.image
        size = r.size
        location = r.location
        defaultCloseOperation = EXIT_ON_CLOSE
        cursor = toolkit.createCustomCursor(
            Resources.BLANK_CURSOR, Point(0, 0),
            "null"
        )

        addMouseListener(this)
        addMouseMotionListener(this)
    }

    /**
     * changes out the visible display panel
     * @param display a `Display` panel to show
     */
    fun setMode(display: Display) {
        object : Thread() {
            override fun run() {
                synchronized(this@DisplayWindow) {
                    if (displayPanel != null) {
                        remove(displayPanel)
                        displayPanel!!.setMainDisplay(false)
                    }
                    add(display)
                    validate()
                    display.setMainDisplay(true)
                    displayPanel = display
                }
            }
        }.start()
    }

    /**
     * paints the hand cursor to the screen
     * @param g2d the graphics to paint to
     */
    fun paintDisplay(g2d: Graphics2D) {
        val i = handDirection.ordinal
        displayTimer.paint(g2d)
        g2d.drawImage(
            Resources.HANDS[i].image,
            mousePos.x + HANDS_OFFSET[i],
            mousePos.y + HANDS_OFFSET[if (i == 0) 3 else i - 1],
            null
        )
    }

    /**
     * changes the position of the mouse
     * @param p the new position of the mouse
     */
    private fun setMouse(p: Point) {
        mousePos = p
        repaint()
    }

    /**
     * enables a timer for a set amount of seconds
     * @param seconds number of seconds for the timer to count down
     */
    fun setTimer(seconds: Int) {
        displayTimer.setTimer(seconds)
    }

    /**
     * removes the timer from `DisplayWindow`
     */
    fun clearTimer() {
        displayTimer.clearTimer()
    }

    override fun mouseReleased(e: MouseEvent) {}

    override fun mousePressed(e: MouseEvent) {
        handDirection = Direction.values()[(handDirection.ordinal + 1) % Direction.values().size]
        repaint()
    }

    override fun mouseClicked(e: MouseEvent) {}

    override fun mouseExited(e: MouseEvent) {
        setMouse(NULL_POS)
    }

    override fun mouseEntered(e: MouseEvent) {
        setMouse(e.point)
    }

    override fun mouseDragged(e: MouseEvent) {
        setMouse(e.point)
    }

    override fun mouseMoved(e: MouseEvent) {
        setMouse(e.point)
    }
}