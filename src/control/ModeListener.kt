package control

import main.Main.changeButton
import main.Mode
import main.Window
import java.awt.event.ActionEvent
import java.awt.event.ActionListener

/**
 * an `ActionListener` class for buttons that change the control or display window
 * @param display the specific display to change to when this button is pressed
 * @param mode the specific mode to change to when this button is pressed
 * @author McAJBen@gmail.com
 * @since 1.0
 */
class ModeListener(
    private val display: Window,
    private val mode: Mode
) : ActionListener {
    override fun actionPerformed(arg0: ActionEvent) {
        changeButton(display, mode)
    }
}