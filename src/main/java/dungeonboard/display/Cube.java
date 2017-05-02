package dungeonboard.display;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.Random;

import dungeonboard.Settings;

public class Cube {
	
	private final int SPEED;
	private Point point;
	private boolean vertical;
	private boolean horizontal;
	private boolean flag;
	
	public Cube(Dimension windowSize) {
		Random rand = new Random();
		SPEED = rand.nextInt(5) + 1;
		point = new Point(
				rand.nextInt(windowSize.width - Settings.ICON_DVD.getIconWidth()),
				rand.nextInt(windowSize.height - Settings.ICON_DVD.getIconHeight()));
		vertical = rand.nextBoolean();
		horizontal = rand.nextBoolean();
		flag = false;
	}
	
	public void paint(Graphics2D g2d, Dimension windowSize) {
		if (flag) {
			g2d.drawImage(Settings.ICON_DVD2.getImage(), point.x, point.y, null);
		}
		else {
			g2d.drawImage(Settings.ICON_DVD.getImage(), point.x, point.y, null);
		}
	}
	
	public void move(Dimension windowSize) {
		if (!flag) {
			boolean vertHit = false;
			if (vertical) {
				point.x -= SPEED;
				if (point.x < 0) {
					point.x = 0;
					vertical = false;
					vertHit = true;
				}
			}
			else {
				point.x += SPEED;
				if (point.x > windowSize.width - Settings.ICON_DVD.getIconWidth()) {
					point.x = windowSize.width - Settings.ICON_DVD.getIconWidth();
					vertical = true;
					vertHit = true;
				}
			}
			if (horizontal) {
				point.y -= SPEED;
				if (point.y < 0) {
					point.y = 0;
					horizontal = false;
					if (vertHit) {
						flag = true;
					}
				}
			}
			else {
				point.y += SPEED;
				if (point.y > windowSize.height - Settings.ICON_DVD.getIconHeight()) {
					point.y = windowSize.height - Settings.ICON_DVD.getIconHeight();
					horizontal = true;
					if (vertHit) {
						flag = true;
					}
				}
			}
		}
	}
}
