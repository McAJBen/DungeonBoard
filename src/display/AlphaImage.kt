package display

import util.Labels
import util.Log
import util.Settings
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

/**
 * a container for name and an image
 * @param name the name of the specific file
 * @param folder the folder that contains the file named n
 * @author McAJBen@gmail.com
 * @since 1.6
 */
class AlphaImage(
    val name: String,
    folder: File
) {

    /**
     * the file used to load the image
     */
    private val file = File(folder, name)

    /**
     * the `BufferedImage` from the file
     */
    val image: BufferedImage?
        get() {
            var error: OutOfMemoryError? = null
            for (i in 0..49) {
                try {
                    return ImageIO.read(file)
                } catch (e: OutOfMemoryError) {
                    error = e
                    try {
                        Thread.sleep(10)
                    } catch (e1: InterruptedException) {}
                } catch (e: Exception) {
                    Log.error(String.format(Labels.CANNOT_LOAD_IMAGE, name), e)
                }
            }
            if (error != null) {
                Log.error(String.format(Labels.CANNOT_LOAD_IMAGE_ERROR_MULTIPLE, name), error)
            }
            return null
        }

    /**
     * the background color of the image by using the top left corner pixel
     */
    val bGColor: Color
        get() {
            val f = Settings.fileToThumb(file)
            try {
                return Color(ImageIO.read(f).getRGB(0, 0))
            } catch (e: Exception) {
                Log.error(String.format(Labels.CANNOT_LOAD_IMAGE_RGB, name), e)
            }
            return Color.BLACK
        }

}