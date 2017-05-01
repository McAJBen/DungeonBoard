package display;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import main.Mode;
import main.Settings;

public class DisplayImage extends Display {

	private static final long serialVersionUID = 4732317749539981643L;
	
	private AlphaImage image;
	private Scale scaleMode;
	private boolean flip;
	
	public DisplayImage() {
		scaleMode = Scale.FILL;
		flip = false;
		setVisible(true);
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(Color.BLACK);
		g2d.fillRect(0, 0, Settings.DISPLAY_SIZE.width, Settings.DISPLAY_SIZE.height);
		
		if (image != null && image.getImage() != null) {
			paintImage(g2d, image.getImage());
		}
		paintMouse(g2d);
		g2d.dispose();
	}
	
	public void paintImage(Graphics2D g2d, BufferedImage image) {
		
		switch (scaleMode) {
		case FILL:
			drawImage(g2d, image, 0, 0, Settings.DISPLAY_SIZE.width, Settings.DISPLAY_SIZE.height);
			break;
		case REAL_SIZE:
			g2d.setColor(new Color(image.getRGB(0, 0)));
			g2d.fillRect(0, 0, Settings.DISPLAY_SIZE.width, Settings.DISPLAY_SIZE.height);
			drawImage(g2d, image,
					(Settings.DISPLAY_SIZE.width - image.getWidth()) / 2,
					(Settings.DISPLAY_SIZE.height - image.getHeight()) / 2,
					image.getWidth(),
					image.getHeight());
			break;
		case UP_SCALE:
			g2d.setColor(new Color(image.getRGB(0, 0)));
			g2d.fillRect(0, 0, Settings.DISPLAY_SIZE.width, Settings.DISPLAY_SIZE.height);
			double screenRatio = Settings.DISPLAY_SIZE.getWidth() / Settings.DISPLAY_SIZE.getHeight();
			double imageRatio = (double)image.getWidth() / image.getHeight();
			Dimension imageScale;
			if (imageRatio > screenRatio) {
				// width > height
				imageScale = new Dimension(Settings.DISPLAY_SIZE.width, (int) (Settings.DISPLAY_SIZE.width / imageRatio));
			}
			else {
				// width < height
				imageScale = new Dimension((int) (Settings.DISPLAY_SIZE.height * imageRatio), Settings.DISPLAY_SIZE.height);
			}
			drawImage(g2d, image,
					(Settings.DISPLAY_SIZE.width - imageScale.width) / 2,
					(Settings.DISPLAY_SIZE.height - imageScale.height) / 2,
					imageScale.width,
					imageScale.height);
			break;
		}
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
	
	public void setImage(String name) {
		image = new AlphaImage(Settings.FOLDERS[Mode.IMAGE.ordinal()], name);
		repaint();
	}

	public void setScaleMode(Object selectedItem) {
		scaleMode = (Scale) selectedItem;
		repaint();
	}

	public void flip() {
		flip = !flip;
		repaint();
	}
}