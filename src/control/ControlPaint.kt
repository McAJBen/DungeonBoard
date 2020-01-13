package control

import main.Main
import main.Mode
import main.Settings
import paint.DrawPanel
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
 * @author McAJBen@gmail.com
 * @since 1.0
 */
class ControlPaint : Control() {

    companion object {
        private const val serialVersionUID = -3231530555502467648L
    }

    /**
     * the main panel the user draws onto to create a mask
     */
    private val drawPanel = DrawPanel()
    /**
     * the drop down menu for selecting a file
     */
    private val fileBox = JComboBox<String>().apply {
        background = Settings.CONTROL_BACKGROUND
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
            if (fileBox.selectedIndex != 0) {
                val file = File(Settings.FOLDERS[Mode.PAINT.ordinal],  fileBox.selectedItem as String)
                if (file.exists()) {
                    drawPanel.saveMask()
                    val maskFile = Settings.fileToMaskFile(file)
                    val dataFile =
                        File(Settings.PAINT_MASK_FOLDER, "${maskFile.name}.data")
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
                            Settings.showError("Cannot load Mask Data", e2)
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
                    Settings.showError("Cannot load Image, file does not exist")
                }
            }
        }

        zoomText.addActionListener {
            var zoom: Double
            try {
                zoom = zoomText.text.toDouble().let {
                    when {
                        it < 0.01 -> 0.01
                        it > maxZoom -> maxZoom
                        else -> it
                    }
                }
                drawPanel.setZoom(zoom)
            } catch (nfe: NumberFormatException) {
                zoom = zoomSlider.value / 100.0
            }
            zoomText.text = String.format("%.2f", zoom)
            zoomSlider.value = (zoom * 100).toInt()
        }

        zoomSlider.addChangeListener {
            var zoom = zoomSlider.value / 100.0
            if (zoom < 0.01) {
                zoom = 0.01
            } else if (zoom > maxZoom) {
                zoom = maxZoom
            }
            zoomText.text = String.format("%.2f", zoom)
            drawPanel.setZoom(zoom)
        }

        val drawStyleButton = Settings.createButton(Settings.DRAW_STYLE[0]).apply {
            addActionListener {
                drawPanel.toggleStyle()
                icon = Settings.DRAW_STYLE[drawPanel.style]
            }
        }

        val shapeButton = Settings.createButton(Settings.PEN_TYPE[0]).apply {
            addActionListener {
                drawPanel.togglePen()
                icon = Settings.PEN_TYPE[drawPanel.pen]
            }
        }

        val drawModeButton = Settings.createButton(Settings.DRAW_MODE[0]).apply {
            addActionListener {
                drawPanel.toggleDrawMode()
                icon = Settings.DRAW_MODE[drawPanel.drawMode.ordinal]
            }
        }

        val showButton = Settings.createButton("Show").apply {
            background = Settings.ACTIVE
            addActionListener { drawPanel.showAll() }
        }

        val hideButton = Settings.createButton("Hide").apply {
            background = Settings.INACTIVE
            addActionListener { drawPanel.hideAll() }
        }

        val sizeSlider = JSlider(SwingConstants.HORIZONTAL, 10, 100, 25).apply {
            background = Settings.CONTROL_BACKGROUND
            addChangeListener { drawPanel.setRadius(value) }
        }

