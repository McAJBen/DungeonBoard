package display;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.LinkedList;

import main.Mode;
import main.Settings;

public class DisplayLayerPanel extends DisplayPanel {
	
	private static final long serialVersionUID = 3211548259335689270L;
	
	private LinkedList<AlphaImage> images;
	private Scale scaleMode;
	
	public DisplayLayerPanel() {
		images = new LinkedList<>();
		scaleMode = Scale.FILL;
		setVisible(true);
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(Color.BLACK);
		g2d.fillRect(0, 0, Settings.DISPLAY_SIZE.width, Settings.DISPLAY_SIZE.height);
		for (AlphaImage image: images) {
			paintImage(g2d, image);
		}
		paintMouse(g2d);
		g2d.dispose();
	}
	
	public void paintImage(Graphics2D g2d, AlphaImage image) {
		switch (scaleMode) {
		case FILL:
			g2d.drawImage(image.getImage(), 0, 0, Settings.DISPLAY_SIZE.width, Settings.DISPLAY_SIZE.height, null);
			break;
		case REAL_SIZE:
			g2d.drawImage(image.getImage(),
					(Settings.DISPLAY_SIZE.width - image.getWidth()) / 2,
					(Settings.DISPLAY_SIZE.height - image.getHeight()) / 2, null);
			break;
		case UP_SCALE:
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
			g2d.drawImage(image.getImage(),
					(Settings.DISPLAY_SIZE.width - imageScale.width) / 2,
					(Settings.DISPLAY_SIZE.height - imageScale.height) / 2,
					imageScale.width,
					imageScale.height,
					null);
			break;
		}
	}
	
	public void addImage(String name) {
		images.add(new AlphaImage(Settings.FOLDERS[Mode.LAYER.ordinal()], name));
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
}