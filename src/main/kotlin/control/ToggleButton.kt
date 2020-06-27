package control

import util.Colors
import util.createButton


/**
 * Toggleable button with enable/disable visual. To be used in `ControlPaint`
 * @param identifier the unique key for this button.
 * @author McAJBen@gmail.com
 * @since 3.0.2
 */
class ToggleButton(
    private val identifier: String,
    private val listener: Listener
) {
    interface Listener {

        /**
         * called when a button is clicked
         * @param identifier the identifier of the button that was clicked
         * @return true if the the button should be enabled, false otherwise
         */
        fun onButtonClicked(identifier: String): Boolean
    }

    /**
     * button that controls whether the image should be displayed
     */
    val button = createButton(identifier).also { button ->
        button.background = Colors.INACTIVE

        button.addActionListener {
            val enabled = listener.onButtonClicked(identifier)
            setEnabled(enabled)
        }
    }

    /**
     * changes the button's visual appearance
     * @param isEnabled whether the button should be enabled
     */
    fun setEnabled(isEnabled: Boolean) {
        button.background = when (isEnabled) {
            true -> Colors.ACTIVE
            false -> Colors.INACTIVE
        }
    }
}