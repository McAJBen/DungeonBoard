package display;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import main.Main;
import main.Mode;
import main.Settings;

/**
 * {@code JFrame} for displaying players screen
 * @author McAJBen <McAJBen@gmail.com>
 * @since 1.0
 */
public class DisplayWindow extends JFrame {

	private static final long serialVersionUID = -251787008359029888L;
	
	/**
	 * the offsets used to display the cursor hand
	 */
	private static final int[] HANDS_OFFSET = {-5, -100, -45, 0};
	
	/**
	 * the position a cursor is placed when not on screen
	 */
	private static final Point NULL_POS = new Point(-100, -100);
	
	/**
	 * the images for cursor hands
	 */
	private static final ImageIcon HANDS[] = {
			Settings.load("hand0.png"),
			Settings.load("hand1.png"),
			Settings.load("hand2.png"),
			Settings.load("hand3.png")
	};
	
	/**
	 * the position that the mouse is on the screen<br>
	 * used to place the hand cursor
	 */
	private Point mousePos;
	
	/**
	 * the direction that the hand cursor is facing
	 */
	private Direction handDirection;
	
	/**
	 * handler for displaying a timer created from {@code DisplayLoading}
	 */
	private DisplayTimer displayTimer;
	
	/**
	 * creates an instance of {@code DisplayWindow}
	 * @param r the position and dimensions of the {@code JFrame}
	 */
	public DisplayWindow(Rectangle r) {
		super();
		setTitle("Display");
		setUndecorated(true);
		setIconImage(Settings.ICON.getImage());
		setSize(r.getSize());
		setLocation(r.getLocation());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		setCursor(getToolkit().createCustomCursor(
				Settings.BLANK_CURSOR, new Point(0, 0),
	            "null"));
		mousePos = NULL_POS;
		handDirection = Direction.UP;
		displayTimer = new DisplayTimer(getSize());
		
		addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent e) {
				setMouse(e.getPoint());
			}
			public void mouseMoved(MouseEvent e) {
				setMouse(e.getPoint());
			}
		});
		addMouseListener(new MouseListener() {
			public void mouseReleased(MouseEvent e) {}
			public void mousePressed(MouseEvent e) {
				handDirection = Direction.values()
						[(handDirection.ordinal() + 1) % Direction.values().length];
				repaint();
			}
			public void mouseClicked(MouseEvent e) {}
			public void mouseExited(MouseEvent e) {
				setMouse(NULL_POS);
			}
			public void mouseEntered(MouseEvent e) {
				setMouse(e.getPoint());
			}
		});
	}
	
	/**
	 * paints the hand cursor to the screen
	 * @param g2d the graphics to paint to
	 */
	public void paintDisplay(Graphics2D g2d) {
		int i = handDirection.ordinal();
		displayTimer.paint(g2d);
		g2d.drawImage(
				HANDS[i].getImage(),
				mousePos.x + HANDS_OFFSET[i],
				mousePos.y + HANDS_OFFSET[i == 0 ? 3 : i - 1],
				null);
		
	}
	
	/**
	 * changes the panel being displayed
	 * @param newMode the new mode to display
	 * @param oldMode the old mode displayed before
	 */
	public void setMode(Mode newMode, Mode oldMode) {
		Thread thread = new Thread() {
			@Override
			public void run() {
				super.run();
				synchronized (Main.getControl()) {
					remove(Main.getDisplay(oldMode));
					Main.getDisplay(oldMode).setMainDisplay(false);
					add(Main.getDisplay(newMode));
					validate();
					Main.getDisplay(newMode).setMainDisplay(true);
				}
			}
		};
		thread.start();
	}
	
	/**
	 * changes the position of the mouse
	 * @param p the new position of the mouse
	 */
	private void setMouse(Point p) {
		mousePos = p;
		repaint();
	}

	/**
	 * enables a timer for a set amount of seconds
	 * @param seconds number of seconds for the timer to count down
	 */
	public void setTimer(int seconds) {
		displayTimer.setTimer(seconds);
	}

	/**
	 * removes the timer from {@code DisplayWindow}
	 */
	public void clearTimer() {
		displayTimer.clearTimer();
	}
}