package display

import main.Mode
import util.Labels
import util.Log
import util.Settings
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

/**
 * a container for name and an image
 * @param mode used to determine the folder that contains the file
 * @param name the name of the specific file
 * @author McAJBen@gmail.com
 * @since 1.6
 */
class AlphaImage(
    mode: Mode,
    val name: String
) {

    /**
     * the file used to load the image
     */
    private val file = File(Settings.getFolder(mode), name)

    /**
     * the thumbnail file for this image
     */
    private val thumbnail = File(Settings.getDataFolder(mode), name)

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
                    Thread.sleep(10)
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
            try {
                return Color(ImageIO.read(thumbnail).getRGB(0, 0))
            } catch (e: Exception) {
                Log.error(String.format(Labels.CANNOT_LOAD_IMAGE_RGB, name), e)
            }
            return Color.BLACK
        }

}