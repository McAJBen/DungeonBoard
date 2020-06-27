package paint

/**
 * a listener for events coming from `DrawPanel`
 * @author McAJBen@gmail.com
 */
interface DrawPanelListener {

	/**
	 * the reference to paint data, used to update controlMask and MaskGraphics
	 */
	var paintRef: PaintReference?

	/**
	 * called when the mask in `DrawPanel` has changed
	 */
	fun maskHasChanged()

	/**
	 * called when the `DisplayPaint` should be repainted
	 */
	fun repaintDisplay()
}
