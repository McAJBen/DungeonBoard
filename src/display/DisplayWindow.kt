package display

import util.Resources
import java.awt.Graphics2D
import java.awt.Point
import java.awt.Rectangle
import javax.swing.JFrame

/**
 * `JFrame` for displaying players screen
 * @param r the position and dimensions of the `JFrame`
 * @author McAJBen@gmail.com
 * @since 1.0
 */
class DisplayWindow(r: Rectangle) : JFrame() {

    companion object {
        private const val serialVersionUID = -251787008359029888L
    }

    /**
     * handler for displaying a timer created from `DisplayLoading`
     */
    private val displayTimer = DisplayTimer(this, r.size)

    /**
     * handler for displaying the cursor hand
     */
    private val displayCursor = DisplayCursor(this)

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

        addMouseListener(displayCursor)
        addMouseMotionListener(displayCursor)
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
     * paints the timer and hand cursor to the screen
     * @param g2d the graphics to paint to
     */
    fun paintDisplay(g2d: Graphics2D) {
        displayTimer.paint(g2d)
        displayCursor.paint(g2d)
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
}