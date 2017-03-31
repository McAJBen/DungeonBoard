package paint;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import main.Settings;

public class DrawPanel extends JComponent {
	
	private static final long serialVersionUID = -3142625453462827948L;
	
	private final Dimension displaySize;
	
	private Object lock;
	
	// Pen variables
	private int radius;
	private int diameter;
	private Pen penType;
	
	// images
	private BufferedImage drawingLayer;
	private BufferedImage image;
	private Graphics2D g2;
	private Dimension controlSize;
	private double displayZoom;
	
	// drawing variables
	private Point lastP;
	private Point mousePos;
	private boolean canDraw;
	private boolean loading;
	
	// style
	private Direction style;
	private DrawMode drawMode;
	
	// window stuff?
	private Point lastWindowClick;
	private Point windowPos;
	private JButton updateButton;
	private DisplayPaintPanel display;
	
	public DrawPanel(Dimension displaySize, DisplayPaintPanel disp) {
		lock = new Object();
		setDoubleBuffered(false);
		setRadius(25);
		mousePos = new Point(-100, -100);
		this.displaySize = displaySize;
		displayZoom = 1;
		this.display = disp;
		windowPos = new Point(0, 0);
		lastWindowClick = new Point(0, 0);
		penType = Pen.CIRCLE;
		style = Direction.NONE;
		drawMode = DrawMode.ANY;
		
		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				synchronized (lock) {
					if (image != null) {
						lastP = toDrawingPoint(e.getPoint());
						switch (drawMode) {
						case ANY:
							if (e.getButton() == MouseEvent.BUTTON2) {
								setWindowPos(lastP);
								display.setWindowPos(getWindowPos());
								canDraw = false;
							}
							else {
								if (e.getButton() == MouseEvent.BUTTON1) {
									g2.setPaint(Settings.CLEAR);
									canDraw = true;
								}
								else if (e.getButton() == MouseEvent.BUTTON3) {
									g2.setPaint(Settings.OPAQUE);
									canDraw = true;
								}
								addPoint(lastP);
							}
							break;
						case INVISIBLE:
						case VISIBLE:
							addPoint(lastP);
							break;
						case WINDOW:
							setWindowPos(lastP);
							display.setWindowPos(getWindowPos());
							break;
						}
						repaint();
					}
				}
			}
		});
		addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent e) {
				synchronized (lock) {
					if (image != null) {
						if (canDraw) {
							addPoint(toDrawingPoint(e.getPoint()));
						}
						else {
							setWindowPos(toDrawingPoint(e.getPoint()));
							display.setWindowPos(getWindowPos());
						}
						mousePos = e.getPoint();
						repaint();
					}
				}
			}
			public void mouseMoved(MouseEvent e) {
				synchronized (lock) {
					mousePos = e.getPoint();
				}
				repaint();
			}
		});
		addComponentListener(new ComponentListener() {
			public void componentShown(ComponentEvent e) {}
			public void componentResized(ComponentEvent e) {
				synchronized (lock) {
					controlSize = getSize();
				}
			}
			public void componentMoved(ComponentEvent e) {}
			public void componentHidden(ComponentEvent e) {}
		});
		repaint();
	}
	
	public void setZoom(double zoom) {
		synchronized (lock) {
			displayZoom = zoom;
			display.setWindowScale(zoom);
			setWindowPos(lastWindowClick);
			display.setWindowPos(getWindowPos());
		}
		repaint();
	}
	
	public void setImage(BufferedImage image) {
		synchronized (lock) {
			if (g2 == null ||
					this.image.getWidth() != image.getWidth() ||
					this.image.getHeight() != image.getHeight() ||
					JOptionPane.showConfirmDialog(this,
						"Would you like to keep the same visibility mask?",
						"Paint Image has been changed",
						JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) {
				
				drawingLayer = new BufferedImage(
					image.getWidth() / Settings.PIXELS_PER_MASK,
					image.getHeight() / Settings.PIXELS_PER_MASK,
					BufferedImage.TYPE_INT_ARGB);
				
				g2 = (Graphics2D) drawingLayer.getGraphics();
				g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC, 0.6f));
				g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
				clear();
			}
			this.image = image;
			loading = false;
		}
	}

	public void setRadius(int value) {
		synchronized (lock) {
			radius = value;
			diameter = radius * 2;
		}
		repaint();
	}

	public void setUpdateButton(JButton updateScreen) {
		updateButton = updateScreen;
	}
	
	public void resetImage() {
		synchronized (lock) {
			image = null;
			g2 = null;
			drawingLayer = null;
			loading = false;
		}
	}
	
	public void togglePen() {
		synchronized (lock) {
			penType = Pen.values()[(penType.ordinal() + 1) % Pen.values().length];
		}
		repaint();
	}

	public void toggleStyle() {
		synchronized (lock) {
			style = Direction.values()[(style.ordinal() + 1) % Direction.values().length];
		}
	}
	
	public void toggleDrawMode() {
		synchronized (lock) {
			drawMode = DrawMode.values()[(drawMode.ordinal() + 1) % DrawMode.values().length];
			if (g2 != null) {
				switch (drawMode) {
				case ANY:
					break;
				case VISIBLE:
					g2.setPaint(Settings.CLEAR);
					canDraw = true;
					break;
				case INVISIBLE:
					g2.setPaint(Settings.OPAQUE);
					canDraw = true;
					break;
				case WINDOW:
					canDraw = false;
					break;
				}
			}
		}
	}
	
	public void setImageLoading() {
		synchronized (lock) {
			loading = true;
		}
		repaint();
	}
	
	public int getPen() {
		return penType.ordinal();
	}
	
	public int getStyle() {
		return style.ordinal();
	}
	
	public int getDrawMode() {
		return drawMode.ordinal();
	}
	
	public BufferedImage getMask() {
		BufferedImage mask = new BufferedImage(
				drawingLayer.getWidth(),
				drawingLayer.getHeight(),
				BufferedImage.TYPE_INT_ARGB);
		
		for (int i = 0; i < drawingLayer.getWidth(); i++) {
			for (int j = 0; j < drawingLayer.getHeight(); j++) {
				int dl = drawingLayer.getRGB(i, j);
				if (dl == -1721434268) { // CLEAR
					mask.setRGB(i, j, 0);
				}
				else if (dl == -1711315868) { // OPAQUE
					mask.setRGB(i, j, -16777215);
				}
				else {
					System.out.println(dl);
				}
			}
		}
		return mask;
	}
	
	public Point getWindowPos() {
		return new Point((int)(windowPos.x / displayZoom), (int) (windowPos.y / displayZoom));
	}
	
	public Dimension getDisplaySize() {
		return displaySize;
	}
	
	protected void paintComponent(Graphics g) {
		synchronized (lock) {
			if (loading) {
				g.drawString("Loading...", controlSize.width / 2, controlSize.height / 2);
			}
			else if (image != null) {
				g.drawImage(image, 0, 0, controlSize.width, controlSize.height, null);
				g.drawImage(drawingLayer, 0, 0, controlSize.width, controlSize.height, null);
				g.setColor(Settings.PINK);
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
						(int) (displaySize.width * displayZoom * controlSize.width / image.getWidth()),
						(int) (displaySize.height * displayZoom * controlSize.height / image.getHeight()));
			}
			else if (controlSize != null) {
				g.drawString("No image loaded", controlSize.width / 2, controlSize.height / 2);
			}
		}
	}

	private Point toDrawingPoint(Point p) {
		return new Point(
				p.x * drawingLayer.getWidth() / controlSize.width,
				p.y * drawingLayer.getHeight() / controlSize.height);
	}
	
	private void setWindowPos(Point p) {
		synchronized (lock) {
			lastWindowClick = p;
			
			windowPos.x = (int) (p.x * Settings.PIXELS_PER_MASK - (displaySize.width * displayZoom) / 2);
			windowPos.y = (int) (p.y * Settings.PIXELS_PER_MASK - (displaySize.height * displayZoom) / 2);
			
			if (image != null) {
				if (windowPos.x > image.getWidth() - displaySize.width * displayZoom) {
					windowPos.x = (int) (image.getWidth() - displaySize.width * displayZoom);
				}
				if (windowPos.x < 0) {
					windowPos.x = 0;
				}
				if (windowPos.y > image.getHeight() - displaySize.height * displayZoom) {
					windowPos.y = (int) (image.getHeight() - displaySize.height * displayZoom);
				}
				if (windowPos.y < 0) {
					windowPos.y = 0;
				}
			}
		}
	}
	
	private void addPoint(Point newP) {
		synchronized (lock) {
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
	
	private void fillAll(Color c) {
		synchronized (lock) {
			if (g2 != null) {
				g2.setPaint(c);
				g2.fillRect(0, 0, drawingLayer.getWidth(), drawingLayer.getHeight());
				repaint();
				updateButton.setEnabled(true);
			}
		}
	}
	
	private void clear() {
		fillAll(Settings.OPAQUE);
	}
}