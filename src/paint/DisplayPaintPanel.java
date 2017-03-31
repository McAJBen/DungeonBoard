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
	
	private Object lock;
	private BufferedImage mask;
	private BufferedImage image;
	private Dimension imageSize;
	private Point windowPos;
	private double scale;
	
	public DisplayPaintPanel(DisplayWindow window) {
		super(window);
		lock = new Object();
		windowPos = new Point(0, 0);
		scale = 1;
		setVisible(true);
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		((Graphics2D) g).setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, getSize().width, getSize().width);
		
		synchronized (lock) {
			if (image != null && mask != null && imageSize != null) {
				g.drawImage(image, -windowPos.x, -windowPos.y, imageSize.width, imageSize.height, null);
				g.drawImage(mask, -windowPos.x, -windowPos.y, imageSize.width, imageSize.height, null);
			}
		}
		
		window.paintMouse(g);
		g.dispose();
	}
	
	public void setMask(BufferedImage newMask) {
		synchronized (lock) {
			mask = newMask;
		}
		repaint();
	}
	
	public void setImage(BufferedImage image) {
		synchronized (lock) {
			this.image = image;
			setWindowScale(scale);
		}
		repaint();
	}
	
	public void setWindowPos(Point p) {
		synchronized (lock) {
			windowPos = p;
			if (imageSize != null) {
				if (imageSize.width < getSize().width) {
					windowPos.x = (imageSize.width - getSize().width) / 2;
				}
				if (imageSize.height < getSize().height) {
						windowPos.y = (imageSize.height - getSize().height) / 2;
				}	
			}
		}
		repaint();
	}
	
	public void setWindowScale(double scale) {
		synchronized (lock) {
			this.scale = scale;
			if (image != null) {
				imageSize = new Dimension(
						(int)(image.getWidth() / scale),
						(int)(image.getHeight() / scale));
			}
		}
		repaint();
	}

	public void resetImage() {
		synchronized (lock) {
			image = Settings.BLANK_CURSOR;
			mask = Settings.BLANK_CURSOR;
		}
	}
}