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

    companion object {
        private val rand = Random()
    }

    /**
     * how many pixels per movement the cube makes
     */
    private val speed = rand.nextInt(5) + 1
    /**
     * the current position of the top left corner
     */
    private val point = Point(
        rand.nextInt(Settings.DISPLAY_SIZE!!.width - Settings.ICON_DVD.iconWidth),
        rand.nextInt(Settings.DISPLAY_SIZE!!.height - Settings.ICON_DVD.iconHeight)
    )
    /**
     * the vertical velocity direction
     * - true moves up
     * - false moves down
     */
    private var vertical = rand.nextBoolean()
    /**
     * the horizontal velocity direction
     * - true moves left
     * - false moves right
     */
    private var horizontal = rand.nextBoolean()
    /**
     * a flag for if the cube has 'hit the corner'
     * - true if it is in the corner
     * - false if it is still floating around
     */
    private var flag = false

    /**
     * paints the cube to the screen
     * @param g2d the graphics to paint onto
     */
    fun paint(g2d: Graphics2D) {
        g2d.drawImage(
            if (flag) {
                Settings.ICON_DVD2.image
            } else {
                Settings.ICON_DVD.image
            },
            point.x,
            point.y,
            null
        )
    }

    /**
     * moves the cube one motion
     */
    fun move() {
        if (!flag) {
            var verticalHit = false
            if (vertical) {
                point.x -= speed
                if (point.x < 0) {
                    point.x = 0
                    vertical = false
                    verticalHit = true
                }
            } else {
                point.x += speed
                if (point.x > Settings.DISPLAY_SIZE!!.width - Settings.ICON_DVD.iconWidth) {
                    point.x = Settings.DISPLAY_SIZE!!.width - Settings.ICON_DVD.iconWidth
                    vertical = true
                    verticalHit = true
                }
            }
            if (horizontal) {
                point.y -= speed
                if (point.y < 0) {
                    point.y = 0
                    horizontal = false
                    if (verticalHit) {
                        flag = true
                    }
                }
            } else {
                point.y += speed
                if (point.y > Settings.DISPLAY_SIZE!!.height - Settings.ICON_DVD.iconHeight) {
                    point.y = Settings.DISPLAY_SIZE!!.height - Settings.ICON_DVD.iconHeight
                    horizontal = true
                    if (verticalHit) {
                        flag = true
                    }
                }
            }
        }
    }
}