package display;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class AlphaImage {
	
	private BufferedImage image;
	private String name;
	
	public AlphaImage(File folder, String n) {
		name = n;
		String file = folder.getAbsolutePath() + "/" + name;
		try {
			image = ImageIO.read(new File(file));
		} catch (Exception e) {
			image = null;
		}
	}
	
	public String getName() {
		return name;
	}
	
	public BufferedImage getImage() {
		return image;
	}
	
	public int getWidth() {
		return image.getWidth();
	}
	
	public int getHeight() {
		return image.getHeight();
	}
	
	public Color getBGColor() {
		return new Color(image.getRGB(0, 0));
	}
	
	
}