package control

import main.Mode
import main.Window
import util.Colors
import util.Resources
import util.Settings
import util.createButton
import java.awt.BorderLayout
import java.awt.GridLayout
import java.awt.Rectangle
import java.awt.event.WindowEvent
import java.awt.event.WindowListener
import javax.swing.*

/**
 * a `JFrame` for controlling the whole program
 * @param listener callback to `WindowManager`
 * @param r the position and dimension of the screen
 * @author McAJBen@gmail.com
 */
class ControlWindow(
	private val listener: ControlWindowListener, r: Rectangle
): JFrame(), WindowListener {

	companion object {
		private const val serialVersionUID = -2980231396321368085L
	}

	/**
	 * an array of the buttons to change control mode
	 */
	private val controlButtons = Mode.values().map { mode ->
		createButton(mode.name).apply {
			background = Colors.INACTIVE
			addActionListener { listener.onControlChange(mode) }
		}
	}

	/**
	 * an array of the buttons to change display mode
	 */
	private val displayButtons = Mode.values().map { mode ->
		createButton(mode.name).apply {
			background = Colors.INACTIVE
			addActionListener { listener.onDisplayChange(mode) }
		}
	}

	/**
	 * the currently active control's panel
	 */
	private var controlPanel: Control? = null

	init {
		iconImage = Resources.ICON.image
		title = Settings.APP_TITLE
		size = Settings.CONTROL_SIZE
		defaultCloseOperation = EXIT_ON_CLOSE

		setLocation(
			(r.width - Settings.CONTROL_SIZE.width) / 2 + r.x,
			(r.height - Settings.CONTROL_SIZE.height) / 2 + r.y
		)

		add(
			JPanel(
				GridLayout(
					1,
					2
				)
			).apply {
				add(
					createButtonGroup(
						"Controls",
						controlButtons
					)
				)
				add(
					createButtonGroup(
						"Displaying",
						displayButtons
					)
				)
			},
			BorderLayout.NORTH
		)

		addWindowListener(this)
	}

	/**
	 * combines buttons into a menu with a header. This is a mock radio button group
	 * @param title the title for the header
	 * @param buttons the buttons to place inside the group
	 * @return a `JPanel` which is formatted for a mock radio button group
	 */
	private fun createButtonGroup(title: String, buttons: List<JButton>): JPanel {
		val buttonPanel = JPanel().apply {
			background = Colors.CONTROL_BACKGROUND
			layout =
				GridLayout(
					1,
					buttons.size
				)
			for (i in buttons.indices) {
				add(buttons[i])
			}
		}

		return JPanel().apply {
			background = Colors.CONTROL_BACKGROUND
			layout =
				GridLayout(
					2,
					1
				)
			border =
				BorderFactory.createLineBorder(
					Colors.BACKGROUND,
					2
				)
			add(
				JLabel(
					title,
					SwingConstants.CENTER
				)
			)
			add(buttonPanel)
		}
	}

	/**
	 * changes out the visible control panel
	 * @param control a `Control` panel to show
	 */
	fun setMode(control: Control) {
		object: Thread() {
			override fun run() {
				synchronized(this) {
					if (controlPanel != null) {
						remove(controlPanel)
						controlPanel!!.setMainControl(false)
					}
					add(
						control,
						BorderLayout.CENTER
					)
					control.setMainControl(true)
					validate()
					repaint()
					controlPanel = control
				}
			}
		}.start()
	}

	/**
	 * changes the background of buttons
	 * @param display whether it is a display or a control button
	 * @param mode the button mode
	 */
	fun setButtons(display: Window, mode: Mode) {
		when (display) {
			Window.CONTROL -> controlButtons
			Window.DISPLAY -> displayButtons
		}.forEachIndexed { index, button ->
			button.background = if (index == mode.ordinal) {
				Colors.ACTIVE
			} else {
				Colors.INACTIVE
			}
		}
	}

	override fun windowDeiconified(e: WindowEvent?) {}

	override fun windowClosing(e: WindowEvent) {
		listener.onWindowClosing()
	}

	override fun windowClosed(e: WindowEvent?) {}

	override fun windowActivated(e: WindowEvent?) {}

	override fun windowDeactivated(e: WindowEvent?) {}

	override fun windowOpened(e: WindowEvent?) {}

	override fun windowIconified(e: WindowEvent?) {}
}