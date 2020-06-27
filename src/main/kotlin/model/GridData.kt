package model

import kotlinx.serialization.Serializable
import util.Colors
import java.awt.Color
import java.awt.Dimension
import java.awt.Point

/**
 * Data type for storing values needed to display a grid
 * @param squareSize the size of each grid in pixels
 * @param lineWidth the width of each line in pixels
 * @param offset the number of pixels to shift the grid based on the top left of the screen
 * @param color the color to use for each grid line
 * @author McAJBen@gmail.com
 */
@Serializable
data class GridData(
	@Serializable(with = DimensionSerializer::class) var squareSize: Dimension = Dimension(
		100,
		100
	), var lineWidth: Int = 4, @Serializable(with = PointSerializer::class) var offset: Point = Point(
		0,
		0
	), @Serializable(with = RGBAColorSerializer::class) var color: Color = Colors.TRANSPARENT_GREY
) {

	/**
	 * changes the color's red value
	 */
	fun changeRed(red: Int) {
		color = Color(
			red,
			color.green,
			color.blue,
			color.alpha
		)
	}

	/**
	 * changes the color's green value
	 */
	fun changeGreen(green: Int) {
		color = Color(
			color.red,
			green,
			color.blue,
			color.alpha
		)
	}

	/**
	 * changes the color's blue value
	 */
	fun changeBlue(blue: Int) {
		color = Color(
			color.red,
			color.green,
			blue,
			color.alpha
		)
	}

	/**
	 * changes the color's alpha value
	 */
	fun changeAlpha(alpha: Int) {
		color = Color(
			color.red,
			color.green,
			color.blue,
			alpha
		)
	}
}