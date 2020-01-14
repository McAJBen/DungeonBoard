package display

import main.Settings
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
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
                    } catch (e1: InterruptedException) {
                    }
                } catch (e: IllegalArgumentException) {
                    Settings.showError("Cannot load Image \"$name", e)
                } catch (e: IOException) {
                    Settings.showError("Cannot load Image \"$name", e)
                }
            }
            Settings.showError(
                "Cannot Load Image\"" + name + "\" after 50 attempts\n" +
                        "Allocate more memory, use smaller images", error
            )
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
            } catch (e: IllegalArgumentException) {
                Settings.showError("Cannot load Image RGB \"$name", e)
            } catch (e: IOException) {
                Settings.showError("Cannot load Image RGB \"$name", e)
            }
            return Color.BLACK
        }

}