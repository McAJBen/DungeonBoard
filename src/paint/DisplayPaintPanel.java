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
	private BufferedImage currentScreen;
	private Dimension imageSize;
	private Point windowPos;
	private double scale;
	private Thread screenCreatorThread;
	
	public DisplayPaintPanel(DisplayWindow window) {
		super(window);
		windowPos = new Point(0, 0);
		scale = 1;
		currentScreen = new BufferedImage(
				Settings.DISPLAY_SIZE.width,
				Settings.DISPLAY_SIZE.height,
				BufferedImage.TYPE_INT_RGB);
		setCurrentScreen();
		setVisible(true);
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		synchronized (currentScreen) {
			g.drawImage(currentScreen, 0, 0, null);
		}
		
		window.paintMouse(g);
		g.dispose();
	}
	
	private void setCurrentScreen() {
		
		
		screenCreatorThread = new Thread("screenCreatorThread") {
			@Override
			public void run() {
				synchronized (currentScreen) {
					Graphics2D g2d = currentScreen.createGraphics();
					g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
					if (image != null && mask != null && imageSize != null) {
						g2d.setColor(new Color(image.getRGB(0, 0)));
						g2d.fillRect(0, 0, Settings.DISPLAY_SIZE.width, Settings.DISPLAY_SIZE.height);
						g2d.drawImage(image, -windowPos.x, -windowPos.y, imageSize.width, imageSize.height, null);
						g2d.drawImage(mask, -windowPos.x, -windowPos.y, imageSize.width, imageSize.height, null);
					}
					g2d.dispose();
					repaint();
				}
			}
		};
		screenCreatorThread.start();
	}
	
	public void setMask(BufferedImage newMask) {
		synchronized (currentScreen) {
			mask = newMask;
			setCurrentScreen();
		}
	}
	
	public void setImage(BufferedImage image) {
		synchronized (currentScreen) {
			this.image = image;
			if (image != null) {
				imageSize = new Dimension(
						(int)(image.getWidth() / scale),
						(int)(image.getHeight() / scale));
			}
			setCurrentScreen();
		}
	}
	
	public void setWindow(double scale, Point p) {
		synchronized (currentScreen) {
			this.scale = scale;
			if (image != null) {
				imageSize = new Dimension(
						(int)(image.getWidth() / scale),
						(int)(image.getHeight() / scale));
			}
			windowPos = p;
			if (imageSize.width < getSize().width) {
				windowPos.x = (imageSize.width - getSize().width) / 2;
			}
			if (imageSize.height < getSize().height) {
					windowPos.y = (imageSize.height - getSize().height) / 2;
			}
			setCurrentScreen();
		}
	}
	
	public void setWindowPos(Point p) {
		synchronized (currentScreen) {
			windowPos = p;
			if (imageSize != null) {
				if (imageSize.width < getSize().width) {
					windowPos.x = (imageSize.width - getSize().width) / 2;
				}
				if (imageSize.height < getSize().height) {
						windowPos.y = (imageSize.height - getSize().height) / 2;
				}	
			}
			setCurrentScreen();
		}
	}
	
	public void setWindowScale(double scale) {
		synchronized (currentScreen) {
			this.scale = scale;
			if (image != null) {
				imageSize = new Dimension(
						(int)(image.getWidth() / scale),
						(int)(image.getHeight() / scale));
			}
			setCurrentScreen();
		}
	}

	public void resetImage() {
		synchronized (currentScreen) {
			image = Settings.BLANK_CURSOR;
			mask = Settings.BLANK_CURSOR;
			setCurrentScreen();
		}
	}
}