package control

import paint.PaintReference

/**
 * a listener for events coming from `ControlPaint`
 * @author McAJBen@gmail.com
 */
interface ControlPaintListener {

	/**
	 * updates the display with a new `PaintReference`
	 * @param ref the new reference
	 */
	fun setPaintReference(ref: PaintReference)

	/**
	 * tells display to repaint
	 */
	fun repaint()
}