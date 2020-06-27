package util

import java.awt.Color

/**
 * contains colors
 * @author McAJBen@gmail.com
 */
object Colors {

	/**
	 * the color of an active button - (153, 255, 187)
	 */
	val ACTIVE =
		Color(
			153,
			255,
			187
		)

	/**
	 * the color of an inactive button - (255, 128, 128)
	 */
	val INACTIVE =
		Color(
			255,
			128,
			128
		)

	/**
	 * the color of an enabled button - Color.GREEN
	 */
	val ENABLE_COLOR =
		Color(
			0,
			255,
			0
		)

	/**
	 * the color of an disabled button - Color.GRAY
	 */
	val DISABLE_COLOR =
		Color(
			128,
			128,
			128
		)

	/**
	 * the green color used on `DrawPanel` to show a visible part of the map
	 */
	val CLEAR =
		Color(
			100,
			255,
			100,
			153
		)

	/**
	 * the red color used on `DrawPanel` to show a blocked part of the map
	 */
	val OPAQUE =
		Color(
			255,
			100,
			100,
			153
		)

	/**
	 * the clear color used on `DisplayPaint` to show through the mask
	 */
	val TRANSPARENT =
		Color(
			0,
			0,
			0,
			0
		)

	/**
	 * the black color used on `DisplayPaint` to hide with the mask
	 */
	val BLACK =
		Color(
			0,
			0,
			0
		)

	/**
	 * the grey color used by default for `GridMenu`
	 */
	val TRANSPARENT_GREY =
		Color(
			128,
			128,
			128,
			192
		)

	/**
	 * the pink color used on `DrawPanel` to show the cursor and outline of player's view
	 */
	val PINK =
		Color(
			255,
			0,
			255
		)

	/**
	 * the semi-transparent pink color used on `DrawPanel`
	 */
	val PINK_CLEAR =
		Color(
			255,
			0,
			255,
			25
		)

	/**
	 * the background color used through Dungeon Board - (153, 153, 153)
	 */
	val BACKGROUND =
		Color(
			153,
			153,
			153
		)

	/**
	 * the background for user controls like `JButtons` and `JComboBoxes` - (200, 200, 200)
	 */
	val CONTROL_BACKGROUND =
		Color(
			200,
			200,
			200
		)
}