package display;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

/**
 * a container for name and an image
 * @author McAJBen <McAJBen@gmail.com>
 * @since 1.6
 */
public class AlphaImage {
	
	/**
	 * the image loaded from file
	 */
	private BufferedImage image;
	
	/**
	 * the name of the file that is loaded
	 */
	private String name;
	
	/**
	 * creates an instance of {@code AlphaImage}
	 * @param folder the folder that contains the file named n
	 * @param n the name of the specific file
	 */
	public AlphaImage(File folder, String n) {
		name = n;
		String file = folder.getAbsolutePath() + "/" + name;
		try {
			image = ImageIO.read(new File(file));
		} catch (Exception e) {
			image = null;
		}
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
		return image;
	}
	
	/**
	 * gets the width of the image
	 * @return the same as {@code image.getWidth()}
	 */
	public int getWidth() {
		return image.getWidth();
	}

	/**
	 * gets the height of the image
	 * @return the same as {@code image.getHeight()}
	 */
	public int getHeight() {
		return image.getHeight();
	}
	
	/**
	 * gets the background color of the image by using the top left corner pixel
	 * @return the color of the top left corner of the image
	 */
	public Color getBGColor() {
		return new Color(image.getRGB(0, 0));
	}
}