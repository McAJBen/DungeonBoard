package paint

import util.Colors
import util.listFilesInOrder
import java.awt.Color
import java.awt.Image
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

/**
 * holds data for a folder of images
 * @author McAJBen@gmail.com
 * @param source the folder within the paint folder to use
 * @since 2.5.1
 */
class PaintFolderReference(source: File) : PaintReference(source) {

    /**
     * data class for storing a file and it's visibility
     */
    private data class ImageFile(
        val file: File,
        var visibility: Boolean
    )

    /**
     * optional backgroundFile to paint with instead of a black background
     */
    private val backgroundFile: File? = source.listFiles(File::isFile)?.find {
        it.nameWithoutExtension.equals("Background", ignoreCase = true)
    }

    /**
     * list of `ImageFiles` that can be enabled/disabled
     */
    private val imageFiles: List<ImageFile> = source.listFilesInOrder().filter {
        it.nameWithoutExtension.matches("[0-9]+".toRegex())
    }.map {
        ImageFile(it, false)
    }

    /**
     * gets the names of all `imageFiles`
     * @return the list of filenames that can be enabled/disabled
     */
    fun getImageFileNames(): List<String> {
        return imageFiles.map {
            it.file.nameWithoutExtension
        }
    }

    /**
     * changes the visibility of an image
     * @param fileName the name of the file to change
     * @param visibility the new visibility, true for visible, false for invisible
     */
    fun setImageVisibility(fileName: String, visibility: Boolean) {
        imageFiles.find {
            it.file.nameWithoutExtension == fileName
        }?.visibility = visibility
    }

    /**
     * updates the displayImage based on the currently visible `ImageFiles`
     */
    fun updateDisplayImage() {
        val g2d = displayImage.createGraphics()
        g2d.color = Color.BLACK
        g2d.fillRect(
            0,
            0,
            displayImage.width,
            displayImage.height
        )

        if (backgroundFile != null) {
            g2d.drawImage(
                ImageIO.read(backgroundFile).getScaledInstance(
                    displayMask.width,
                    displayMask.height,
                    Image.SCALE_AREA_AVERAGING
                ),
                0,
                0,
                displayImage.width,
                displayImage.height,
                null
            )
        }

        imageFiles.filter {
            it.visibility
        }.reversed().forEach {
            g2d.drawImage(
                ImageIO.read(it.file),
                0,
                0,
                displayImage.width,
                displayImage.height,
                null
            )
        }

        g2d.dispose()
    }

    /**
     * loads a lower resolution version of the background image to match the resolution of displayMask
     * @return the scaled background image
     */
    private fun getBackgroundImage(): BufferedImage {
        return BufferedImage(
            displayMask.width,
            displayMask.height,
            BufferedImage.TYPE_INT_ARGB
        ).apply {
            val g2d = createGraphics()
            g2d.drawImage(
                ImageIO.read(backgroundFile).getScaledInstance(
                    width,
                    height,
                    Image.SCALE_AREA_AVERAGING
                ),
                0,
                0,
                null
            )
            g2d.dispose()
        }
    }

    override fun loadImages() {
        val guideFile = source.listFiles(File::isFile)?.find {
            it.nameWithoutExtension.equals("Guide", ignoreCase = true)
        }

        controlImage = ImageIO.read(guideFile ?: imageFiles.last().file)

        displayImage = BufferedImage(
            controlImage.width,
            controlImage.height,
            BufferedImage.TYPE_INT_ARGB
        )
    }

    override fun updateDisplayMask() {
        if (backgroundFile == null) {
            displayMask.apply {
                for (i in 0 until width) {
                    for (j in 0 until height) {
                        setRGB(i, j, when(controlMask.getRGB(i, j)) {
                            Colors.CLEAR.rgb -> Colors.TRANSPARENT.rgb
                            else -> Colors.BLACK.rgb
                        })
                    }
                }
            }
        } else {
            val backgroundImage = getBackgroundImage()

            displayMask.apply {
                for (i in 0 until width) {
                    for (j in 0 until height) {
                        setRGB(i, j, when(controlMask.getRGB(i, j)) {
                            Colors.CLEAR.rgb -> Colors.TRANSPARENT.rgb
                            else -> backgroundImage.getRGB(i, j)
                        })
                    }
                }
            }
        }
    }
}