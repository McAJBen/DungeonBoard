package control

import main.Mode
import paint.*
import util.*
import java.awt.BorderLayout
import java.awt.Dimension
import java.io.File
import java.lang.Double.max
import java.util.concurrent.Executors
import javax.swing.*

/**
 * a `Control` for the Paint Utility
 * @param displayListener callback to `DisplayPaint`
 * @author McAJBen@gmail.com
 * @since 1.0
 */
class ControlPaint(
    private val displayListener: ControlPaintListener
) : Control(), DrawPanelListener {

    companion object {
        private const val serialVersionUID = -3231530555502467648L
    }

    /**
     * used to synchronize actions and prevent them from running no the ui thread
     */
    private val executor = Executors.newSingleThreadExecutor()

    /**
     * the main panel the user draws onto to create a mask
     */
    private val drawPanel = DrawPanel(this)

    /**
     * the drop down menu for selecting a file
     */
    private val fileBox = JComboBox<String>().apply {
        background = Colors.CONTROL_BACKGROUND
        addItem("")
    }

    /**
     * the maximum zoom this image is allowed
     */
    private var maxZoom = 10.0

    /**
     * the text field for changing zoom
     */
    private val zoomText = JTextField("1.00", 1).apply {
        maximumSize = Dimension(5000, 25)
    }

    /**
     * the slider for changing zoom
     */
    private val zoomSlider = JSlider(SwingConstants.VERTICAL, 1, (maxZoom * 100).toInt(), 100)

    /**
     * the `JPanel` holding options for which file in a folder to display
     */
    private val folderControlPanel = emptyNorthPanel.apply {
        isVisible = false
    }

    /**
     * button to confirm mask changes and update the display
     */
    private val updateButton = createButton(Labels.UPDATE_SCREEN).apply {
        addActionListener {
            isEnabled = false
            background = Colors.CONTROL_BACKGROUND
            executor.execute {
                try {
                    paintRef?.updateDisplayMask()
                    displayListener.repaint()
                } catch (error: OutOfMemoryError) {
                    Log.error(Labels.CANNOT_UPDATE_IMAGE, error)
                }
            }
        }
        isEnabled = false
    }

    /**
     * reference to paint data
     */
    override var paintRef: PaintReference? = null

    init {
        load()

        fileBox.addActionListener {
            if (fileBox.selectedIndex != 0) {
                executor.submit {
                    changeSource(File(Settings.getFolder(Mode.PAINT), fileBox.selectedItem as String))
                }
            }
        }

        zoomText.addActionListener {
            var zoom: Double
            try {
                zoom = zoomText.text.toDouble().boundZoom()
                paintRef?.setDisplayZoom(zoom)
                drawPanel.repaint()
                displayListener.repaint()
            } catch (nfe: NumberFormatException) {
                zoom = zoomSlider.value / 100.0
            }
            zoomText.text = String.format("%.2f", zoom)
            zoomSlider.value = (zoom * 100).toInt()
        }

        zoomSlider.addChangeListener {
            val zoom = (zoomSlider.value / 100.0).boundZoom()
            zoomText.text = String.format("%.2f", zoom)
            paintRef?.setDisplayZoom(zoom)
            drawPanel.repaint()
            displayListener.repaint()
        }

        val drawStyleButton = createButton(Resources.DRAW_STYLE[0]).apply {
            addActionListener {
                drawPanel.toggleStyle()
                icon = drawPanel.getStyleResource()
            }
        }

        val shapeButton = createButton(Resources.PEN_TYPE[0]).apply {
            addActionListener {
                drawPanel.togglePen()
                icon = drawPanel.getPenResource()
            }
        }

        val drawModeButton = createButton(Resources.DRAW_MODE[0]).apply {
            addActionListener {
                drawPanel.toggleDrawMode()
                icon = drawPanel.getDrawModeResource()
            }
        }

        val gridButton = createButton(Resources.ICON_GRID).also {
            it.addActionListener {
                paintRef?.let { pr ->
                    val gridMenu = GridMenu(
                        SwingUtilities.getWindowAncestor(this) as JFrame,
                        pr.paintData.grid,
                        displayListener
                    )
                    pr.paintData.grid = gridMenu.getResult()
                    gridMenu.isVisible = true

                    pr.paintData.grid = gridMenu.getResult()
                    displayListener.repaint()
                }
            }
        }

        val showButton = createButton(Labels.SHOW).apply {
            background = Colors.ACTIVE
            addActionListener { drawPanel.showAll() }
        }

        val hideButton = createButton(Labels.HIDE).apply {
            background = Colors.INACTIVE
            addActionListener { drawPanel.hideAll() }
        }

        val sizeSlider = JSlider(SwingConstants.HORIZONTAL, 10, 100, 25).apply {
            background = Colors.CONTROL_BACKGROUND
            addChangeListener { drawPanel.setRadius(value) }
        }

        add(
            JPanel().apply {
                layout = BoxLayout(this, BoxLayout.Y_AXIS)
                background = Colors.CONTROL_BACKGROUND
                add(JLabel(Labels.ZOOM, SwingConstants.LEFT))
                add(zoomText)
                add(zoomSlider)
            },
            BorderLayout.WEST
        )
        add(
            JPanel().apply {
                layout = BoxLayout(this, BoxLayout.Y_AXIS)
                add(folderControlPanel)
                add(northPanel.apply {
                    add(fileBox)
                    add(drawStyleButton)
                    add(shapeButton)
                    add(drawModeButton)
                    add(gridButton)
                    add(showButton)
                    add(hideButton)
                    add(sizeSlider)
                    add(updateButton)
                })
            },
            BorderLayout.NORTH
        )
        add(drawPanel, BorderLayout.CENTER)
        isFocusable = true
        isVisible = true
    }

    /**
     * called when the source file or folder has changed. Used to change the paintRef and load new source
     * @param source the file or folder within the paint folder
     */
    private fun changeSource(source: File) {
        if (source.exists()) {
            drawPanel.setImageLoading(true)
            paintRef?.save()
            val paintRef = when {
                source.isDirectory -> {
                    val pr = PaintFolderReference(source)
                    setFolderControlPanel(pr)
                    pr.updateDisplayImage()
                    pr
                }
                else -> {
                    folderControlPanel.isVisible = false
                    PaintFileReference(source)
                }
            }

            maxZoom = max(
                paintRef.displayImage.width / Settings.DISPLAY_SIZE.getWidth(),
                paintRef.displayImage.height / Settings.DISPLAY_SIZE.getHeight()
            )
            zoomSlider.maximum = (maxZoom * 100).toInt()
            zoomSlider.value = (paintRef.paintData.displayZoom * 100).toInt()
            zoomText.text = String.format("%.2f", paintRef.paintData.displayZoom)

            paintRef.updateDisplayMask()

            displayListener.setPaintReference(paintRef)
            this.paintRef = paintRef
            drawPanel.setImageLoading(false)
        } else {
            Log.error(Labels.CANNOT_LOAD_IMAGE_DOES_NOT_EXIST)
        }
    }

    /**
     * updates the `folderControlPanel` with the new `PaintFolderReference`
     * @param paintRef the new reference to paint data
     */
    private fun setFolderControlPanel(paintRef: PaintFolderReference) {
        folderControlPanel.removeAll()
        paintRef.getImageFileNames().forEach { imageName ->
            folderControlPanel.add(createButton(imageName).apply {
                background = Colors.INACTIVE
                addActionListener {
                    if (background == Colors.ACTIVE) {
                        background = Colors.INACTIVE
                        paintRef.setImageVisibility(imageName, false)
                    } else {
                        background = Colors.ACTIVE
                        paintRef.setImageVisibility(imageName, true)
                    }

                    executor.execute {
                        paintRef.updateDisplayImage()
                        displayListener.repaint()
                    }
                }
            })
        }
        folderControlPanel.isVisible = true
    }

    /**
     * transforms the value to be between 0.01 and maxZoom
     */
    private fun Double.boundZoom(): Double {
        return when {
            this < 0.01 -> 0.01
            this > maxZoom -> maxZoom
            else -> this
        }
    }

    override fun maskHasChanged() {
        updateButton.isEnabled = true
        updateButton.background = Colors.ACTIVE
    }

    override fun repaintDisplay() {
        displayListener.repaint()
    }

    override fun load() {
        while (fileBox.itemCount > 1) {
            fileBox.removeItemAt(1)
        }
        Settings.getFolder(Mode.PAINT).listFilesInOrder().forEach {
            if (it.isDirectory) {
                fileBox.addItem(it.name)
            } else if (it.extension.equals("PNG", ignoreCase = true)
                || it.extension.equals("JPG", ignoreCase = true)
                || it.extension.equals("JPEG", ignoreCase = true)
            ) {
                fileBox.addItem(it.name)
            }
        }
    }

    override fun setMainControl(b: Boolean) {}

    override fun onClosing() {
        paintRef?.save()
    }
}