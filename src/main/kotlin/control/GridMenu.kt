package control

import model.GridData
import util.Labels
import util.Settings
import util.createButton
import java.awt.BorderLayout
import java.awt.FlowLayout
import java.awt.Frame
import java.awt.Point
import javax.swing.*

/**
 * Modal Menu for changing grid settings in the paint utility.
 * @param owner parent frame
 * @param originalGridData previous `GridData` for this image or null
 * @param displayListener callback to `DisplayPaint` to tell when to repaint
 * @author McAJBen@gmail.com
 * @since 3.0
 */
class GridMenu(
    owner: Frame,
    private val originalGridData: GridData?,
    private val displayListener: ControlPaintListener
) : JDialog(owner, Settings.APP_TITLE, true) {

    /**
     * structure for storing which `GridData` to use
     */
    private enum class ReturnType {
        NULL, ORIGINAL, NEW
    }

    /**
     * used to tell which `GridData` to use as the result
     * starts as NEW so the parent can assign it to the `PaintReference`
     */
    private var returnType: ReturnType = ReturnType.NEW

    /**
     * The current working `GridData` to be modified as settings change
     */
    private val gridData = originalGridData?.copy() ?: GridData()

    /**
     * control for red, green, blue, alpha color values
     */
    private val redSlider = JSlider(JSlider.HORIZONTAL, 0, 255, gridData.color.red)
    private val greenSlider = JSlider(JSlider.HORIZONTAL, 0, 255, gridData.color.green)
    private val blueSlider = JSlider(JSlider.HORIZONTAL, 0, 255, gridData.color.blue)
    private val alphaSlider = JSlider(JSlider.HORIZONTAL, 0, 255, gridData.color.alpha)

    /**
     * number visual for red, green, blue, alpha color values
     */
    private val redLabel = JLabel(gridData.color.red.toLabel())
    private val greenLabel = JLabel(gridData.color.green.toLabel())
    private val blueLabel = JLabel(gridData.color.blue.toLabel())
    private val alphaLabel = JLabel(gridData.color.alpha.toLabel())

    /**
     * control for the width of each line in the grid
     */
    private val lineWidthSlider = JSlider(JSlider.HORIZONTAL, 1, 20, gridData.lineWidth)

    /**
     * number visual for the width of each line in the grid
     */
    private val lineWidthLabel = JLabel(gridData.lineWidth.toLabel(2))

    /**
     * control for the size of each square in the grid in pixels between each line
     */
    private val squareSizeWidthSlider = JSlider(JSlider.HORIZONTAL, 2, 999, gridData.squareSize.width)
    private val squareSizeHeightSlider = JSlider(JSlider.HORIZONTAL, 2, 999, gridData.squareSize.height)

    /**
     * number visual for the size of each square in the grid in pixels between each line
     */
    private val pixelsPerSquareWidthLabel = JLabel(gridData.squareSize.width.toLabel())
    private val pixelsPerSquareHeightLabel = JLabel(gridData.squareSize.height.toLabel())

    /**
     * control for the number of pixels to shift the grid in x and y directions
     */
    private val offsetXSlider = JSlider(JSlider.HORIZONTAL, 0, gridData.squareSize.width - 1, gridData.offset.x)
    private val offsetYSlider = JSlider(JSlider.HORIZONTAL, 0, gridData.squareSize.height - 1, gridData.offset.y)

    /**
     * number visual for the number of pixels to shift the grid in x and y directions
     */
    private val offsetXLabel = JLabel(gridData.offset.x.toLabel())
    private val offsetYLabel = JLabel(gridData.offset.y.toLabel())

    /**
     * button to confirm changes
     */
    private val okButton = createButton(Labels.OKAY)

    /**
     * button to cancel changes and go back to `originalGridData`
     */
    private val cancelButton = createButton(Labels.CANCEL)

    /**
     * button to remove the `GridData` and not display a grid
     */
    private val removeButton = createButton(Labels.REMOVE_GRID)

    init {
        isResizable = false
        size = Settings.GRID_MODAL_SIZE
        location = Point(
            owner.location.x + (owner.size.width - size.width) / 2,
            owner.location.y + (owner.size.height - size.height) / 2
        )

        redSlider.addChangeListener {
            gridData.changeRed(redSlider.value)
            displayListener.repaint()
            redLabel.text = redSlider.value.toLabel()
        }

        greenSlider.addChangeListener {
            gridData.changeGreen(greenSlider.value)
            displayListener.repaint()
            greenLabel.text = greenSlider.value.toLabel()
        }

        blueSlider.addChangeListener {
            gridData.changeBlue(blueSlider.value)
            displayListener.repaint()
            blueLabel.text = blueSlider.value.toLabel()
        }

        alphaSlider.addChangeListener {
            gridData.changeAlpha(alphaSlider.value)
            displayListener.repaint()
            alphaLabel.text = alphaSlider.value.toLabel()
        }

        lineWidthSlider.addChangeListener {
            gridData.lineWidth = lineWidthSlider.value
            displayListener.repaint()
            lineWidthLabel.text = lineWidthSlider.value.toLabel(2)
        }

        squareSizeWidthSlider.addChangeListener {
            gridData.squareSize.width = squareSizeWidthSlider.value
            displayListener.repaint()
            offsetXSlider.maximum = squareSizeWidthSlider.value - 1
            offsetYSlider.maximum = squareSizeWidthSlider.value - 1
            pixelsPerSquareWidthLabel.text = squareSizeWidthSlider.value.toLabel()
        }

        squareSizeHeightSlider.addChangeListener {
            gridData.squareSize.height = squareSizeHeightSlider.value
            displayListener.repaint()
            offsetXSlider.maximum = squareSizeHeightSlider.value - 1
            offsetYSlider.maximum = squareSizeHeightSlider.value - 1
            pixelsPerSquareHeightLabel.text = squareSizeHeightSlider.value.toLabel()
        }

        offsetXSlider.addChangeListener {
            gridData.offset.x = offsetXSlider.value
            displayListener.repaint()
            offsetXLabel.text = offsetXSlider.value.toLabel()
        }

        offsetYSlider.addChangeListener {
            gridData.offset.y = offsetYSlider.value
            displayListener.repaint()
            offsetYLabel.text = offsetYSlider.value.toLabel()
        }

        okButton.addActionListener {
            returnType = ReturnType.NEW
            dispose()
        }

        cancelButton.addActionListener {
            returnType = ReturnType.ORIGINAL
            dispose()
        }

        removeButton.addActionListener {
            returnType = ReturnType.NULL
            dispose()
        }

        layout = BorderLayout()
        add(
            JPanel().apply {
                layout = BoxLayout(this, BoxLayout.Y_AXIS)
                add(JPanel().apply {
                    add(JLabel(Labels.RED))
                    add(redLabel)
                    add(redSlider)
                })
                add(JPanel().apply {
                    add(JLabel(Labels.GREEN))
                    add(greenLabel)
                    add(greenSlider)
                })
                add(JPanel().apply {
                    add(JLabel(Labels.BLUE))
                    add(blueLabel)
                    add(blueSlider)
                })
                add(JPanel().apply {
                    add(JLabel(Labels.ALPHA))
                    add(alphaLabel)
                    add(alphaSlider)
                })
                add(JPanel().apply {
                    add(JLabel(Labels.LINE_WIDTH))
                    add(lineWidthLabel)
                    add(lineWidthSlider)
                })
                add(JPanel().apply {
                    add(JLabel(Labels.SQUARE_WIDTH))
                    add(pixelsPerSquareWidthLabel)
                    add(squareSizeWidthSlider)
                })
                add(JPanel().apply {
                    add(JLabel(Labels.SQUARE_HEIGHT))
                    add(pixelsPerSquareHeightLabel)
                    add(squareSizeHeightSlider)
                })
                add(JPanel().apply {
                    add(JLabel(Labels.X_OFFSET))
                    add(offsetXLabel)
                    add(offsetXSlider)
                })
                add(JPanel().apply {
                    add(JLabel(Labels.Y_OFFSET))
                    add(offsetYLabel)
                    add(offsetYSlider)
                })
            },
            BorderLayout.NORTH
        )
        add(
            JPanel().apply {
                layout = FlowLayout()
                add(okButton)
                add(cancelButton)
                add(removeButton)
            },
            BorderLayout.SOUTH
        )
        displayListener.repaint()
    }

    /**
     * returns the new `GridData` or null if there is no grid.
     * If called before `isVisible` has been set to true this will return `ReturnType.NEW`'s `GridData`
     */
    fun getResult(): GridData? {
        return when (returnType) {
            ReturnType.NEW -> gridData
            ReturnType.ORIGINAL -> originalGridData
            ReturnType.NULL -> null
        }
    }

    /**
     * converts to a `0` padded string, which by default is of length 3
     */
    private fun Int.toLabel(digits: Int = 3) = toString().padStart(digits, '0')
}