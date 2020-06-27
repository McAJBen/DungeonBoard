package display

import java.awt.Graphics2D
import javax.swing.JPanel

/**
 * a `JPanel` for all Displays
 * @param window callback to `DisplayWindow`
 * @author McAJBen@gmail.com
 */
abstract class Display(
	private val window: DisplayWindow
): JPanel() {

	companion object {
		private const val serialVersionUID = 3464890244015717841L
	}

	/**
	 * paints the mouse over the screen
	 * @param g2d the graphics to paint onto
	 */
	protected fun paintMouse(g2d: Graphics2D) {
		window.paintDisplay(g2d)
	}

	/**
	 * tells the Display if it is visible
	 * @param b
	 * - true if it is now the main display
	 * - false if it is not the main display
	 */
	open fun setMainDisplay(b: Boolean) {}
}