package layer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.io.File;
import java.util.LinkedList;

import display.DisplayPanel;

public class DisplayLayerPanel extends DisplayPanel {
	private static final long serialVersionUID = 1L;

	private LinkedList<AlphaImage> images;
	private File folder;
	private boolean autoScale;
	
	public DisplayLayerPanel() {
		images = new LinkedList<>();
		autoScale = true;
		setVisible(true);
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Dimension s = getSize();
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, s.width, s.height);
		
		if (autoScale) {
			for (AlphaImage image: images) {
				g.drawImage(image.getImage(), 0, 0, s.width, s.height, null);
			}
		}
		else {
			double screenRatio = s.getWidth() / s.getHeight();
			for (AlphaImage image: images) {
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
			}
		}
		
		g.dispose();
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
	
	public void toggleAutoScale() {
		autoScale = !autoScale;
		repaint();
	}

	public boolean getAutoScale() {
		return autoScale;
	}
}