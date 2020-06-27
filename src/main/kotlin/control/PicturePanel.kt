package control

import main.Mode
import java.awt.GridLayout
import java.io.File
import javax.swing.BorderFactory
import javax.swing.JPanel

/**
 * a scroll menu to display images on as buttons
 * @param mode directory to hold thumbnail caches
 * @author McAJBen@gmail.com
 * @since 1.0
 */
abstract class PicturePanel(
    private val mode: Mode
) : JPanel(), PictureButton.Listener {

    companion object {

        private const val serialVersionUID = 2972394170217781329L

        /**
         * The number of picture per row of a picture panel
         */
        private const val GRID_WIDTH = 4
    }

    /**
     * reference to buttons in this panel
     */
    protected val buttons = mutableListOf<PictureButton>()

    init {
        layout = GridLayout(0, GRID_WIDTH)
        border = BorderFactory.createEmptyBorder()
    }

    /**
     * creates a button for the `PicturePanel` by loading an image from file
     * @param source the file of an image to add
     */
    fun addPicture(source: File) {
        val pictureButton = PictureButton(source, this)

        buttons.add(pictureButton)
    }

    /**
     * loads the thumbnails from file
     */
    fun loadButtons() {
        buttons.forEach {
            it.load()
            add(it.button)
        }
    }

    /**
     * removes the thumbnails from local memory
     */
    fun unloadButtons() {
        buttons.forEach {
            it.unload()
            remove(it.button)
        }
    }

    abstract override fun onChange(button: PictureButton, isEnabled: Boolean)
}