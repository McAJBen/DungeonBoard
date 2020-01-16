package control

import main.Mode
import paint.DrawPanel
import paint.DrawPanelListener
import util.*
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Dimension
import java.awt.Point
import java.awt.image.BufferedImage
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException
import javax.imageio.ImageIO
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

    init {
        load()

        fileBox.addActionListener {
            if (fileBox.selectedIndex == 0) {
                return@addActionListener
            }

            val file = File(Settings.getFolder(Mode.PAINT), fileBox.selectedItem as String)
            if (file.exists()) {
                drawPanel.saveMask()
                val maskFile = Settings.fileToMaskFile(file)
                val dataFile = File(Settings.PAINT_MASK_FOLDER, "${maskFile.name}.data")
                if (dataFile.exists()) {
                    try {
                        val br = BufferedReader(FileReader(dataFile))
                        val data =
                            br.readLine().split(" ".toRegex()).toTypedArray()
                        val zoom = data[0].toDouble()
                        val p = Point(data[1].toInt(), data[2].toInt())
                        zoomSlider.maximum = 10000
                        zoomSlider.value = (zoom * 100).toInt()
                        zoomText.text = String.format("%.2f", zoom)
                        drawPanel.setWindow(zoom, p)
                        br.close()
                    } catch (e2: IOException) {
                        Log.error(Labels.CANNOT_LOAD_MASK_DATA, e2)
                    }
                }
                if (file.isDirectory) {
                    folderControlPanel.isVisible = true
                    setFolder(file)
                } else {
                    folderControlPanel.isVisible = false
                    setFile(file)
                }
            } else {
                Log.error(Labels.CANNOT_LOAD_IMAGE_DOES_NOT_EXIST)
            }
        }

        zoomText.addActionListener {
            var zoom: Double
            try {
                zoom = zoomText.text.toDouble().boundZoom()
                drawPanel.setZoom(zoom)
            } catch (nfe: NumberFormatException) {
                zoom = zoomSlider.value / 100.0
            }
            zoomText.text = String.format("%.2f", zoom)
            zoomSlider.value = (zoom * 100).toInt()
        }

        zoomSlider.addChangeListener {
            val zoom = (zoomSlider.value / 100.0).boundZoom()
            zoomText.text = String.format("%.2f", zoom)
            drawPanel.setZoom(zoom)
        }

        val drawStyleButton = createButton(Resources.DRAW_STYLE[0]).apply {
            addActionListener {
                drawPanel.toggleStyle()
                icon = Resources.DRAW_STYLE[drawPanel.style]
            }
        }

        val shapeButton = createButton(Resources.PEN_TYPE[0]).apply {
            addActionListener {
                drawPanel.togglePen()
                icon = Resources.PEN_TYPE[drawPanel.pen]
            }
        }

        val drawModeButton = createButton(Resources.DRAW_MODE[0]).apply {
            addActionListener {
                drawPanel.toggleDrawMode()
                icon = Resources.DRAW_MODE[drawPanel.drawMode.ordinal]
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
                    add(showButton)
                    add(hideButton)
                    add(sizeSlider)
                    add(drawPanel.updateButton)
                })
            },
            BorderLayout.NORTH
        )
        add(drawPanel, BorderLayout.CENTER)
        isFocusable = true
        isVisible = true
    }

    /**
     * sets up the selected folder for painting
     * loads it into control and the display
     * @param folder the folder name to load
     */
    private fun setFolder(folder: File) {
        Settings.PAINT_FOLDER = folder
        Settings.PAINT_FOLDER_SIZE = 0
        for (f in Settings.PAINT_FOLDER!!.listFilesInOrder().filter { obj: File -> obj.isFile }) {
            try {
                if (f.extension.equals("PNG", ignoreCase = true)
                    && f.nameWithoutExtension.toInt() == Settings.PAINT_FOLDER_SIZE + 1
                ) {
                    Settings.PAINT_FOLDER_SIZE++
                }
            } catch (e: NumberFormatException) {
                if (!f.name.equals("Guide.png", ignoreCase = true) && !f.name.equals("Background.png", ignoreCase = true)) {
                    Log.debug(
                        String.format(
                            Labels.FILE_NOT_IN_CORRECT_FORMAT,
                            f.name,
                            Settings.PAINT_FOLDER_SIZE
                        )
                    )
                }
            }
        }
        Settings.PAINT_IMAGES = BooleanArray(Settings.PAINT_FOLDER_SIZE)
        folderControlPanel.removeAll()

        // create all buttons
        for (fileNum in 1..Settings.PAINT_FOLDER_SIZE) {
            folderControlPanel.add(createButton(fileNum.toString()).apply {
                background = Colors.INACTIVE
                addActionListener { e ->
                    val number = (e.source as JButton).text.toInt()
                    if (background == Colors.ACTIVE) {
                        background = Colors.INACTIVE
                        Settings.PAINT_IMAGES!![number - 1] = false
                    } else {
                        background = Colors.ACTIVE
                        Settings.PAINT_IMAGES!![number - 1] = true
                    }
                    object : Thread("fileLoadingThread") {
                        override fun run() {
                            try {
                                synchronized(Settings.PAINT_IMAGE!!) {
                                    val g2d = Settings.PAINT_IMAGE!!.createGraphics()
                                    g2d.color = Color.BLACK
                                    g2d.fillRect(
                                        0,
                                        0,
                                        Settings.PAINT_IMAGE!!.width,
                                        Settings.PAINT_IMAGE!!.height
                                    )
                                    for (j in Settings.PAINT_FOLDER_SIZE downTo 1) {
                                        if (Settings.PAINT_IMAGES!![j - 1]) {
                                            val f = File(Settings.PAINT_FOLDER, "$j.png")
                                            g2d.drawImage(ImageIO.read(f), 0, 0, null)
                                        }
                                    }
                                    g2d.dispose()
                                    if (Settings.PAINT_IMAGE != null) {
                                        displayListener.setMask(drawPanel.mask)
                                        displayListener.setImageSize()
                                        setZoomMax()
                                    }
                                }
                            } catch (error: Exception) {
                                drawPanel.resetImage()
                                displayListener.setMask()
                                Settings.PAINT_IMAGE = null
                                Log.error(Labels.CANNOT_LOAD_IMAGE_ERROR, error)
                            }
                            displayListener.repaint()
                            drawPanel.repaint()
                            drawPanel.setImageLoading(false)
                        }
                    }.start()
                }
            })
        }
        folderControlPanel.revalidate()

        // loads guide
        if (Settings.PAINT_FOLDER != null && Settings.PAINT_FOLDER!!.exists()) {
            drawPanel.setImageLoading(true)
            object : Thread("folderLoadingThread") {
                override fun run() {
                    try {
                        Settings.PAINT_IMAGE = null
                        Settings.PAINT_CONTROL_IMAGE = null
                        var imageSize: Dimension
                        run {
                            val guideImg = ImageIO.read(Settings.PAINT_GUIDE_FILE)
                            imageSize = Dimension(guideImg.width, guideImg.height)
                            Settings.PAINT_CONTROL_IMAGE = BufferedImage(
                                imageSize.width / Settings.PAINT_GUIDE_SCALE,
                                imageSize.height / Settings.PAINT_GUIDE_SCALE,
                                BufferedImage.TYPE_INT_RGB
                            )
                            Settings.PAINT_CONTROL_IMAGE!!.graphics.drawImage(
                                guideImg.getScaledInstance(
                                    imageSize.width / Settings.PAINT_GUIDE_SCALE,
                                    imageSize.height / Settings.PAINT_GUIDE_SCALE,
                                    BufferedImage.SCALE_SMOOTH
                                ), 0, 0, null
                            )
                        }
                        Settings.PAINT_IMAGE = BufferedImage(
                            imageSize.width,
                            imageSize.height,
                            BufferedImage.TYPE_INT_ARGB
                        )
                        drawPanel.setImage()
                        displayListener.setMask(drawPanel.mask)
                        displayListener.setImageSize()
                        setZoomMax()
                    } catch (error: Exception) {
                        drawPanel.resetImage()
                        displayListener.setMask()
                        Settings.PAINT_IMAGE = null
                        Log.error(Labels.CANNOT_LOAD_IMAGE_ERROR, error)
                    }
                    displayListener.repaint()
                    drawPanel.repaint()
                    drawPanel.setImageLoading(false)
                }
            }.start()
        }
    }

    /**
     * sets up the selected file for painting
     * loads it into control and the display
     * @param file the file name to load
     */
    private fun setFile(file: File) {
        drawPanel.setImageLoading(true)
        Settings.PAINT_FOLDER = file
        object : Thread("fileLoadingThread") {
            override fun run() {
                try {
                    Settings.PAINT_IMAGE = null
                    Settings.PAINT_IMAGE = ImageIO.read(file)
                    Settings.PAINT_CONTROL_IMAGE = Settings.PAINT_IMAGE
                    if (Settings.PAINT_IMAGE != null) {
                        drawPanel.setImage()
                        displayListener.setMask(drawPanel.mask)
                        displayListener.setImageSize()
                        setZoomMax()
                    }
                } catch (error: Exception) {
                    drawPanel.resetImage()
                    displayListener.setMask()
                    Settings.PAINT_IMAGE = null
                    Log.error(Labels.CANNOT_LOAD_IMAGE_ERROR, error)
                }
                displayListener.repaint()
                drawPanel.repaint()
                drawPanel.setImageLoading(false)
            }
        }.start()
    }

    /**
     * changes the maximum zoom so the image cannot be smaller than the screen
     */
    private fun setZoomMax() {
        val w = Settings.PAINT_IMAGE!!.width / Settings.DISPLAY_SIZE!!.getWidth()
        val h = Settings.PAINT_IMAGE!!.height / Settings.DISPLAY_SIZE!!.getHeight()
        maxZoom = if (h > w) h else w
        zoomSlider.maximum = (maxZoom * 100).toInt()
    }

    override fun load() {
        while (fileBox.itemCount > 1) {
            fileBox.removeItemAt(1)
        }
        val folder = Settings.getFolder(Mode.PAINT)
        if (folder.exists()) {
            folder.listFilesInOrder().forEach {
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

    override fun setMainControl(b: Boolean) {}

    override fun onClosing() {
        drawPanel.saveMask()
    }

    override fun setMask(mask: BufferedImage) {
        displayListener.setMask(mask)
    }

    override fun setWindow(zoom: Double, windowPos: Point) {
        displayListener.setWindow(zoom, windowPos)
    }

    override fun setWindowPos(windowPos: Point) {
        displayListener.setWindowPos(windowPos)
    }
}