package display

import util.Resources
import java.awt.Graphics2D
import java.awt.Point
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import java.awt.event.MouseMotionListener

/**
 * container for hand cursor
 * @param display callback to `DisplayWindow`
 * @author McAJBen@gmail.com
 */
class DisplayCursor(
	private val display: DisplayWindow
): MouseListener, MouseMotionListener {

	companion object {

		/**
		 * the offsets used to display the cursor hand
		 */
		private val HANDS_OFFSET =
			intArrayOf(
				-5,
				-100,
				-45,
				0
			)

		/**
		 * the position a cursor is placed when not on screen
		 */
		private val NULL_POS =
			Point(
				Int.MIN_VALUE,
				Int.MIN_VALUE
			)
	}

	/**
	 * the position that the mouse is on the screen
	 * used to place the hand cursor
	 */
	private var mousePos = NULL_POS

	/**
	 * the direction that the hand cursor is facing
	 */
	private var handDirection = Direction.UP

	/**
	 * changes the position of the mouse
	 * @param p the new position of the mouse
	 */
	private fun setMouse(p: Point) {
		mousePos = p
		display.repaint()
	}

	/**
	 * paints the hand cursor to the screen
	 * @param g2d the graphics to paint to
	 */
	fun paint(g2d: Graphics2D) {
		val i = handDirection.ordinal
		g2d.drawImage(
			Resources.HANDS[i].image,
			mousePos.x + HANDS_OFFSET[i],
			mousePos.y + HANDS_OFFSET[if (i == 0) 3 else i - 1],
			null
		)
	}

	override fun mouseReleased(e: MouseEvent) {}

	override fun mousePressed(e: MouseEvent) {
		handDirection = Direction.values()[(handDirection.ordinal + 1) % Direction.values().size]
		display.repaint()
	}

	override fun mouseClicked(e: MouseEvent) {}

	override fun mouseExited(e: MouseEvent) {
		setMouse(NULL_POS)
	}

	override fun mouseEntered(e: MouseEvent) {
		setMouse(e.point)
	}

	override fun mouseDragged(e: MouseEvent) {
		setMouse(e.point)
	}

	override fun mouseMoved(e: MouseEvent) {
		setMouse(e.point)
	}
}