package display;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

import main.FileChooser;

public class DisplayWindow extends JFrame {

	private static final long serialVersionUID = 1L;
	private static final BufferedImage BLANK_CURSOR = new BufferedImage(3, 3, BufferedImage.TYPE_INT_ARGB);
	private static final Point NULL_POS = new Point(-100, -100);
	private static final Dimension HAND_OFFSET = new Dimension(100, 5);
	
	private BufferedImage handImages[];
	private Point mousePos;
	
	public DisplayWindow(Rectangle r) {
		super();
		setUndecorated(true);
		add(new PointerPanel());
		setSize(r.getSize());
		setLocation(r.getLocation());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		setCursor(getToolkit().createCustomCursor(
				BLANK_CURSOR, new Point(0, 0),
	            "null"));
		mousePos = NULL_POS;
		handImages = getImages();
		
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
			public void mousePressed(MouseEvent e) {}
			public void mouseClicked(MouseEvent e) {}
			public void mouseExited(MouseEvent e) {
				setMouse(NULL_POS);
			}
			public void mouseEntered(MouseEvent e) {
				setMouse(e.getPoint());
			}
		});
		
		setVisible(true);
		
		repaint();
	}
	
	private static BufferedImage[] getImages() {
		BufferedImage[] img = new BufferedImage[4];
        try {
        	for (int i = 0; i < 4; i++) {
	        	java.net.URL imgURL = FileChooser.class.getResource("/resources/hand" + i + ".png");
	        	if (imgURL != null) {
	        		img[i] = ImageIO.read(imgURL);
	        	}
        	}
        	return img;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
    }
	
	private void setMouse(Point p) {
		mousePos = p;
		repaint();
	}
	
	private class PointerPanel extends JPanel {
		private static final long serialVersionUID = 1L;
		public void paint(Graphics g) {
			paintMouse(g);
		}
	}
	
	private void paintMouse(Graphics g) {
		// U D L R
		if (mousePos.y < getSize().height / 3) {
			// UP
			g.drawImage(handImages[0],
					mousePos.x - HAND_OFFSET.height,
					mousePos.y, null);
		}
		else if (mousePos.y > getSize().height * 2 / 3) {
			// DOWN
			g.drawImage(handImages[1],
					mousePos.x - handImages[1].getWidth() + HAND_OFFSET.height,
					mousePos.y - HAND_OFFSET.width, null);
		}
		else if (mousePos.x < getSize().width / 3) {
			// LEFT
			g.drawImage(handImages[2],
					mousePos.x,
					mousePos.y + HAND_OFFSET.height - handImages[2].getHeight(), null);
		}
		else {
			// RIGHT
			g.drawImage(handImages[3],
					mousePos.x - HAND_OFFSET.width,
					mousePos.y - HAND_OFFSET.height, null);
		}
	}
}