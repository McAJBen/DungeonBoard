package control

import display.DisplayLoading
import util.Colors
import util.Labels
import util.Log
import util.createButton
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.JLabel
import javax.swing.JOptionPane
import javax.swing.JSlider
import javax.swing.SwingConstants

/**
 * a `Control` for the Loading Utility
 * @param listener callback to `WindowManager`
 * @param displayLoading callback to `DisplayLoader`
 * @author McAJBen@gmail.com
 */
class ControlLoading(
	private val listener: ControlLoadingListener, private val displayLoading: DisplayLoading
): Control() {

	companion object {
		private const val serialVersionUID = 5986059033234358609L
	}

	init {
		val upScaleButton = createButton(Labels.UP_SCALE).apply {
			background = Colors.INACTIVE
			addActionListener {
				when (background) {
					Colors.ACTIVE -> {
						displayLoading.setUpScale(false)
						background = Colors.INACTIVE
					}
					Colors.INACTIVE -> {
						displayLoading.setUpScale(true)
						background = Colors.ACTIVE
					}
				}
			}
		}

		val addCubeButton = createButton(Labels.ADD_CUBE).apply {
			addActionListener { displayLoading.addCube() }
		}

		val clearCubesButton = createButton(Labels.CLEAR_CUBES).apply {
			addActionListener { displayLoading.clearCubes() }
		}

		val timeLabel = JLabel("08").apply {
			background = Colors.CONTROL_BACKGROUND
			horizontalAlignment = SwingConstants.CENTER
		}

		val timeSlider = JSlider(
			SwingConstants.HORIZONTAL,
			1,
			20,
			8
		).apply {
			background = Colors.CONTROL_BACKGROUND
			minimumSize = Dimension(
				100,
				0
			)
			addChangeListener {
				timeLabel.text = String.format(
					"%02d",
					value
				)
				displayLoading.setTotalWait(value)
			}
		}

		val createTimerButton = createButton(Labels.CREATE_TIMER).apply {
			addActionListener {
				val input = JOptionPane.showInputDialog(
					this,
					Labels.ENTER_MINUTES_OR_SECONDS,
					""
				)

				try {
					val seconds = if (input.matches("[0-9]*:[0-9]*".toRegex())) {
						val colon = input.indexOf(':')
						input.substring(
							0,
							colon
						).toInt() * 60 + input.substring(colon + 1).toInt()
					} else {
						input.toInt() * 60
					}
					listener.setTimer(seconds)
				} catch (e: NumberFormatException) {
					Log.error(Labels.INVALID_TIME_FORMAT)
				}
			}
		}

		val clearTimerButton = createButton(Labels.CLEAR_TIMER).apply {
			addActionListener { listener.clearTimer() }
		}

		add(
			emptyNorthPanel.apply {
				add(upScaleButton)
				add(addCubeButton)
				add(clearCubesButton)
				add(timeLabel)
				add(timeSlider)
				add(createTimerButton)
				add(clearTimerButton)
			},
			BorderLayout.NORTH
		)
		isVisible = true
	}

	override fun load() {}

	override fun setMainControl(b: Boolean) {}

	override fun onClosing() {}
}