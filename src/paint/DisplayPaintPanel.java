package paint;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import display.DisplayPanel;
import main.Settings;

public class DisplayPaintPanel extends DisplayPanel {
	
	private static final long serialVersionUID = -8389531693546434519L;
	
	private BufferedImage mask;
	private Dimension imageSize;
	private Point windowPos;
	private double scale;
	
	public DisplayPaintPanel() {
		windowPos = new Point(0, 0);
		scale = 1;
		setVisible(true);
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2d = ((Graphics2D) g);
		
		g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g2d.setColor(Color.BLACK);
		g2d.fillRect(0, 0, Settings.DISPLAY_SIZE.width, Settings.DISPLAY_SIZE.height);
		
		if (Settings.PAINT_IMAGE != null && mask != null && imageSize != null) {
			g2d.drawImage(Settings.PAINT_IMAGE, -windowPos.x, -windowPos.y, imageSize.width, imageSize.height, null);
			g2d.drawImage(mask, -windowPos.x, -windowPos.y, imageSize.width, imageSize.height, null);
		}
		paintMouse(g2d);
		g2d.dispose();
	}
	
	
	public void setMask(BufferedImage newMask) {
		mask = newMask;
		repaint();
	}
	
	public void setImageSize() {
		imageSize = new Dimension(
				(int)(Settings.PAINT_IMAGE.getWidth() / scale),
				(int)(Settings.PAINT_IMAGE.getHeight() / scale));
	}
	
	public void setWindow(double scale, Point p) {
		this.scale = scale;
		if (Settings.PAINT_IMAGE != null) {
			setImageSize();
			windowPos = p;
			if (imageSize.width < Settings.DISPLAY_SIZE.width) {
				windowPos.x = (imageSize.width - Settings.DISPLAY_SIZE.width) / 2;
			}
			if (imageSize.height < Settings.DISPLAY_SIZE.height) {
					windowPos.y = (imageSize.height - Settings.DISPLAY_SIZE.height) / 2;
			}
		}
		repaint();
	}
	
	public void setWindowPos(Point p) {
		windowPos = p;
		if (imageSize != null) {
			if (imageSize.width < getSize().width) {
				windowPos.x = (imageSize.width - getSize().width) / 2;
			}
			if (imageSize.height < getSize().height) {
					windowPos.y = (imageSize.height - getSize().height) / 2;
			}	
		}
		repaint();
	}
	
	public void setWindowScale(double scale) {
		this.scale = scale;
		if (Settings.PAINT_IMAGE != null) {
			setImageSize();
		}
		repaint();
	}

	public void resetImage() {
		mask = Settings.BLANK_CURSOR;
		repaint();
	}
}