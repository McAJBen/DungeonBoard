package control

import main.Main
import main.Main.getControl
import main.Mode
import main.Settings
import main.Window
import java.awt.BorderLayout
import java.awt.GridLayout
import java.awt.Rectangle
import javax.swing.*

/**
 * a `JFrame` for controlling the whole program
 * @param r the position and dimension of the screen
 * @author McAJBen@gmail.com
 * @since 1.0
 */
class ControlWindow(r: Rectangle) : JFrame() {

    companion object {
        private const val serialVersionUID = -2980231396321368085L
    }

    /**
     * an array of the buttons to change control mode
     */
    private val controlButtons = Mode.values().map { mode ->
        Settings.createButton(mode.name).apply {
            background = Settings.INACTIVE
            addActionListener { Main.changeButton(Window.CONTROL, mode) }
        }
    }
    /**
     * an array of the buttons to change display mode
     */
    private val displayButtons = Mode.values().map { mode ->
        Settings.createButton(mode.name).apply {
            background = Settings.INACTIVE
            addActionListener { Main.changeButton(Window.DISPLAY, mode) }
        }
    }

    init {
        iconImage = Settings.ICON.image
        title = Settings.APP_TITLE
        size = Settings.CONTROL_SIZE
        defaultCloseOperation = EXIT_ON_CLOSE
        setLocation(
            (r.width - Settings.CONTROL_SIZE.width) / 2 + r.x,
            (r.height - Settings.CONTROL_SIZE.height) / 2 + r.y
        )

        add(
            JPanel(GridLayout(1, 2)).apply {
                add(createButtonGroup("Controls", controlButtons))
                add(createButtonGroup("Displaying", displayButtons))
            },
            BorderLayout.NORTH
        )
    }

    /**
     * combines buttons into a menu with a header. This is a mock radio button group
     * @param title the title for the header
     * @param buttons the buttons to place inside the group
     * @return a `JPanel` which is formatted for a mock radio button group
     */
    private fun createButtonGroup(title: String, buttons: List<JButton>): JPanel {
        val buttonPanel = JPanel().apply {
            background = Settings.CONTROL_BACKGROUND
            layout = GridLayout(1, buttons.size)
            for (i in buttons.indices) {
                add(buttons[i])
            }
        }

        return JPanel().apply {
            background = Settings.CONTROL_BACKGROUND
            layout = GridLayout(2, 1)
            border = BorderFactory.createLineBorder(Settings.BACKGROUND, 2)
            add(JLabel(title, SwingConstants.CENTER))
            add(buttonPanel)
        }
    }

    /**
     * changes the mode of control being displayed
     * @param newMode the new mode to display
     * @param oldMode the old mode displayed before
     */
    fun setMode(newMode: Mode, oldMode: Mode) {
        remove(getControl(oldMode))
        getControl(oldMode).setMainControl(false)
        add(getControl(newMode), BorderLayout.CENTER)
        getControl(newMode).setMainControl(true)
        validate()
        repaint()
    }

    /**
     * changes the background of a button
     * @param display whether it is a display or a control button
     * @param mode the button mode
     * @param value true for active, false for inactive
     */
    fun setButton(display: Window, mode: Mode, value: Boolean) {
        when (display) {
            Window.CONTROL -> controlButtons
            Window.DISPLAY -> displayButtons
        }[mode.ordinal].background = if (value) Settings.ACTIVE else Settings.INACTIVE
    }
}