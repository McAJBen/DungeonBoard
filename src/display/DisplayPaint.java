package display;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import main.Settings;

/**
 * {@code JPanel} for displaying Paint Utility
 * @author McAJBen <McAJBen@gmail.com>
 * @since 1.0
 */
public class DisplayPaint extends Display {
	
	private static final long serialVersionUID = -8389531693546434519L;
	
	/**
	 * the image used as a mask for the player's display<br>
	 * each pixel should be either Color.BLACK or transparent
	 */
	private BufferedImage mask;
	
	/**
	 * the zoomed size of the image
	 */
	private Dimension imageSize;
	
	/**
	 * the top left corner of the window<br>
	 * the negative of this will be the position to start drawing
	 */
	private Point windowPos;
	
	/**
	 * the zoom scale of the image<br>
	 * - larger means zoomed out and a smaller image<br>
	 * - smaller means zoomed in and a larger image
	 */
	private double scale;
	
	/**
	 * creates in instance of {@code DisplayPaint}
	 */
	public DisplayPaint() {
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
	
	/**
	 * sets the mask to draw over the image
	 * @param newMask an image where every pixel is either Color.BLACK or transparent
	 */
	public void setMask(BufferedImage newMask) {
		mask = newMask;
		repaint();
	}
	
	/**
	 * the dimension of the image to be painted
	 */
	public void setImageSize() {
		imageSize = new Dimension(
				(int)(Settings.PAINT_IMAGE.getWidth() / scale),
				(int)(Settings.PAINT_IMAGE.getHeight() / scale));
	}
	
	@Override
	public void setMainDisplay(boolean b) {
		if (b) {
			repaint();
		}
	}
	
	/**
	 * sets the window to a specific scale and position
	 * @param scale the zoom scale for the image
	 * @param p the top left corner of the image
	 */
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
	
	/**
	 * sets the window position
	 * @param p the top left corner of the image
	 */
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
	
	/**
	 * sets the zoom scale of the image
	 * @param scale the zoom scale of the image
	 */
	public void setWindowScale(double scale) {
		this.scale = scale;
		if (Settings.PAINT_IMAGE != null) {
			setImageSize();
		}
		repaint();
	}

	/**
	 * sets the mask to completely opaque
	 */
	public void resetImage() {
		mask = Settings.BLANK_CURSOR;
		repaint();
	}
}