        add(JPanel().apply {
                background = Settings.CONTROL_BACKGROUND
                layout = BoxLayout(this, BoxLayout.Y_AXIS)
                add(JLabel("Zoom", SwingConstants.LEFT))
                add(zoomText)
                add(zoomSlider)
            },
            BorderLayout.WEST
        )
        add(JPanel().apply {
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
     * sets up the selected folder for painting<br></br>
     * loads it into control and the display
     * @param folder the folder name to load
     */
    private fun setFolder(folder: File) {
        Settings.PAINT_FOLDER = folder
        Settings.PAINT_FOLDER_SIZE = 0
        for (f in Settings.listFilesInOrder(Settings.PAINT_FOLDER!!).filter { obj: File -> obj.isFile }) {
            val fileName = f.name
            val prefix = fileName.substring(0, fileName.lastIndexOf('.'))
            val suffix = fileName.substring(fileName.lastIndexOf('.') + 1)
            try {
                if (suffix.equals("PNG", ignoreCase = true) && prefix.toInt() == Settings.PAINT_FOLDER_SIZE + 1) {
                    Settings.PAINT_FOLDER_SIZE++
                }
            } catch (e: NumberFormatException) {
                if (!fileName.equals("Guide.png", ignoreCase = true)) {
                    println(
                        "File not in correct format: " + fileName +
                                ". Should be named with a number, ex: '" + Settings.PAINT_FOLDER_SIZE + ".png'"
                    )
                }
            }
        }
        Settings.PAINT_IMAGES = BooleanArray(Settings.PAINT_FOLDER_SIZE)
        folderControlPanel.removeAll()
        // creates all buttons
        for (i in 1..Settings.PAINT_FOLDER_SIZE) {
            val button = Settings.createButton(i.toString() + "")
            button.background = Settings.INACTIVE
            button.addActionListener { e ->
                val number = (e.source as JButton).text.toInt()
                if (button.background == Settings.ACTIVE) {
                    button.background = Settings.INACTIVE
                    Settings.PAINT_IMAGES!![number - 1] = false
                } else {
                    button.background = Settings.ACTIVE
                    Settings.PAINT_IMAGES!![number - 1] = true
                }
                val fileLoadingThread: Thread = object : Thread("fileLoadingThread") {
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
                                        val f =
                                            File(Settings.PAINT_FOLDER.toString() + "/" + j + ".png")
                                        g2d.drawImage(ImageIO.read(f), 0, 0, null)
                                    }
                                }
                                g2d.dispose()
                                if (Settings.PAINT_IMAGE != null) {
                                    Main.displayPaint.setMask(drawPanel.mask)
                                    Main.displayPaint.setImageSize()
                                    setZoomMax()
                                }
                            }
                        } catch (error: IOException) {
                            drawPanel.resetImage()
                            Main.displayPaint.resetImage()
                            Settings.PAINT_IMAGE = null
                            Settings.showError("Cannot load Image, file is probably too large", error)
                        } catch (error: OutOfMemoryError) {
                            drawPanel.resetImage()
                            Main.displayPaint.resetImage()
                            Settings.PAINT_IMAGE = null
                            Settings.showError("Cannot load Image, file is probably too large", error)
                        }
                        Main.displayPaint.repaint()
                        drawPanel.repaint()
                        drawPanel.setImageLoading(false)
                    }
                }
                fileLoadingThread.start()
            }
            folderControlPanel.add(button)
        }
        folderControlPanel.revalidate()
        // loads guide
        val guide = File(Settings.PAINT_FOLDER!!.absolutePath + "/Guide.png")
        if (Settings.PAINT_FOLDER != null && Settings.PAINT_FOLDER!!.exists()) {
            drawPanel.setImageLoading(true)
            val folderLoadingThread: Thread = object : Thread("folderLoadingThread") {
                override fun run() {
                    try {
                        Settings.PAINT_IMAGE = null
                        Settings.PAINT_CONTROL_IMAGE = null
                        var imageSize: Dimension
                        run {
                            val guideImg = ImageIO.read(guide)
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
                        Main.displayPaint.setMask(drawPanel.mask)
                        Main.displayPaint.setImageSize()
                        setZoomMax()
                    } catch (error: IOException) {
                        drawPanel.resetImage()
                        Main.displayPaint.resetImage()
                        Settings.PAINT_IMAGE = null
                        Settings.showError("Cannot load Image, file is probably too large", error)
                    } catch (error: OutOfMemoryError) {
                        drawPanel.resetImage()
                        Main.displayPaint.resetImage()
                        Settings.PAINT_IMAGE = null
                        Settings.showError("Cannot load Image, file is probably too large", error)
                    }
                    Main.displayPaint.repaint()
                    drawPanel.repaint()
                    drawPanel.setImageLoading(false)
                }
            }
            folderLoadingThread.start()
        }
    }

    /**
     * sets up the selected file for painting<br></br>
     * loads it into control and the display
     * @param file the file name to load
     */
    private fun setFile(file: File) {
        drawPanel.setImageLoading(true)
        Settings.PAINT_FOLDER = file
        val fileLoadingThread: Thread = object : Thread("fileLoadingThread") {
            override fun run() {
                try {
                    Settings.PAINT_IMAGE = null
                    Settings.PAINT_IMAGE = ImageIO.read(file)
                    Settings.PAINT_CONTROL_IMAGE = Settings.PAINT_IMAGE
                    if (Settings.PAINT_IMAGE != null) {
                        drawPanel.setImage()
                        Main.displayPaint.setMask(drawPanel.mask)
                        Main.displayPaint.setImageSize()
                        setZoomMax()
                    }
                } catch (error: IOException) {
                    drawPanel.resetImage()
                    Main.displayPaint.resetImage()
                    Settings.PAINT_IMAGE = null
                    Settings.showError("Cannot load Image, file is probably too large", error)
                } catch (error: OutOfMemoryError) {
                    drawPanel.resetImage()
                    Main.displayPaint.resetImage()
                    Settings.PAINT_IMAGE = null
                    Settings.showError("Cannot load Image, file is probably too large", error)
                }
                Main.displayPaint.repaint()
                drawPanel.repaint()
                drawPanel.setImageLoading(false)
            }
        }
        fileLoadingThread.start()
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
        val folder = Settings.FOLDERS[Mode.PAINT.ordinal]
        if (folder.exists()) {
            Settings.listFilesInOrder(folder).forEach {
                if (it.isDirectory) {
                    fileBox.addItem(it.name)
                } else {
                    val name = it.name
                    val suffix = name.substring(name.lastIndexOf('.') + 1)
                    if (suffix.equals("PNG", ignoreCase = true)
                        || suffix.equals("JPG", ignoreCase = true)
                        || suffix.equals("JPEG", ignoreCase = true)
                    ) {
                        fileBox.addItem(name)
                    }
                }
            }
        }
    }

    override fun setMainControl(b: Boolean) {}

    /**
     * saves the mask in `DrawPanel` to file
     */
    fun saveMask() {
        drawPanel.saveMask()
    }
}