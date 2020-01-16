package control

import util.Resources
import java.awt.Point
import java.awt.image.BufferedImage

/**
 * a listener for events coming from `ControlPaint`
 * @author McAJBen@gmail.com
 * @since 2.5
 */
interface ControlPaintListener {

    /**
     * sets the dimension of the image to be painted
     */
    fun setImageSize()

    /**
     * sets the mask to draw over the image
     * @param mask an image where every pixel is either Color.BLACK or transparent
     */
    fun setMask(mask: BufferedImage = Resources.BLANK_CURSOR)

    /**
     * tells display to repaint
     */
    fun repaint()

    /**
     * sets the window to a specific scale and position
     * @param scale the zoom scale for the image
     * @param windowPos the offset of the top left corner of the image
     */
    fun setWindow(scale: Double, windowPos: Point)

    /**
     * sets the window position
     * @param windowPos the offset of the top left corner of the image
     */
    fun setWindowPos(windowPos: Point)
}