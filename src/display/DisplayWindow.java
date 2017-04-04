package display;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import javax.swing.JFrame;
import main.Settings;

public class DisplayWindow extends JFrame {

	private static final long serialVersionUID = -251787008359029888L;
	
	private Point mousePos;
	private Direction handDirection;
	
	public DisplayWindow(Rectangle r) {
		super();
		setUndecorated(true);
		setIconImage(Settings.ICON.getImage());
		setSize(r.getSize());
		setLocation(r.getLocation());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		setCursor(getToolkit().createCustomCursor(
				Settings.BLANK_CURSOR, new Point(0, 0),
	            "null"));
		mousePos = Settings.NULL_POS;
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
				setMouse(Settings.NULL_POS);
			}
			public void mouseEntered(MouseEvent e) {
				setMouse(e.getPoint());
			}
		});
		
		setVisible(true);
		
		repaint();
	}
	
	public void paintMouse(Graphics g) {
		int i = handDirection.ordinal();
		g.drawImage(
				Settings.HANDS[i],
				mousePos.x + Settings.HANDS_OFFSET[i],
				mousePos.y + Settings.HANDS_OFFSET[i == 0 ? 3 : i - 1],
				null);
	}
	
	private void setMouse(Point p) {
		mousePos = p;
		repaint();
	}
}