package main

import java.awt.Dimension
import java.awt.GraphicsDevice
import java.awt.Rectangle

/**
 * a container for a name and rectangle of a display
 * @param graphicsDevice the {@code GraphicsDevice} related to the display
 * @author McAJBen@gmail.com
 */
class Screen(graphicsDevice: GraphicsDevice) {
	/**
	 * the dimensions and position of the `Screen`
	 */
	val rectangle: Rectangle = graphicsDevice.defaultConfiguration.bounds

	/**
	 * the `IDString` obtained from the `GraphicsDevice`
	 */
	private val name: String = graphicsDevice.iDstring

	/**
	 * size of the `Screen` just like calling `rectangle.size`
	 */
	val size: Dimension
		get() = rectangle.size

	override fun toString(): String {
		return "$name  ${rectangle.width}x${rectangle.height}"
	}
}