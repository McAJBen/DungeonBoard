package image;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;

import display.DisplayPanel;
import display.DisplayWindow;
import layer.AlphaImage;
import layer.Scale;

public class DisplayImagePanel extends DisplayPanel {

	private static final long serialVersionUID = 4732317749539981643L;
	
	private AlphaImage image;
	private File folder;
	private Scale scaleMode;
	private boolean flip;
	
	public DisplayImagePanel(DisplayWindow window) {
		super(window);
		scaleMode = Scale.FILL;
		flip = false;
		setVisible(true);
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Dimension s = getSize();
		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(Color.BLACK);
		g2d.fillRect(0, 0, s.width, s.height);
		
		if (image != null && image.getImage() != null) {
			paintImage(g2d, image.getImage(), s);
		}
		window.paintMouse(g2d);
		g2d.dispose();
	}
	
	public void paintImage(Graphics2D g2d, BufferedImage image, Dimension s) {
		
		switch (scaleMode) {
		case FILL:
			drawImage(g2d, image, 0, 0, s.width, s.height);
			break;
		case REAL_SIZE:
			g2d.setColor(new Color(image.getRGB(0, 0)));
			g2d.fillRect(0, 0, s.width, s.height);
			drawImage(g2d, image,
					(s.width - image.getWidth()) / 2,
					(s.height - image.getHeight()) / 2,
					image.getWidth(),
					image.getHeight());
			break;
		case UP_SCALE:
			g2d.setColor(new Color(image.getRGB(0, 0)));
			g2d.fillRect(0, 0, s.width, s.height);
			double screenRatio = s.getWidth() / s.getHeight();
			double imageRatio = (double)image.getWidth() / image.getHeight();
			Dimension imageScale;
			if (imageRatio > screenRatio) {
				// width > height
				imageScale = new Dimension(s.width, (int) (s.width / imageRatio));
			}
			else {
				// width < height
				imageScale = new Dimension((int) (s.height * imageRatio), s.height);
			}
			drawImage(g2d, image,
					(s.width - imageScale.width) / 2,
					(s.height - imageScale.height) / 2,
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
		image = new AlphaImage(folder, name);
		repaint();
	}

	public void setFolder(File folder) {
		this.folder = folder;
		image = null;
		repaint();
	}

	public void setScaleMode(Scale selectedItem) {
		scaleMode = selectedItem;
		repaint();
	}

	public void flip() {
		flip = !flip;
		repaint();
	}
}