package display;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

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
		file = new File(folder.getAbsolutePath() + "/" + name);
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

	/**
	 * removes the image from memory to free up space
	 */
	public void forgetImage() {
		image = null;
	}

	/**
	 * reloads the image from file
	 */
	public void rememberImage() {
		try {
			image = ImageIO.read(file);
		} catch (IllegalArgumentException | IOException e) {
			image = null;
			JOptionPane.showMessageDialog(null, "Cannot load Image \"" + name + "\"\n" + e.getMessage());
		}
	}
}