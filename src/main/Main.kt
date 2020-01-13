package main

import control.*
import display.*
import java.awt.GraphicsEnvironment
import java.awt.HeadlessException
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.swing.JOptionPane
import javax.swing.UIManager
import javax.swing.UnsupportedLookAndFeelException

/**
 * contains most of the starting code for Dungeon Board
 * @author McAJBen <McAJBen></McAJBen>@gmail.com>
 * @since 1.0
 */
object Main {
    /**
     * gets the `CONTROL_WINDOW`
     * @return the `CONTROL_WINDOW`
     */
    /**
     * `JFrame` for control window
     */
    @JvmStatic
    var control: ControlWindow? = null
        private set
    /**
     * `JPanel` for controlling Layer Utility
     */
    private var CONTROL_LAYER: ControlPictures? = null
    /**
     * `JPanel` for controlling Image Utility
     */
    private var CONTROL_IMAGE: ControlPictures? = null
    /**
     * `JPanel` for controlling Paint Utility
     */
    private var CONTROL_PAINT: ControlPaint? = null
    /**
     * `JPanel` for controlling Loading Utility
     */
    private var CONTROL_LOADING: ControlLoading? = null
    /**
     * the current control mode.
     * Used to tell which button to disable
     */
    private var CONTROL_MODE = Mode.PAINT
    /**
     * gets the `DISPLAY_WINDOW`
     * @return the `DISPLAY_WINDOW`
     */
    /**
     * `JFrame` for display window
     */
    @JvmStatic
    var display: DisplayWindow? = null
        private set
    /**
     * `JPanel` for displaying Layer Utility
     */
    private var DISPLAY_LAYER: DisplayPictures? = null
    /**
     * `JPanel` for displaying Image Utility
     */
    private var DISPLAY_IMAGE: DisplayPictures? = null
    /**
     * `JPanel` for displaying Paint Utility
     */
	@JvmField
	var DISPLAY_PAINT: DisplayPaint? = null
    /**
     * `JPanel` for displaying Loading Utility
     */
	@JvmField
	var DISPLAY_LOADING: DisplayLoading? = null
    /**
     * the current display mode.
     * Used to tell which button to disable
     */
    private var DISPLAY_MODE = Mode.LOADING

