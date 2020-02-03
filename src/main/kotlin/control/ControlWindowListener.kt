package control

import main.Mode

/**
 * a listener for events coming from `ControlWindow`
 * @author McAJBen@gmail.com
 * @since 2.5
 */
interface ControlWindowListener {

    /**
     * called when a display button has been clicked
     */
    fun onDisplayChange(mode: Mode)

    /**
     * called when a control button has been clicked
     */
    fun onControlChange(mode: Mode)

    /**
     * called right before the window is to be closed
     */
    fun onWindowClosing()
}