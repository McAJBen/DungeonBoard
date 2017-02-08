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
	
	public DisplayLayerPanel() {
		images = new LinkedList<>();
		setVisible(true);
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Dimension s = getSize();
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, s.width, s.height);
		for (AlphaImage image: images) {
			g.drawImage(image.getImage(), 0, 0, s.width, s.height, null);
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
	}
}