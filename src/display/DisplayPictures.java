package display;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.LinkedList;

import main.Settings;

/**
 * {@code JPanel} for displaying Image and Layer Utility
 * @author McAJBen <McAJBen@gmail.com>
 * @since 2.0
 */
public class DisplayPictures extends Display {

	private static final long serialVersionUID = 350995921778402576L;
	
	/**
	 * a list of the images to be painted
	 */
	private LinkedList<AlphaImage> images;
	
	/**
	 * the method of scale to show the image<br>
	 */
	private Scale scaleMode;
	
	/**
	 * tells if the image should be flipped<br>
	 * - true will rotate the image by 180 degrees before displaying<br>
	 * - false will display normally
	 */
	private boolean flip;
	
	/**
	 * the folder to gather images from
	 */
	private final File folder;
	
	/**
	 * creates an instance of {@code DisplayPictures}
	 * @param folder the folder that contains images
	 */
	public DisplayPictures(File folder) {
		this.folder = folder;
		
		images = new LinkedList<>();
		scaleMode = Scale.FILL;
		flip = false;
		
		setVisible(true);
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2d = (Graphics2D) g;
		
		if (images.isEmpty()) {
			fillBackground(g2d, Color.BLACK);
		}
		else {
			fillBackground(g2d, images.getFirst().getBGColor());
		}
		
		for (AlphaImage image: images) {
			paintImage(g2d, image);
		}
		
		paintMouse(g2d);
		g2d.dispose();
	}
	
	/**
	 * paints an image to the screen
	 * @param g2d the graphics to draw onto
	 * @param aImage the image to draw
	 */
	public void paintImage(Graphics2D g2d, AlphaImage aImage) {
		
		switch (scaleMode) {
		case FILL:
			drawImage(g2d, aImage.getImage(), 0, 0, Settings.DISPLAY_SIZE.width, Settings.DISPLAY_SIZE.height);
			break;
		case REAL_SIZE:
			drawImage(g2d, aImage.getImage(),
					(Settings.DISPLAY_SIZE.width - aImage.getWidth()) / 2,
					(Settings.DISPLAY_SIZE.height - aImage.getHeight()) / 2,
					aImage.getWidth(),
					aImage.getHeight());
			break;
		case UP_SCALE:
			double screenRatio = Settings.DISPLAY_SIZE.getWidth() / Settings.DISPLAY_SIZE.getHeight();
			double imageRatio = (double)aImage.getWidth() / aImage.getHeight();
			Dimension imageScale;
			if (imageRatio > screenRatio) {
				// width > height
				imageScale = new Dimension(Settings.DISPLAY_SIZE.width, (int) (Settings.DISPLAY_SIZE.width / imageRatio));
			}
			else {
				// width < height
				imageScale = new Dimension((int) (Settings.DISPLAY_SIZE.height * imageRatio), Settings.DISPLAY_SIZE.height);
			}
			drawImage(g2d, aImage.getImage(),
					(Settings.DISPLAY_SIZE.width - imageScale.width) / 2,
					(Settings.DISPLAY_SIZE.height - imageScale.height) / 2,
					imageScale.width,
					imageScale.height);
			break;
		}
	}
	
	/**
	 * fills the whole background with a color
	 * @param g2d the graphics to draw to
	 * @param c the color to draw
	 */
	private void fillBackground(Graphics2D g2d, Color c) {
		g2d.setColor(c);
		g2d.fillRect(0, 0, Settings.DISPLAY_SIZE.width, Settings.DISPLAY_SIZE.height);
	}
	
	/**
	 * draws an image to the graphics with the given position and dimensions
	 * @param g2d the graphics to draw to
	 * @param img the image to draw
	 * @param x the x position of the top left corner
	 * @param y the y position of the top left corner
	 * @param w the width of the image
	 * @param h the height of the image
	 */
	private void drawImage(Graphics2D g2d, BufferedImage img, int x, int y, int w, int h) {
		if (flip) {
			AffineTransform oldAT = g2d.getTransform();
			AffineTransform at = new AffineTransform();
			at.rotate(Math.PI, getWidth() / 2, getHeight() / 2);
			g2d.setTransform(at);
			g2d.drawImage(img, x, y, w, h, null);
			g2d.setTransform(oldAT);
		}
		else {
			g2d.drawImage(img, x, y, w, h, null);
		}
	}
	
	/**
	 * adds an image to be drawn on the panel
	 * @param name the name of the file to load an image from
	 */
	public void addImage(String name) {
		images.add(new AlphaImage(folder, name));
		repaint();
	}
	
	/**
	 * removes an image by the name of the file
	 * @param name the name of the file that was used to load the image from
	 */
	public void removeImage(String name) {
		for (int i = 0; i < images.size();) {
			if (images.get(i).getName().equals(name)) {
				images.remove(i);
			}
			else {
				i++;
			}
		}
		repaint();
	}

	/**
	 * changes the scale mode
	 * @param selectedItem the index of the new scale mode
	 */
	public void setScaleMode(Object selectedItem) {
		scaleMode = (Scale) selectedItem;
		repaint();
	}
	
	/**
	 * removes all images from the screen
	 */
	public void removeAllImages() {
		images.clear();
		repaint();
	}

	/**
	 * toggles the flip of the images. Rotates them by 180 degrees
	 */
	public void flip() {
		flip = !flip;
		repaint();
	}
}