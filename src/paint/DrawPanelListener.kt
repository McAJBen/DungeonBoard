package paint

import java.awt.Point
import java.awt.image.BufferedImage

/**
 * a listener for events coming from `DrawPanel
 * @author McAJBen@gmail.com
 * @since 2.5
 */
interface DrawPanelListener {

    /**
     * updates the mask on display
     * @param mask the new mask
     */
    fun setMask(mask: BufferedImage)

    /**
     * updates the window position and zoom on display
     * @param zoom the new zoom
     * @param windowPos the new window position
     */
    fun setWindow(zoom: Double, windowPos: Point)

    /**
     * updates the window position on display
     * @param windowPos the new window position
     */
    fun setWindowPos(windowPos: Point)
}
