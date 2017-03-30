package image;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
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
	
	public DisplayImagePanel(DisplayWindow window) {
		super(window);
		scaleMode = Scale.FILL;
		setVisible(true);
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Dimension s = getSize();
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, s.width, s.height);
		
		if (image != null && image.getImage() != null) {
			paintImage(g, image, s);
		}
		window.paintMouse(g);
		g.dispose();
	}
	
	public void paintImage(Graphics g, AlphaImage image, Dimension s) {
		switch (scaleMode) {
		case FILL:
			g.drawImage(image.getImage(), 0, 0, s.width, s.height, null);
			break;
		case REAL_SIZE:
			g.setColor(new Color(image.getImage().getRGB(0, 0)));
			g.fillRect(0, 0, s.width, s.height);
			g.drawImage(image.getImage(),
					(s.width - image.getWidth()) / 2,
					(s.height - image.getHeight()) / 2, null);
			break;
		case UP_SCALE:
			g.setColor(new Color(image.getImage().getRGB(0, 0)));
			g.fillRect(0, 0, s.width, s.height);
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
}