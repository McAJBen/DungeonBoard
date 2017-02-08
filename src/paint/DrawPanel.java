package paint;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;

import javax.swing.JButton;
import javax.swing.JComponent;

public class DrawPanel extends JComponent {
	
	private static final long serialVersionUID = 1L;
	private static final Dimension SIZE = new Dimension(640, 460);
	private static final int CLEAR = new Color(100, 255, 100, 120).getRGB();
	private static final int OPAQUE = new Color(255, 100, 100, 128).getRGB();
	private static final Color PINK = new Color(255, 0, 255);
	
	// Pen variables
	private int radius;
	private int diameter;
	private enum Pen {SQUARE, CIRCLE};
	private Pen penType;
	
	// images
	private BufferedImage drawingLayer;
	private BufferedImage image;
	private Graphics2D g2;
	private Dimension imageSize;
	
	// drawing variables
	private Point lastP;
	private Point mousePos;
	private boolean canDraw;
	
	// style
	private enum Direction {NONE, VERTICAL, HORIZONTAL};
	private Direction style;
	
	
	private enum DrawMode {ANY, VISIBLE, INVISIBLE, WINDOW};
	private DrawMode drawMode;
	
	// 
	private final Dimension displaySize;
	private Point windowPos;
	private JButton updateButton;
	private DisplayPaintPanel display;
	
