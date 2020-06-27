package paint

import main.Mode
import model.PaintData
import util.Colors
import util.Labels
import util.Log
import util.Settings
import util.Settings.DISPLAY_SIZE
import util.Settings.PIXELS_PER_MASK
import java.awt.AlphaComposite
import java.awt.Graphics2D
import java.awt.Point
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import javax.imageio.ImageIO
import kotlin.math.roundToInt

/**
 * holds data on a paint resource
 * @author McAJBen@gmail.com
 * @param source the picture file or folder within the paint folder to use
 */
abstract class PaintReference(internal val source: File) {

	companion object {

		/**
		 * generates a new mask file of a given size
		 * @param width width of mask
		 * @param height height of mask
		 * @return a mask that is filled with `Colors.OPAQUE`
		 */
		private fun createControlMask(width: Int, height: Int): BufferedImage {
			return BufferedImage(
				width,
				height,
				BufferedImage.TYPE_INT_ARGB
			).apply {
				createGraphics().apply {
					composite = AlphaComposite.getInstance(AlphaComposite.SRC)
					setRenderingHint(
						RenderingHints.KEY_RENDERING,
						RenderingHints.VALUE_RENDER_SPEED
					)
					paint = Colors.OPAQUE
					fillRect(
						0,
						0,
						width,
						height
					)
				}
			}
		}
	}

	/**
	 * the data file that contains the paint mask
	 */
	private val maskFile = source.toMaskFile()

	/**
	 * the data file that contains `PaintData` information
	 */
	private val dataFile = source.toDataFile()

	/**
	 * the background image to display on the `DrawPanel`
	 */
	internal lateinit var controlImage: BufferedImage

	/**
	 * the image to display on `DisplayPaint`
	 */
	internal lateinit var displayImage: BufferedImage

	/**
	 * the current mask for `DrawPanel`
	 */
	internal val controlMask: BufferedImage

	/**
	 * the current mask for `DisplayPaint`
	 */
	internal val displayMask: BufferedImage

	/**
	 * a graphics instance for `controlMask`, used by `DrawPanel`
	 */
	internal val maskGraphics: Graphics2D

	/**
	 * the window offset for displaying an image on `DisplayPaint`
	 */
	internal var windowOffset =
		Point(
			0,
			0
		)

	/**
	 * paint data that will be saved between instances
	 */
	internal var paintData: PaintData

	init {
		@Suppress("LeakingThis") loadImages()

		paintData = if (dataFile.exists()) {
			try {
				PaintData.read(dataFile)
			} catch (e2: Exception) {
				Log.error(
					Labels.CANNOT_LOAD_MASK_DATA,
					e2
				)
				PaintData()
			}
		} else {
			PaintData()
		}
		updateWindow()

		controlMask = if (maskFile.exists() && maskFile.lastModified() > source.lastModified()) {
			try {
				ImageIO.read(maskFile)
			} catch (e: IOException) {
				Log.error(
					Labels.CANNOT_LOAD_MASK_DATA,
					e
				)
				createControlMask(
					controlImage.width / PIXELS_PER_MASK,
					controlImage.height / PIXELS_PER_MASK
				)
			}
		} else {
			createControlMask(
				controlImage.width / PIXELS_PER_MASK,
				controlImage.height / PIXELS_PER_MASK
			)
		}

		displayMask = BufferedImage(
			controlMask.width,
			controlMask.height,
			BufferedImage.TYPE_INT_ARGB
		)

		maskGraphics = controlMask.createGraphics().apply {
			composite =
				AlphaComposite.getInstance(
					AlphaComposite.SRC,
					1.0f
				)
			setRenderingHint(
				RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_SPEED
			)
		}
	}

	/**
	 * sets the `displayZoom`, moves the display window and changes the window position based on edges
	 * @param zoom the number of pixels in the image per the pixel on the output display.
	 * - a higher number will zoom out
	 * - a lower number will zoom in
	 */
	fun setDisplayZoom(zoom: Double) {
		paintData.displayZoom = zoom
		updateWindow()
	}

	/**
	 * changes the center position of the window
	 * @param center the center point of the display, according to `controlMask`
	 */
	fun setWindowPosition(center: Point) {
		paintData.windowCenter = center
		updateWindow()
	}

	/**
	 * saves the current `controlMask` to the `maskFile` and the data to `dataFile`
	 */
	fun save() {
		try {
			ImageIO.write(
				controlMask,
				"png",
				maskFile
			)
			PaintData.write(
				dataFile,
				paintData
			)
		} catch (e: IOException) {
			Log.error(
				Labels.CANNOT_SAVE_MASK,
				e
			)
		}
	}

	/**
	 * updates the `windowOffset`, used when a change is made to `windowCenter` or `displayZoom`
	 */
	private fun updateWindow() {
		val x =
			paintData.windowCenter.x * PIXELS_PER_MASK - (DISPLAY_SIZE.width * paintData.displayZoom / 2).roundToInt()
		val y =
			paintData.windowCenter.y * PIXELS_PER_MASK - (DISPLAY_SIZE.height * paintData.displayZoom / 2).roundToInt()
		val xMax = displayImage.width - (DISPLAY_SIZE.width * paintData.displayZoom).roundToInt()
		val yMax = displayImage.height - (DISPLAY_SIZE.height * paintData.displayZoom).roundToInt()

		windowOffset = Point(
			x.boundTo(
				0,
				xMax
			),
			y.boundTo(
				0,
				yMax
			)
		)
	}

	/**
	 * converts a file to the matching mask file
	 */
	private fun File.toMaskFile(): File {
		val filename = if (isDirectory) {
			"$name.f"
		} else {
			name
		}
		return File(
			Settings.getDataFolder(Mode.PAINT),
			filename
		)
	}

	/**
	 * converts a file to the matching data file
	 */
	private fun File.toDataFile(): File {
		return File(
			Settings.getDataFolder(Mode.PAINT),
			"${name}.data"
		)
	}

	/**
	 * ensures that a value is between min (inclusive) and max (inclusive)
	 */
	private fun Int.boundTo(min: Int, max: Int): Int {
		return when {
			this > max -> max
			this < min -> min
			else -> this
		}
	}

	/**
	 * function to load `controlImage` and `displayImage`
	 */
	internal abstract fun loadImages()

	/**
	 * function to update the `displayMask` based on `controlMask`
	 */
	internal abstract fun updateDisplayMask()
}