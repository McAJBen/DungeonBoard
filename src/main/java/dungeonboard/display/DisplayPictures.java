package dungeonboard.display;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.LinkedList;

import dungeonboard.Settings;

public class DisplayPictures extends Display {

	private static final long serialVersionUID = 350995921778402576L;
	
	private LinkedList<AlphaImage> images;
	private Scale scaleMode;
	private boolean flip;
	
	private final File folder;
	
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
	
	private void fillBackground(Graphics2D g2d, Color c) {
		g2d.setColor(c);
		g2d.fillRect(0, 0, Settings.DISPLAY_SIZE.width, Settings.DISPLAY_SIZE.height);
	}
	
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
	
	public void addImage(String name) {
		images.add(new AlphaImage(folder, name));
		repaint();
	}
	
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

	public void setScaleMode(Object selectedItem) {
		scaleMode = (Scale) selectedItem;
		repaint();
	}
	
	public void removeAllImages() {
		images.clear();
		repaint();
	}

	public void flip() {
		flip = !flip;
		repaint();
	}
}