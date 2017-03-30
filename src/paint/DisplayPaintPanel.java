package paint;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import display.DisplayPanel;
import display.DisplayWindow;
import main.Settings;

public class DisplayPaintPanel extends DisplayPanel {
	
	private static final long serialVersionUID = -8389531693546434519L;
	
	private BufferedImage mask;
	private BufferedImage image;
	private Point windowPos;
	private double imageScale;
	
	public DisplayPaintPanel(DisplayWindow window) {
		super(window);
		image = Settings.BLANK_CURSOR;
		windowPos = new Point(0, 0);
		imageScale = 1;
		mask = Settings.BLANK_CURSOR;
		setVisible(true);
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		((Graphics2D) g).setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, getSize().width, getSize().width);
		Dimension size = new Dimension((int)(image.getWidth() / imageScale), (int) (image.getHeight() / imageScale));
		g.drawImage(image, -windowPos.x, -windowPos.y, size.width, size.height, null);
		g.drawImage(mask, -windowPos.x, -windowPos.y, size.width, size.height, null);
		window.paintMouse(g);
		g.dispose();
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
	
	public void changeWindowScale(double scale) {
		imageScale = scale;
		repaint();
	}
}
