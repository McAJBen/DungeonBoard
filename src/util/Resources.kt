package util

import java.awt.image.BufferedImage
import javax.swing.ImageIcon

/**
 * contains references to images in the resources directory
 * @author McAJBen@gmail.com
 * @since 2.5
 */
object Resources {

    /**
     * the main `ImageIcon` for Dungeon Board
     */
    val ICON = loadResource("icon.gif")

    /**
     * the `ImageIcon` for a refresh `JButton`
     */
    val ICON_REFRESH = loadResource("refresh.gif")

    /**
     * the `ImageIcon` for a flip `JButton`
     */
    val ICON_FLIP = loadResource("flip.gif")

    /**
     * the `ImageIcon` for a settings `JButton`
     */
    val ICON_SETTINGS = loadResource("settings.gif")

    /**
     * the `ImageIcon` that floats around the `DisplayLoading`
     */
    val ICON_DVD = loadResource("dvdLogo.gif")

    /**
     * the `ImageIcon` that sticks to the corner of `DisplayLoading`
     */
    val ICON_DVD2 = loadResource("dvdLogo2.gif")

    /**
     * an array of `ImageIcons` visualizing the drawing `Direction` state on a `JButton`
     */
    val DRAW_STYLE = arrayOf(
        loadResource("squiggle.gif"),
        loadResource("vertical.gif"),
        loadResource("horizontal.gif")
    )

    /**
     * an array of `ImageIcons` visualizing the `DrawMode` state on a `JButton`
     */
    val DRAW_MODE = arrayOf(
        loadResource("mouse.gif"),
        loadResource("visible.gif"),
        loadResource("invisible.gif"),
        loadResource("move.gif")
    )

    /**
     * an array of `ImageIcons` visualizing the `Pen` state on a `JButton`
     */
    val PEN_TYPE = arrayOf(
        loadResource("circle.gif"),
        loadResource("square.gif"),
        loadResource("hex.gif"),
        loadResource("rect.gif")
    )

    /**
     * a blank 3x3 `BufferedImage` for displaying an invisible cursor, or as a placeholder for an image;
     */
    val BLANK_CURSOR = BufferedImage(3, 3, BufferedImage.TYPE_INT_ARGB)

    /**
     * the images for cursor hands
     */
    val HANDS = arrayOf(
        loadResource("hand0.png"),
        loadResource("hand1.png"),
        loadResource("hand2.png"),
        loadResource("hand3.png")
    )

    /**
     * method used to load resources by file name
     * @param res name of the file to be loaded
     * @return an `ImageIcon` from the resources folder
     */
    private fun loadResource(res: String): ImageIcon {
        return ImageIcon(Resources::class.java.getResource("/resources/$res"))
    }
}