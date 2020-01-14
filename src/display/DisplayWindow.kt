package display

import main.Main.controlWindow
import main.Main.getDisplay
import main.Mode
import main.Settings
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
        private val NULL_POS = Point(-100, -100)
        /**
         * the images for cursor hands
         */
        private val HANDS = arrayOf(
            Settings.loadResource("hand0.png"),
            Settings.loadResource("hand1.png"),
            Settings.loadResource("hand2.png"),
            Settings.loadResource("hand3.png")
        )
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
    private val displayTimer = DisplayTimer(size)

    init {
        title = "Display"
        isUndecorated = true
        iconImage = Settings.ICON.image
        size = r.size
        location = r.location
        defaultCloseOperation = EXIT_ON_CLOSE
        cursor = toolkit.createCustomCursor(
            Settings.BLANK_CURSOR, Point(0, 0),
            "null"
        )
        addMouseListener(this)
        addMouseMotionListener(this)
    }

    /**
     * paints the hand cursor to the screen
     * @param g2d the graphics to paint to
     */
    fun paintDisplay(g2d: Graphics2D) {
        val i = handDirection.ordinal
        displayTimer.paint(g2d)
        g2d.drawImage(
            HANDS[i].image,
            mousePos.x + HANDS_OFFSET[i],
            mousePos.y + HANDS_OFFSET[if (i == 0) 3 else i - 1],
            null
        )
    }

    /**
     * changes the panel being displayed
     * @param newMode the new mode to display
     * @param oldMode the old mode displayed before
     */
    fun setMode(newMode: Mode?, oldMode: Mode?) {
        val thread: Thread = object : Thread() {
            override fun run() {
                super.run()
                synchronized(controlWindow) {
                    remove(getDisplay(oldMode))
                    getDisplay(oldMode)!!.setMainDisplay(false)
                    add(getDisplay(newMode))
                    validate()
                    getDisplay(newMode)!!.setMainDisplay(true)
                }
            }
        }
        thread.start()
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