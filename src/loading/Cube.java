package loading;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.Random;

import main.Settings;

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
				rand.nextInt(windowSize.width - Settings.cube.getIconWidth()),
				rand.nextInt(windowSize.height - Settings.cube.getIconHeight()));
		vertical = rand.nextBoolean();
		horizontal = rand.nextBoolean();
		flag = false;
	}
	
	public void paint(Graphics2D g, Dimension windowSize) {
		if (flag) {
			g.drawImage(Settings.cube2.getImage(), point.x, point.y, null);
		}
		else {
			g.drawImage(Settings.cube.getImage(), point.x, point.y, null);
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
				if (point.x > windowSize.width - Settings.cube.getIconWidth()) {
					point.x = windowSize.width - Settings.cube.getIconWidth();
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
				if (point.y > windowSize.height - Settings.cube.getIconHeight()) {
					point.y = windowSize.height - Settings.cube.getIconHeight();
					horizontal = true;
					if (vertHit) {
						flag = true;
					}
				}
			}
		}
	}
}
