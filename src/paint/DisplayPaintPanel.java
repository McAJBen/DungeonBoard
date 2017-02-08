package paint;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;

import main.DisplayPanel;

public class DisplayPaintPanel extends DisplayPanel {
	private static final long serialVersionUID = 1L;
	
	private static final BufferedImage BLANK_CURSOR = new BufferedImage(3, 3, BufferedImage.TYPE_INT_ARGB);
	
	private BufferedImage mask;
	private BufferedImage image;
	private Point windowPos;
	
	public DisplayPaintPanel() {
		image = BLANK_CURSOR;
		windowPos = new Point(0, 0);
		mask = BLANK_CURSOR;
		setVisible(true);
	}
	
	public void setMask(BufferedImage newMask) {
		if (newMask != null) {
			mask = new BufferedImage(newMask.getWidth(), newMask.getHeight(), BufferedImage.TYPE_INT_ARGB);
			for (int i = 0; i < newMask.getWidth(); i++) {
				for (int j = 0; j < newMask.getHeight(); j++) {
					mask.setRGB(i, j, newMask.getRGB(i, j) + 1);
				}
			}
			repaint();
		}
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, getSize().width, getSize().width);
		g.drawImage(image, -windowPos.x, -windowPos.y, null);
		g.drawImage(mask, -windowPos.x, -windowPos.y, image.getWidth(), image.getHeight(), null);
		g.dispose();
	}
	
	public void setImage(BufferedImage image) {
		this.image = image;
		repaint();
	}
	
	public void changeWindowPos(Point p) {
		windowPos = p;
		if (image.getWidth() < getSize().width) {
			windowPos.x = (image.getWidth() - getSize().width) / 2;
		}
		if (image.getHeight() < getSize().height) {
			windowPos.y = (image.getHeight() - getSize().height) / 2;
		}
		repaint();
	}
}
