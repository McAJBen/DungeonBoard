package control

import util.*
import java.awt.Color
import java.awt.Dimension
import java.awt.Insets
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import javax.swing.ImageIcon
import javax.swing.JButton
import javax.swing.SwingConstants

/**
 * a container for an image source. To be used in `PicturePanel`
 * @param source the source for the image. This must be in one of the Dungeon Board folders
 * @author McAJBen@gmail.com
 */
class PictureButton(
	private val source: File, private val listener: Listener
) {
	interface Listener {

		/**
		 * called when an image is selected or deselected
		 * @param button the button that was clicked
		 * @param isEnabled the new state of the button
		 */
		fun onChange(button: PictureButton, isEnabled: Boolean)
	}

	companion object {

		/**
		 * The size of the `ImageIcon` in each of the buttons
		 */
		private val IMAGE_ICON_SIZE =
			Dimension(
				100,
				60
			)
	}

	/**
	 * the file used to load the image
	 */
	private val mode = Settings.getFileMode(source) ?: throw IllegalArgumentException("invalid source")

	/**
	 * the thumbnail file for this image
	 */
	private val thumbnailSource =
		File(
			Settings.getDataFolder(mode),
			source.name
		)

	/**
	 * whether the button is currently enabled or disabled
	 */
	private var isEnabled = false

	/**
	 * button that controls whether the image should be displayed
	 */
	val button = JButton(source.nameWithoutExtension).also { button ->
		button.margin =
			Insets(
				0,
				0,
				0,
				0
			)
		button.isFocusPainted = false
		button.verticalTextPosition = SwingConstants.TOP
		button.horizontalTextPosition = SwingConstants.CENTER
		button.background = Colors.DISABLE_COLOR

		button.addActionListener {
			val isEnabled = !isEnabled
			listener.onChange(
				this,
				isEnabled
			)
			setEnabled(isEnabled)
		}
	}

	init {
		if (!thumbnailSource.exists() || source.lastModified() > thumbnailSource.lastModified()) {
			try {
				val bufferedImage = BufferedImage(
					IMAGE_ICON_SIZE.width,
					IMAGE_ICON_SIZE.height,
					BufferedImage.TYPE_INT_RGB
				)
				bufferedImage.graphics.drawImage(
					ImageIO.read(source).getScaledInstance(
						IMAGE_ICON_SIZE.width,
						IMAGE_ICON_SIZE.height,
						BufferedImage.SCALE_SMOOTH
					),
					0,
					0,
					null
				)
				ImageIO.write(
					bufferedImage,
					"GIF",
					thumbnailSource
				)
			} catch (e: Exception) {
				Log.error(
					Labels.CANNOT_CREATE_THUMBNAIL,
					e
				)
			}
		}
	}

	/**
	 * changes the button's visual appearance
	 * @param isEnabled whether the button should be enabled
	 */
	fun setEnabled(isEnabled: Boolean) {
		this.isEnabled = isEnabled

		button.background = when (this.isEnabled) {
			true -> Colors.ENABLE_COLOR
			false -> Colors.DISABLE_COLOR
		}
	}

	/**
	 * loads the thumbnail from file
	 */
	fun load() {
		try {
			button.icon = ImageIcon(ImageIO.read(thumbnailSource))
		} catch (e: Exception) {
			Log.error(
				Labels.CANNOT_LOAD_THUMBNAIL,
				e
			)
		}
	}

	/**
	 * removes the thumbnail from local memory
	 */
	fun unload() {
		button.icon = null
	}

	/**
	 * the `BufferedImage` from the file
	 */
	fun readImage(): BufferedImage {
		return try {
			ImageIO.read(source)
		} catch (e: Exception) {
			Log.error(
				String.format(
					Labels.CANNOT_LOAD_IMAGE,
					source
				),
				e
			)
			Resources.BLANK_CURSOR
		}
	}

	/**
	 * the background color of the image by using the top left corner pixel
	 */
	fun getBackgroundColor(): Color {
		return try {
			Color(
				ImageIO.read(thumbnailSource)
					.getRGB(
						0,
						0
					)
			)
		} catch (e: Exception) {
			Log.error(
				String.format(
					Labels.CANNOT_LOAD_IMAGE_RGB,
					thumbnailSource
				),
				e
			)
			Color.BLACK
		}
	}
}