    @JvmStatic
    fun main(args: Array<String>) {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel")
            UIManager.put("Button.background", Settings.CONTROL_BACKGROUND)
            UIManager.put("Button.opaque", true)
            UIManager.put("OptionPane.background", Settings.BACKGROUND)
            UIManager.put("Panel.background", Settings.BACKGROUND)
            UIManager.put("Slider.background", Settings.CONTROL_BACKGROUND)
        } catch (e: ClassNotFoundException) {
            Settings.showError("Error - Changing look and feel", e)
        } catch (e: InstantiationException) {
            Settings.showError("Error - Changing look and feel", e)
        } catch (e: IllegalAccessException) {
            Settings.showError("Error - Changing look and feel", e)
        } catch (e: UnsupportedLookAndFeelException) {
            Settings.showError("Error - Changing look and feel", e)
        }
        try {
            Settings.load()
            val screens = screens
            val displayIndex = JOptionPane.showOptionDialog(
                null, "Select Display Window", Settings.NAME,
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null, screens, 0
            )
            if (displayIndex >= 0 && displayIndex < screens.size) {
                val controlIndex = if (displayIndex == 0) screens.size - 1 else 0
                Settings.DISPLAY_SIZE = screens[displayIndex]!!.size
                display = DisplayWindow(
                    screens[displayIndex]!!.rectangle
                )
                control = ControlWindow(
                    screens[controlIndex]!!.rectangle
                )
                DISPLAY_LAYER = DisplayPictures(Settings.FOLDERS[Mode.LAYER.ordinal])
                DISPLAY_IMAGE = DisplayPictures(Settings.FOLDERS[Mode.IMAGE.ordinal])
                DISPLAY_PAINT = DisplayPaint()
                DISPLAY_LOADING = DisplayLoading()
                CONTROL_LAYER =
                    ControlPictures(Settings.FOLDERS[Mode.LAYER.ordinal], DISPLAY_LAYER, true)
                CONTROL_IMAGE =
                    ControlPictures(Settings.FOLDERS[Mode.IMAGE.ordinal], DISPLAY_IMAGE, false)
                CONTROL_PAINT = ControlPaint()
                CONTROL_LOADING = ControlLoading()
                control!!.addWindowListener(object : WindowAdapter() {
                    override fun windowClosing(windowEvent: WindowEvent) {
                        CONTROL_PAINT!!.saveMask()
                    }
                })
                control!!.setButton(Window.CONTROL, Mode.PAINT, true)
                control!!.setButton(Window.DISPLAY, Mode.LOADING, true)
                control!!.setMode(CONTROL_MODE, Mode.IMAGE)
                display!!.setMode(DISPLAY_MODE, Mode.IMAGE)
                synchronized(control!!) {
                    display!!.isVisible = true
                    control!!.isVisible = true
                }
            }
        } catch (e: SecurityException) {
            Settings.showError("Error - Loading resources", e)
        } catch (e: HeadlessException) {
            println("Error - Cannot find any screens\n" + e.message)
        }
    }

    /**
     * @param mode The `Mode` of the `JPanel` that will be returned
     * @return The `JPanel` which contains the controls for the given `Mode`
     */
    @JvmStatic
    fun getControl(mode: Mode?): Control? {
        return when (mode) {
            Mode.IMAGE -> CONTROL_IMAGE
            Mode.LAYER -> CONTROL_LAYER
            Mode.LOADING -> CONTROL_LOADING
            Mode.PAINT -> CONTROL_PAINT
            else -> null
        }
    }

    /**
     * @param mode The `Mode` of the `JPanel` that will be returned
     * @return The `JPanel` which contains the display for the given `Mode`
     */
    @JvmStatic
    fun getDisplay(mode: Mode?): Display? {
        return when (mode) {
            Mode.IMAGE -> DISPLAY_IMAGE
            Mode.LAYER -> DISPLAY_LAYER
            Mode.LOADING -> DISPLAY_LOADING
            Mode.PAINT -> DISPLAY_PAINT
            else -> null
        }
    }

    /**
     *
     * @return an array of all the `Screens` usable by the system
     * @throws HeadlessException if the environment does not support a display
     */
    @get:Throws(HeadlessException::class)
    private val screens: Array<Screen?>
        get() {
            val graphicsDevice =
                GraphicsEnvironment.getLocalGraphicsEnvironment().screenDevices
            val screens = arrayOfNulls<Screen>(graphicsDevice.size)
            for (i in graphicsDevice.indices) {
                screens[i] = Screen(graphicsDevice[i])
            }
            return screens
        }

    /**
     * changes the active button on the control window
     * @param disp the new display to be set
     * @param mode the mode to be set
     */
	@JvmStatic
	fun changeButton(disp: Window?, mode: Mode) {
        when (disp) {
            Window.CONTROL -> synchronized(CONTROL_MODE) {
                if (CONTROL_MODE != mode) {
                    control!!.setButton(disp, CONTROL_MODE, false)
                    control!!.setMode(mode, CONTROL_MODE)
                    CONTROL_MODE = mode
                    control!!.setButton(disp, CONTROL_MODE, true)
                }
            }
            Window.DISPLAY -> synchronized(DISPLAY_MODE) {
                if (DISPLAY_MODE != mode) {
                    control!!.setButton(disp, DISPLAY_MODE, false)
                    display!!.setMode(mode, DISPLAY_MODE)
                    DISPLAY_MODE = mode
                    control!!.setButton(disp, DISPLAY_MODE, true)
                }
            }
        }
    }
}