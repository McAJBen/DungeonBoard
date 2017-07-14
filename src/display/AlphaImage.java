package display;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import main.Settings;

/**
 * a container for name and an image
 * @author McAJBen <McAJBen@gmail.com>
 * @since 1.6
 */
public class AlphaImage {
	
	/**
	 * the name of the file that is loaded
	 */
	private String name;
	
	/**
	 * the file used to load the image
	 */
	private File file;
	
	/**
	 * creates an instance of {@code AlphaImage}
	 * @param folder the folder that contains the file named n
	 * @param n the name of the specific file
	 */
	public AlphaImage(File folder, String n) {
		name = n;
		file = new File(folder.getAbsolutePath() + File.separator + name);
	}
	
	/**
	 * gets the name of the file
	 * @return a {@code String} for the file name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * gets the Image
	 * @return a {@code BufferedImage} with the file name
	 */
	public BufferedImage getImage() {
		try {
			return ImageIO.read(file);
		} catch (IllegalArgumentException | IOException e) {
			Settings.showError("Cannot load Image \"" + name, e);
			return null;
		}
	}
	
	/**
	 * gets the background color of the image by using the top left corner pixel
	 * @return the color of the top left corner of the image
	 */
	public Color getBGColor() {
		File f = Settings.fileToThumb(file);
		try {
			return new Color(ImageIO.read(f).getRGB(0, 0));
		} catch (IllegalArgumentException | IOException e) {
			Settings.showError("Cannot load Image RGB \"" + name, e);
		}
		return Color.BLACK;
	}
}