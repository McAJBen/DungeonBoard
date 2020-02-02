package paint

import util.Colors
import java.io.File
import javax.imageio.ImageIO

/**
 * holds data on a single image
 * @author McAJBen@gmail.com
 * @param source the picture file within the paint folder to use
 * @since 2.5.1
 */
class PaintFileReference(source: File) : PaintReference(source) {

    override fun loadImages() {
        controlImage = ImageIO.read(source)
        displayImage = controlImage
    }

    override fun updateDisplayMask() {
        displayMask.apply {
            for (i in 0 until width) {
                for (j in 0 until height) {
                    setRGB(i, j, when (controlMask.getRGB(i, j)) {
                        Colors.CLEAR.rgb -> Colors.TRANSPARENT.rgb
                        else -> Colors.BLACK.rgb
                    })
                }
            }
        }
    }
}