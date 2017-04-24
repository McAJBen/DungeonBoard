package layer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.io.File;
import java.util.LinkedList;
import display.DisplayPanel;
import display.DisplayWindow;
import main.Settings;

public class DisplayLayerPanel extends DisplayPanel {
	
	private static final long serialVersionUID = 3211548259335689270L;
	
	private LinkedList<AlphaImage> images;
	private File folder;
	private Scale scaleMode;
	
	public DisplayLayerPanel(DisplayWindow window) {
		super(window);
		images = new LinkedList<>();
		scaleMode = Scale.FILL;
		setVisible(true);
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, Settings.DISPLAY_SIZE.width, Settings.DISPLAY_SIZE.height);
		
		for (AlphaImage image: images) {
			paintImage(g, image);
		}
		
		window.paintMouse(g);
		g.dispose();
	}
	
	public void paintImage(Graphics g, AlphaImage image) {
		switch (scaleMode) {
		case FILL:
			g.drawImage(image.getImage(), 0, 0, Settings.DISPLAY_SIZE.width, Settings.DISPLAY_SIZE.height, null);
			break;
		case REAL_SIZE:
			g.drawImage(image.getImage(),
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
			g.drawImage(image.getImage(),
					(Settings.DISPLAY_SIZE.width - imageScale.width) / 2,
					(Settings.DISPLAY_SIZE.height - imageScale.height) / 2,
					imageScale.width,
					imageScale.height,
					null);
			break;
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

	public void setFolder(File folder) {
		this.folder = folder;
		images.clear();
		repaint();
	}

	public void setScaleMode(Scale selectedItem) {
		scaleMode = selectedItem;
		repaint();
	}
}