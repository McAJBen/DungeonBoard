package main

import control.*
import display.*
import java.awt.GraphicsEnvironment
import java.awt.HeadlessException
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.swing.JOptionPane
import javax.swing.UIManager

fun main() {
    try {
        UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel")
        UIManager.put("Button.background", Settings.CONTROL_BACKGROUND)
        UIManager.put("Button.opaque", true)
        UIManager.put("OptionPane.background", Settings.BACKGROUND)
        UIManager.put("Panel.background", Settings.BACKGROUND)
        UIManager.put("Slider.background", Settings.CONTROL_BACKGROUND)
    } catch (e: Exception) {
        Settings.showError("Error - Changing look and feel", e)
    }
    try {
        Settings.load()

        val screens = getScreens()

        val displayIndex = JOptionPane.showOptionDialog(
            null, "Select Display Window", Settings.APP_TITLE,
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            screens,
            0
        )
        if (0 <= displayIndex && displayIndex < screens.size) {
            val displayScreen = screens[displayIndex]
            val controlScreen = screens[if (displayIndex == 0) screens.size - 1 else 0]

            Settings.DISPLAY_SIZE = displayScreen.size
            Main.setup(displayScreen, controlScreen)
        }
    } catch (e: SecurityException) {
        Settings.showError("Error - Loading resources", e)
    } catch (e: HeadlessException) {
        println("Error - Cannot find any screens\n" + e.message)
    }
}

/**
 *
 * @return an array of all the `Screens` usable by the system
 * @throws HeadlessException if the environment does not support a display
 */
@Throws(HeadlessException::class)
private fun getScreens(): Array<Screen> {
    return GraphicsEnvironment.getLocalGraphicsEnvironment().screenDevices.map {
        Screen(it)
    }.toTypedArray()
}

/**
 * Contains most of the starting code for Dungeon Board
 * @author McAJBen@gmail.com
 * @since 1.0
 */
object Main {

    lateinit var controlWindow: ControlWindow
        private set
    private lateinit var controlLayer: ControlPictures
    private lateinit var controlImage: ControlPictures
    private lateinit var controlPaint: ControlPaint
    private lateinit var controlLoading: ControlLoading
    private var controlMode = Mode.PAINT

    lateinit var display: DisplayWindow
        internal set
    private lateinit var displayLayer: DisplayPictures
    private lateinit var displayImage: DisplayPictures
    lateinit var displayPaint: DisplayPaint
    lateinit var displayLoading: DisplayLoading
    private var displayMode = Mode.LOADING

    /**
     * sets up the initial screens
     * @param displayScreen dimensions for player visible screen
     * @param controlScreen dimensions for control screen
     */
    internal fun setup(displayScreen: Screen, controlScreen: Screen) {

        display = DisplayWindow(displayScreen.rectangle)
        controlWindow = ControlWindow(controlScreen.rectangle)
        displayLayer = DisplayPictures(Settings.FOLDERS[Mode.LAYER.ordinal])
        displayImage = DisplayPictures(Settings.FOLDERS[Mode.IMAGE.ordinal])
        displayPaint = DisplayPaint()
        displayLoading = DisplayLoading()
        controlLayer = ControlPictures(
            Settings.FOLDERS[Mode.LAYER.ordinal],
            displayLayer,
            true
        )
        controlImage = ControlPictures(
            Settings.FOLDERS[Mode.IMAGE.ordinal],
            displayImage,
            false
        )
        controlPaint = ControlPaint()
        controlLoading = ControlLoading()

        controlWindow.addWindowListener(object : WindowAdapter() {
            override fun windowClosing(windowEvent: WindowEvent) {
                controlPaint.saveMask()
            }
        })
        controlWindow.setButton(Window.CONTROL, Mode.PAINT, true)
        controlWindow.setButton(Window.DISPLAY, Mode.LOADING, true)
        controlWindow.setMode(controlMode, Mode.IMAGE)
        display.setMode(displayMode, Mode.IMAGE)
        synchronized(controlWindow) {
            display.isVisible = true
            controlWindow.isVisible = true
        }
    }

    /**
     * @param mode The `Mode` of the `JPanel` that will be returned
     * @return The `JPanel` which contains the controls for the given `Mode`
     */
    fun getControl(mode: Mode): Control {
        return when (mode) {
            Mode.IMAGE -> controlImage
            Mode.LAYER -> controlLayer
            Mode.LOADING -> controlLoading
            Mode.PAINT -> controlPaint
        }
    }

    /**
     * @param mode The `Mode` of the `JPanel` that will be returned
     * @return The `JPanel` which contains the display for the given `Mode`
     */
    fun getDisplay(mode: Mode?): Display? {
        return when (mode) {
            Mode.IMAGE -> displayImage
            Mode.LAYER -> displayLayer
            Mode.LOADING -> displayLoading
            Mode.PAINT -> displayPaint
            else -> null
        }
    }

    /**
     * changes the active button on the control window
     * @param display the new display to be set
     * @param mode the mode to be set
     */
    fun changeButton(display: Window?, mode: Mode) {
        when (display) {
            Window.CONTROL -> synchronized(controlMode) {
                if (controlMode != mode) {
                    controlWindow.setButton(display, controlMode, false)
                    controlWindow.setMode(mode, controlMode)
                    controlMode = mode
                    controlWindow.setButton(display, controlMode, true)
                }
            }
            Window.DISPLAY -> synchronized(displayMode) {
                if (displayMode != mode) {
                    controlWindow.setButton(display, displayMode, false)
                    this.display.setMode(mode, displayMode)
                    displayMode = mode
                    controlWindow.setButton(display, displayMode, true)
                }
            }
        }
    }
}