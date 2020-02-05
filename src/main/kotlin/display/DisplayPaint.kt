package display

import control.ControlPaintListener
import paint.PaintReference
import util.Settings.DISPLAY_SIZE
import java.awt.*
import kotlin.math.roundToInt

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
     * reference to paint data
     */
    private var paintRef: PaintReference? = null

    override fun setPaintReference(ref: PaintReference) {
        paintRef = ref
        repaint()
    }

    override fun paint(g: Graphics) {
        super.paint(g)
        val g2d = g as Graphics2D
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)
        g2d.color = Color.BLACK
        g2d.fillRect(0, 0, DISPLAY_SIZE.width, DISPLAY_SIZE.height)

        paintRef?.apply {
            val imageSize = Dimension(
                (displayImage.width / displayZoom).roundToInt(),
                (displayImage.height / displayZoom).roundToInt()
            )

            val offset = Point(
                if (DISPLAY_SIZE.width > imageSize.width) {
                    (DISPLAY_SIZE.width - imageSize.width) / 2
                } else {
                    (-windowOffset.x / displayZoom).roundToInt()
                },
                if (DISPLAY_SIZE.height > imageSize.height) {
                    (DISPLAY_SIZE.height - imageSize.height) / 2
                } else {
                    (-windowOffset.y / displayZoom).roundToInt()
                }
            )

            g2d.drawImage(
                displayImage,
                offset.x,
                offset.y,
                imageSize.width,
                imageSize.height,
                null
            )
            g2d.drawImage(
                displayMask,
                offset.x,
                offset.y,
                imageSize.width,
                imageSize.height,
                null
            )

            gridData?.let { gridData ->
                g2d.paint = gridData.color

                IntProgression.fromClosedRange(
                    gridData.offset.x + offset.x,
                    DISPLAY_SIZE.width,
                    gridData.squareSize.width
                ).forEach {
                    g2d.fillRect(it, 0, gridData.lineWidth, DISPLAY_SIZE.height)
                }

                IntProgression.fromClosedRange(
                    gridData.offset.y + offset.y,
                    DISPLAY_SIZE.height,
                    gridData.squareSize.height
                ).forEach {
                    g2d.fillRect(0, it, DISPLAY_SIZE.width, gridData.lineWidth)
                }
            }
        }

        paintMouse(g2d)
        g2d.dispose()
    }

    override fun setMainDisplay(b: Boolean) {
        if (b) {
            repaint()
        }
    }
}