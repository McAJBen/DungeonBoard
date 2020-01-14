package control

import main.Main
import main.Main.controlWindow
import main.Main.display
import main.Settings
import java.awt.BorderLayout
import java.awt.Dimension
import java.lang.NumberFormatException
import javax.swing.JLabel
import javax.swing.JOptionPane
import javax.swing.JSlider
import javax.swing.SwingConstants

/**
 * a `Control` for the Loading Utility
 * @author McAJBen@gmail.com
 * @since 1.0
 */
class ControlLoading : Control() {

    companion object {
        private const val serialVersionUID = 5986059033234358609L
    }

    init {
        val upScaleButton = Settings.createButton("Up Scale").apply {
            background = Settings.INACTIVE
            addActionListener {
                if (background === Settings.ACTIVE) {
                    Main.displayLoading.setUpScale(false)
                    background = Settings.INACTIVE
                } else if (background === Settings.INACTIVE) {
                    Main.displayLoading.setUpScale(true)
                    background = Settings.ACTIVE
                }
            }
        }

        val addCubeButton = Settings.createButton("Add Cube").apply {
            addActionListener { Main.displayLoading.addCube() }
        }

        val clearCubesButton = Settings.createButton("Clear Cubes").apply {
            addActionListener { Main.displayLoading.clearCubes() }
        }

        val timeLabel = JLabel("08").apply {
            background = Settings.CONTROL_BACKGROUND
            horizontalAlignment = SwingConstants.CENTER
        }

        val timeSlider = JSlider(SwingConstants.HORIZONTAL, 1, 20, 8).apply {
            background = Settings.CONTROL_BACKGROUND
            minimumSize = Dimension(100, 0)
            addChangeListener {
                timeLabel.text = String.format("%02d", value)
                Main.displayLoading.setTotalWait(value)
            }
        }

        val createTimerButton = Settings.createButton("Create Timer").apply {
            addActionListener {
                val input = JOptionPane.showInputDialog(controlWindow, "Enter minutes or M:SS", "")
                try {
                    val seconds = if (input.contains(":")) {
                        val split = input.split(":".toRegex())
                        split[0].toInt() * 60 + split[1].toInt()
                    } else {
                        input.toInt() * 60
                    }
                    display.setTimer(seconds)
                } catch (e: NumberFormatException) {}
            }
        }

        val clearTimerButton = Settings.createButton("Clear Timer").apply {
            addActionListener { display.clearTimer() }
        }

        add(
            northPanel.apply {
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

}