	public DrawPanel(Dimension displaySize, DisplayPaintPanel disp) {
		setDoubleBuffered(false);
		setRadius(20);
		mousePos = new Point(-100, -100);
		this.displaySize = displaySize;
		this.display = disp;
		windowPos = new Point(0, 0);
		penType = Pen.SQUARE;
		style = Direction.NONE;
		drawMode = DrawMode.ANY;
		
		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (image != null) {
					lastP = e.getPoint();
					if (drawMode == DrawMode.ANY) {
						if (e.getButton() == MouseEvent.BUTTON2) {
							setWindowPos(lastP);
							display.changeWindowPos(getWindowPos());
							canDraw = false;
						}
						else {
							if (e.getButton() == MouseEvent.BUTTON1) {
								g2.setPaint(Color.WHITE);
								canDraw = true;
							}
							else if (e.getButton() == MouseEvent.BUTTON3) {
								g2.setPaint(Color.BLACK);
								canDraw = true;
							}
							addPoint(lastP);
						}
					}
					else if (drawMode == DrawMode.VISIBLE) {
						addPoint(lastP);
					}
					else if (drawMode == DrawMode.INVISIBLE) {
						addPoint(lastP);
					}
					else if (drawMode == DrawMode.WINDOW) {
						setWindowPos(lastP);
						display.changeWindowPos(getWindowPos());
					}
					repaint();
				}
			}
		});
		addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent e) {
				if (image != null) {
					if (canDraw) {
						addPoint(e.getPoint());
					}
					else {
						setWindowPos(e.getPoint());
						display.changeWindowPos(getWindowPos());
					}
					mousePos = e.getPoint();
					repaint();
				}
			}
			public void mouseMoved(MouseEvent e) {
				mousePos = e.getPoint();
				repaint();
			}
		});
		repaint();
	}
	
	private void setWindowPos(Point p) {
		windowPos.x = p.x * imageSize.width / SIZE.width;
		windowPos.y = p.y * imageSize.height / SIZE.height;
		windowPos.x -= displaySize.width / 2;
		windowPos.y -= displaySize.height / 2;
		
		if (windowPos.x > imageSize.width - displaySize.width) {
			windowPos.x = imageSize.width - displaySize.width;
		}
		if (windowPos.x < 0) {
			windowPos.x = 0;
		}
		if (windowPos.y > imageSize.height - displaySize.height) {
			windowPos.y = imageSize.height - displaySize.height;
		}
		if (windowPos.y < 0) {
			windowPos.y = 0;
		}
		
	}
	
	private void addPoint(Point newP) {
		if (g2 != null) {
			switch (style) {
				case HORIZONTAL:
					newP.y = lastP.y;
					break;
				case VERTICAL:
					newP.x = lastP.x;
					break;
				default:
					break;
			}
			switch (penType) {
			case CIRCLE:
				g2.fillPolygon(getPolygon(newP, lastP));
				g2.fillOval(newP.x - radius, newP.y - radius, diameter, diameter);
				break;
			case SQUARE:
				g2.fillRect(newP.x - radius, newP.y - radius, diameter, diameter);
				break;
			}
			lastP = newP;
			updateButton.setEnabled(true);
		}
	}
	
	private Polygon getPolygon(Point newP, Point oldP) {
		double angle = -Math.atan2(newP.getY() - oldP.getY(), newP.getX() - oldP.getX());
		double anglePos = angle + Math.PI / 2;
		double angleNeg = angle - Math.PI / 2;
		int cosP = (int) (Math.cos(anglePos) * radius);
		int cosN = (int) (Math.cos(angleNeg) * radius);
		int sinP = (int) (Math.sin(anglePos) * radius);
		int sinN = (int) (Math.sin(angleNeg) * radius);
		return new Polygon(
				new int[] {
						newP.x + cosP,
						newP.x + cosN,
						oldP.x + cosN,
						oldP.x + cosP},
				new int[] {
						newP.y - sinP,
						newP.y - sinN,
						oldP.y - sinN,
						oldP.y - sinP}, 4);
		
	}
	
	protected void paintComponent(Graphics g) {
		if (image != null) {
			g.drawImage(image, 0, 0, SIZE.width, SIZE.height, null);
			g.drawImage(toMask(drawingLayer), 0, 0, null);
			g.setColor(PINK);
			switch (penType) {
			case CIRCLE:
				g.drawOval(mousePos.x - radius, mousePos.y - radius, diameter, diameter);
				break;
			case SQUARE:
				g.drawRect(mousePos.x - radius, mousePos.y - radius, diameter, diameter);
				break;
			}
			g.drawRect(
					windowPos.x * SIZE.width / imageSize.width,
					windowPos.y * SIZE.height / imageSize.height,
					displaySize.width * SIZE.width / imageSize.width,
					displaySize.height * SIZE.height / imageSize.height);
		}
	}
	
	private BufferedImage toMask(BufferedImage img) {
		BufferedImage mask = new BufferedImage(
				img.getWidth(),
				img.getHeight(),
				BufferedImage.TYPE_INT_ARGB);
		for (int i = 0; i < img.getWidth(); i++) {
			for (int j = 0; j < img.getHeight(); j++) {
				int dl = img.getRGB(i, j);
				if (dl == -16777216) { // clear
					mask.setRGB(i, j, OPAQUE);
				}
				else { // -1 opaque
					mask.setRGB(i, j, CLEAR);
				}
			}
		}
		return mask;
	}
	
	public BufferedImage getMask() {
		return drawingLayer;
	}
	
	public void showAll() {
		fillAll(Color.WHITE);
	}
	
	public void clear() {
		fillAll(Color.BLACK);
	}
	
	private void fillAll(Color c) {
		if (g2 != null) {
			g2.setPaint(c);
			g2.fillRect(0, 0, getSize().width, getSize().height);
			repaint();
			updateButton.setEnabled(true);
		}
	}
	
	public void setImage(BufferedImage image) {
		this.image = image;
		imageSize = new Dimension(image.getWidth(), image.getHeight());
		drawingLayer = new BufferedImage(SIZE.width, SIZE.height, BufferedImage.TYPE_BYTE_BINARY);
		g2 = (Graphics2D) drawingLayer.getGraphics();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		clear();
	}

	public void setRadius(int value) {
		radius = value;
		diameter = radius * 2;
		repaint();
	}

	public Point getWindowPos() {
		return windowPos;
	}

	public void setUpdateButton(JButton updateScreen) {
		updateButton = updateScreen;
	}

	public void toggleStyle() {
		style = Direction.values()[(style.ordinal() + 1) % Direction.values().length];
	}

	public int getStyle() {
		return style.ordinal();
	}
	
	public void toggleDrawMode() {
		drawMode = DrawMode.values()[(drawMode.ordinal() + 1) % DrawMode.values().length];
		if (g2 != null) {
		switch (drawMode) {
			case ANY:
				break;
			case VISIBLE:
				g2.setPaint(Color.WHITE);
				canDraw = true;
				break;
			case INVISIBLE:
				g2.setPaint(Color.BLACK);
				canDraw = true;
				break;
			case WINDOW:
				canDraw = false;
				break;
			}
		}
	}
	
	public int getDrawMode() {
		return drawMode.ordinal();
	}
	
	public void togglePen() {
		penType = Pen.values()[(penType.ordinal() + 1) % Pen.values().length];
		repaint();
	}

	public int getPen() {
		return penType.ordinal();
	}
}