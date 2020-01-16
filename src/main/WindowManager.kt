package main

import control.*
import display.DisplayLoading
import display.DisplayPaint
import display.DisplayPictures
import display.DisplayWindow
import util.Settings
import java.awt.Rectangle

/**
 * the main control for windows when the app is open
 * @param displayScreen dimensions for the display screen
 * @param controlScreen dimensions for the control screen
 * @author McAJBen@gmail.com
 * @since 2.5
 */
class WindowManager(
    displayScreen: Rectangle,
    controlScreen: Rectangle
) : ControlWindowListener, ControlLoadingListener {

    /**
     * the main window that is shown to players
     */
    private val displayWindow = DisplayWindow(displayScreen)

    /**
     * the main window that is shown to the person controlling everything
     */
    private val controlWindow = ControlWindow(this, controlScreen)

    /**
     * a list of all possible displays to be shown to players
     */
    private val displays = arrayOf(
        DisplayPictures(displayWindow, Settings.getFolder(Mode.LAYER)),
        DisplayPictures(displayWindow, Settings.getFolder(Mode.IMAGE)),
        DisplayPaint(displayWindow),
        DisplayLoading(displayWindow)
    )

    /**
     * a list of all control methods available
     */
    private val controls = arrayOf(
        ControlPictures(
            Settings.getFolder(Mode.LAYER),
            displays[0] as DisplayPictures,
            true
        ),
        ControlPictures(
            Settings.getFolder(Mode.IMAGE),
            displays[1] as DisplayPictures,
            false
        ),
        ControlPaint(
            displays[2] as ControlPaintListener
        ),
        ControlLoading(
            this,
            displays[3] as DisplayLoading
        )
    )

    /**
     * current display mode
     */
    private var displayMode = Mode.LOADING

    /**
     * current control mode
     */
    private var controlMode = Mode.PAINT

    init {
        controlWindow.setButtons(Window.DISPLAY, Mode.LOADING)
        displayWindow.setMode(displays[Mode.LOADING.ordinal])

        controlWindow.setButtons(Window.CONTROL, Mode.PAINT)
        controlWindow.setMode(controls[Mode.PAINT.ordinal])

        displayWindow.isVisible = true
        controlWindow.isVisible = true
    }

    override fun onDisplayChange(mode: Mode) {
        controlWindow.setButtons(Window.DISPLAY, mode)
        displayWindow.setMode(displays[mode.ordinal])
        displayMode = mode
    }

    override fun onControlChange(mode: Mode) {
        controlWindow.setButtons(Window.CONTROL, mode)
        controlWindow.setMode(controls[mode.ordinal])
        controlMode = mode
    }

    override fun onWindowClosing() {
        controls.forEach { it.onClosing() }
    }

    override fun setTimer(seconds: Int) {
        displayWindow.setTimer(seconds)
    }

    override fun clearTimer() {
        displayWindow.clearTimer()
    }
}