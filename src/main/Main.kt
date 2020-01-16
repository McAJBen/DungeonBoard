package main

import util.Colors
import util.Labels
import util.Log
import util.Settings
import java.awt.GraphicsEnvironment
import java.awt.HeadlessException
import javax.swing.JOptionPane
import javax.swing.UIManager

fun main() {
    try {
        UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel")
        UIManager.put("Button.background", Colors.CONTROL_BACKGROUND)
        UIManager.put("Button.opaque", true)
        UIManager.put("OptionPane.background", Colors.BACKGROUND)
        UIManager.put("Panel.background", Colors.BACKGROUND)
        UIManager.put("Slider.background", Colors.CONTROL_BACKGROUND)
    } catch (e: Exception) {
        Log.error(Labels.ERROR_CHANGING_LOOK, e)
    }
    try {
        Settings.load()

        val screens = getScreens()

        val displayIndex = JOptionPane.showOptionDialog(
            null, Labels.SELECT_DISPLAY_WINDOW, Settings.APP_TITLE,
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
            WindowManager(displayScreen.rectangle, controlScreen.rectangle)
        }
    } catch (e: SecurityException) {
        Log.error(Labels.ERROR_LOADING_RESOURCES, e)
    } catch (e: HeadlessException) {
        Log.error(Labels.ERROR_FINDING_SCREENS, e)
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