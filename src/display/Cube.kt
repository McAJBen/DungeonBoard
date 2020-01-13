package display

import main.Settings
import java.awt.Graphics2D
import java.awt.Point
import java.util.*

/**
 * an image that floats around the screen like the old DvD logo would on DvD players
 * @author McAJBen@gmail.com
 * @since 1.9
 */
class Cube {
    /**
     * how many pixels per movement the cube makes
     */
    private val SPEED: Int
    /**
     * the current position of the top left corner
     */
    private val point: Point
    /**
     * the vertical velocity direction<br></br>
     * - true moves up<br></br>
     * - false moves down
     */
    private var vertical: Boolean
    /**
     * the horizontal velocity direction<br></br>
     * - true moves left<br></br>
     * - false moves right
     */
    private var horizontal: Boolean
    /**
     * a flag for if the cube has 'hit the corner'<br></br>
     * - true if it is in the corner<br></br>
     * - false if it is still floating around
     */
    private var flag: Boolean

    /**
     * paints the cube to the screen
     * @param g2d the graphics to paint onto
     */
    fun paint(g2d: Graphics2D) {
        if (flag) {
            g2d.drawImage(Settings.ICON_DVD2.image, point.x, point.y, null)
        } else {
            g2d.drawImage(Settings.ICON_DVD.image, point.x, point.y, null)
        }
    }

    /**
     * moves the cube one motion
     */
    fun move() {
        if (!flag) {
            var vertHit = false
            if (vertical) {
                point.x -= SPEED
                if (point.x < 0) {
                    point.x = 0
                    vertical = false
                    vertHit = true
                }
            } else {
                point.x += SPEED
                if (point.x > Settings.DISPLAY_SIZE!!.width - Settings.ICON_DVD.iconWidth) {
                    point.x = Settings.DISPLAY_SIZE!!.width - Settings.ICON_DVD.iconWidth
                    vertical = true
                    vertHit = true
                }
            }
            if (horizontal) {
                point.y -= SPEED
                if (point.y < 0) {
                    point.y = 0
                    horizontal = false
                    if (vertHit) {
                        flag = true
                    }
                }
            } else {
                point.y += SPEED
                if (point.y > Settings.DISPLAY_SIZE!!.height - Settings.ICON_DVD.iconHeight) {
                    point.y = Settings.DISPLAY_SIZE!!.height - Settings.ICON_DVD.iconHeight
                    horizontal = true
                    if (vertHit) {
                        flag = true
                    }
                }
            }
        }
    }

    /**
     * creates as instance of `Cube` within the window
     */
    init {
        val rand = Random()
        SPEED = rand.nextInt(5) + 1
        point = Point(
            rand.nextInt(Settings.DISPLAY_SIZE!!.width - Settings.ICON_DVD.iconWidth),
            rand.nextInt(Settings.DISPLAY_SIZE!!.height - Settings.ICON_DVD.iconHeight)
        )
        vertical = rand.nextBoolean()
        horizontal = rand.nextBoolean()
        flag = false
    }
}