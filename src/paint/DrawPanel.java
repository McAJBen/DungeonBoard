package paint;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;

import javax.swing.JButton;
import javax.swing.JComponent;

public class DrawPanel extends JComponent {
	
	private static final long serialVersionUID = 1L;
	private static final Color CLEAR = new Color(100, 255, 100); // green
	private static final Color OPAQUE = new Color(255, 100, 100); // red
	private static final Color PINK = new Color(255, 0, 255);
	
	private static final int PIXELS_PER_MASK = 10;
	
	// Pen variables
	private int radius;
	private int diameter;
	private enum Pen {CIRCLE, SQUARE};
	private Pen penType;
	
	// images
	private BufferedImage drawingLayer;
	private BufferedImage image;
	private Graphics2D g2;
	private Dimension controlSize;
	
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
		penType = Pen.CIRCLE;
		style = Direction.NONE;
		drawMode = DrawMode.ANY;
		
		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (image != null) {
					lastP = toDrawingPoint(e.getPoint());
					if (drawMode == DrawMode.ANY) {
						if (e.getButton() == MouseEvent.BUTTON2) {
							setWindowPos(lastP);
							display.changeWindowPos(getWindowPos());
							canDraw = false;
						}
						else {
							if (e.getButton() == MouseEvent.BUTTON1) {
								g2.setPaint(CLEAR);
								canDraw = true;
							}
							else if (e.getButton() == MouseEvent.BUTTON3) {
								g2.setPaint(OPAQUE);
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
						addPoint(toDrawingPoint(e.getPoint()));
					}
					else {
						setWindowPos(toDrawingPoint(e.getPoint()));
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
		addComponentListener(new ComponentListener() {
			public void componentShown(ComponentEvent e) {}
			public void componentResized(ComponentEvent e) {
				controlSize = getSize();
			}
			public void componentMoved(ComponentEvent e) {}
			public void componentHidden(ComponentEvent e) {}
		});
		
		repaint();
	}
	
	private Point toDrawingPoint(Point p) {
		return new Point(
				p.x * drawingLayer.getWidth() / controlSize.width,
				p.y * drawingLayer.getHeight() / controlSize.height);
	}
	
	private void setWindowPos(Point p) {
		windowPos.x = p.x * PIXELS_PER_MASK;
		windowPos.y = p.y * PIXELS_PER_MASK;
		windowPos.x -= displaySize.width / 2;
		windowPos.y -= displaySize.height / 2;
		
		if (windowPos.x > image.getWidth() - displaySize.width) {
			windowPos.x = image.getWidth() - displaySize.width;
		}
		if (windowPos.x < 0) {
			windowPos.x = 0;
		}
		if (windowPos.y > image.getHeight() - displaySize.height) {
			windowPos.y = image.getHeight() - displaySize.height;
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
				g2.fillOval(
						newP.x - radius * drawingLayer.getWidth() / controlSize.width,
						newP.y - radius * drawingLayer.getHeight() / controlSize.height,
						diameter * drawingLayer.getWidth() / controlSize.width,
						diameter * drawingLayer.getHeight() / controlSize.height);
				break;
			case SQUARE:
				g2.fillRect(
						newP.x - radius * drawingLayer.getWidth() / controlSize.width,
						newP.y - radius * drawingLayer.getHeight() / controlSize.height,
						diameter * drawingLayer.getWidth() / controlSize.width,
						diameter * drawingLayer.getHeight() / controlSize.height);
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
		int cosP = (int) (Math.cos(anglePos) * radius * drawingLayer.getWidth() / controlSize.width);
		int cosN = (int) (Math.cos(angleNeg) * radius * drawingLayer.getWidth() / controlSize.width);
		int sinP = (int) (Math.sin(anglePos) * radius * drawingLayer.getHeight() / controlSize.height);
		int sinN = (int) (Math.sin(angleNeg) * radius * drawingLayer.getHeight() / controlSize.height);
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
			g.drawImage(image, 0, 0, controlSize.width, controlSize.height, null);
			g.drawImage(drawingLayer, 0, 0, controlSize.width, controlSize.height, null);
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
					windowPos.x * controlSize.width / image.getWidth(),
					windowPos.y * controlSize.height / image.getHeight(),
					displaySize.width * controlSize.width / image.getWidth(),
					displaySize.height * controlSize.height / image.getHeight());
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
				if (dl == -1721434268) { // CLEAR
					mask.setRGB(i, j, -1);
				}
				else if (dl == -1711315868) { // OPAQUE
					mask.setRGB(i, j, -16777216);
				}
				else {
					System.out.println(dl);
				}
			}
		}
		return mask;
	}
	
	public BufferedImage getMask() {
		return toMask(drawingLayer);
	}
	
	public void showAll() {
		fillAll(CLEAR);
	}
	
	public void clear() {
		fillAll(OPAQUE);
	}
	
	private void fillAll(Color c) {
		if (g2 != null) {
			g2.setPaint(c);
			g2.fillRect(0, 0, drawingLayer.getWidth(), drawingLayer.getHeight());
			repaint();
			updateButton.setEnabled(true);
		}
	}
	
	public void setImage(BufferedImage image) {
		this.image = image;
		drawingLayer = new BufferedImage(
				image.getWidth() / PIXELS_PER_MASK,
				image.getHeight() / PIXELS_PER_MASK,
				BufferedImage.TYPE_INT_ARGB);
		g2 = (Graphics2D) drawingLayer.getGraphics();
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC, 0.6f));
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
				g2.setPaint(CLEAR);
				canDraw = true;
				break;
			case INVISIBLE:
				g2.setPaint(OPAQUE);
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