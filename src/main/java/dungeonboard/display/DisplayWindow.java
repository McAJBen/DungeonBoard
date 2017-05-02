package dungeonboard.display;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import dungeonboard.Main;
import dungeonboard.Mode;
import dungeonboard.Settings;

public class DisplayWindow extends JFrame {

	private static final long serialVersionUID = -251787008359029888L;
	
	private static final int[] HANDS_OFFSET = {-5, -100, -45, 0};
	
	private static final Point NULL_POS = new Point(-100, -100);
	
	private static final ImageIcon HANDS[] = {
			Settings.load("hand0.png"),
			Settings.load("hand1.png"),
			Settings.load("hand2.png"),
			Settings.load("hand3.png")
	};
	
	private Point mousePos;
	
	private Direction handDirection;
	
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
				handDirection = Direction.values()[(handDirection.ordinal() + 1) % Direction.values().length];
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
	
	public void paintMouse(Graphics2D g2d) {
		int i = handDirection.ordinal();
		g2d.drawImage(
				HANDS[i].getImage(),
				mousePos.x + HANDS_OFFSET[i],
				mousePos.y + HANDS_OFFSET[i == 0 ? 3 : i - 1],
				null);
	}
	
	public void setMode(Mode newMode, Mode oldMode) {
		remove(Main.getDisplay(oldMode));
		Main.getDisplay(oldMode).setMainDisplay(false);
		add(Main.getDisplay(newMode));
		Main.getDisplay(newMode).setMainDisplay(true);
		validate();
		repaint();
	}
	
	private void setMouse(Point p) {
		mousePos = p;
		repaint();
	}
}