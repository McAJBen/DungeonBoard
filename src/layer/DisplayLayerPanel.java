package layer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.io.File;
import java.util.LinkedList;

import display.DisplayPanel;
import layer.ControlLayer.Scale;

public class DisplayLayerPanel extends DisplayPanel {
	private static final long serialVersionUID = 1L;

	private LinkedList<AlphaImage> images;
	private File folder;
	private Scale scaleMode;
	private boolean showOne;
	
	public DisplayLayerPanel() {
		images = new LinkedList<>();
		scaleMode = Scale.FILL;
		showOne = false;
		setVisible(true);
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Dimension s = getSize();
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, s.width, s.height);
		
		if (showOne) {
			if (!images.isEmpty()) {
				paintImage(g, images.getLast(), s);
			}
		}
		else {
			for (AlphaImage image: images) {
				paintImage(g, image, s);
			}
		}
		g.dispose();
	}
	
	public void paintImage(Graphics g, AlphaImage image, Dimension s) {
		switch (scaleMode) {
		case FILL:
			g.drawImage(image.getImage(), 0, 0, s.width, s.height, null);
			break;
		case REAL_SIZE:
			g.drawImage(image.getImage(),
					(s.width - image.getWidth()) / 2,
					(s.height - image.getHeight()) / 2, null);
			break;
		case UP_SCALE:
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
			g.drawImage(image.getImage(),
					(s.width - imageScale.width) / 2,
					(s.height - imageScale.height) / 2,
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

	public void setShowOne(boolean b) {
		showOne = b;
		repaint();
	}
}