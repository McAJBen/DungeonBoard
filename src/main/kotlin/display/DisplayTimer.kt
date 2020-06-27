package display

import java.awt.Color
import java.awt.Dimension
import java.awt.Font
import java.awt.Graphics2D
import kotlin.math.log10
import kotlin.math.max

/**
 * container for count down timer
 * @param display callback to `DisplayWindow`
 * @param size the size of the `JFrame`
 * @author McAJBen@gmail.com
 */
class DisplayTimer(
	private val display: DisplayWindow, size: Dimension
) {

	companion object {

		/**
		 * width of the timer block
		 */
		private const val TIMER_WIDTH = 220

		/**
		 * width added for extra length timers
		 */
		private const val TIMER_WIDTH_MULTIPLIER = 60

		/**
		 * height of the timer block
		 */
		private const val TIMER_HEIGHT = 90

		/**
		 * the number of seconds to display a completed timer
		 */
		private const val TIMER_DONE_TIME = 60

		/**
		 * the font used for `DisplayTimer`
		 */
		private val FONT =
			Font(
				"TimesRoman",
				Font.BOLD,
				120
			)
	}

	/**
	 * the number of seconds the timer has left
	 */
	private var timerSeconds = -TIMER_DONE_TIME

	/**
	 * the thread that is repainting the `DisplayTimer`
	 * and calculating timers and keeping track of time
	 */
	private var paintThread = Thread()

	/**
	 * the x position of the left side of `DisplayTimer`
	 */
	private val left = (size.width - TIMER_WIDTH) / 2

	/**
	 * the y position of the top side of `DisplayTimer`
	 */
	private val top = size.height - TIMER_HEIGHT

	/**
	 * the y position of the bottom side of `DisplayTimer`
	 */
	private val bottom: Int = size.height - 4

	/**
	 * draws the timer to a `Graphics2D`
	 * @param g2d the `Graphics2D` to paint to
	 */
	fun paint(g2d: Graphics2D) {
		if (timerEnabled()) {
			g2d.font = FONT
			if (timerSeconds <= 0) {
				paintTimer(
					g2d,
					Color.RED,
					0
				)
			} else {
				paintTimer(
					g2d,
					Color.LIGHT_GRAY,
					timerSeconds
				)
			}
		}
	}

	/**
	 * paints the number of seconds to a point on screen
	 * @param g2d the graphics to draw to
	 * @param background the color for the background
	 * @param seconds the number of seconds to display
	 */
	private fun paintTimer(g2d: Graphics2D, background: Color, seconds: Int) {
		val digits =
			max(
				log10(seconds / 60.toDouble()).toInt(),
				0
			)
		val x = left - TIMER_WIDTH_MULTIPLIER * digits / 2
		val w = TIMER_WIDTH + TIMER_WIDTH_MULTIPLIER * digits
		g2d.color = background
		g2d.fillRoundRect(
			x,
			top,
			w,
			TIMER_HEIGHT,
			15,
			15
		)
		g2d.color = Color.BLACK
		g2d.drawString(
			String.format(
				"%d:%02d",
				(seconds / 60),
				seconds % 60
			),
			x,
			bottom
		)
	}

	/**
	 * tells if the timer is currently on
	 * @return true if the timer is counting down or at 0
	 */
	fun timerEnabled(): Boolean {
		return timerSeconds > -TIMER_DONE_TIME
	}

	/**
	 * enables a timer for a set amount of seconds
	 * @param seconds number of seconds for the timer to count down
	 */
	fun setTimer(seconds: Int) {
		clearTimer()
		timerSeconds = seconds + 1
		paintThread = object: Thread("paintThread") {
			override fun run() {
				while (timerEnabled()) {
					try {
						timerSeconds--
						display.repaint()
						sleep(1000)
					} catch (e: InterruptedException) {
						break
					}
				}
			}
		}
		paintThread.start()
	}

	/**
	 * removes the timer from being displayed
	 */
	fun clearTimer() {
		timerSeconds = -TIMER_DONE_TIME
		paintThread.interrupt()
		try {
			paintThread.join()
		} catch (e1: InterruptedException) {
			e1.printStackTrace()
		}
		display.repaint()
	}
}