package control

import util.Colors
import util.Resources
import util.createButton
import java.awt.BorderLayout
import javax.swing.BorderFactory
import javax.swing.BoxLayout
import javax.swing.JPanel

/**
 * a `JPanel` for all Controls
 * @author McAJBen@gmail.com
 */
abstract class Control: JPanel() {

	companion object {
		private const val serialVersionUID = 7062093943678033069L
	}

	/**
	 * creates a top panel with a refresh button and the correct layout
	 * @return a `JPanel` with a refresh button
	 */
	protected val northPanel: JPanel
		get() {
			val refreshButton = createButton(Resources.ICON_REFRESH).apply {
				addActionListener { load() }
			}

			return emptyNorthPanel.apply {
				add(refreshButton)
				repaint()
			}
		}

	/**
	 * creates a top panel without anything in it
	 * @return a `JPanel`
	 */
	protected val emptyNorthPanel: JPanel
		get() {
			return JPanel().apply {
				background = Colors.CONTROL_BACKGROUND
				layout = BoxLayout(
					this,
					BoxLayout.X_AXIS
				)
				repaint()
			}
		}

	init {
		layout = BorderLayout()
		border = BorderFactory.createLineBorder(
			Colors.BACKGROUND,
			1
		)
	}

	/**
	 * reloads all images from file
	 */
	protected abstract fun load()

	/**
	 * tells the `ControlPanel` if it is the currently displayed control
	 * @param b true if it is displayed, false if it is not
	 */
	abstract fun setMainControl(b: Boolean)

	/**
	 * perform any operations that need to happen before the application is closed
	 */
	abstract fun onClosing()
}