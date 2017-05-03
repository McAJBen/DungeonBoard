package display;

import java.awt.Graphics2D;
import java.awt.Point;
import java.util.Random;

import main.Settings;

/**
 * an image that floats around the screen like the old DvD logo would on DvD players
 * @author McAJBen <McAJBen@gmail.com>
 * @since 1.9
 */
public class Cube {
	
	/**
	 * how many pixels per movement the cube makes
	 */
	private final int SPEED;
	
	/**
	 * the current position of the top left corner
	 */
	private Point point;
	
	/**
	 * the vertical velocity direction<br>
	 * - true moves up<br>
	 * - false moves down
	 */
	private boolean vertical;
	
	/**
	 * the horizontal velocity direction<br>
	 * - true moves left<br>
	 * - false moves right
	 */
	private boolean horizontal;
	
	/**
	 * a flag for if the cube has 'hit the corner'<br>
	 * - true if it is in the corner<br>
	 * - false if it is still floating around
	 */
	private boolean flag;
	
	/**
	 * creates as instance of {@code Cube} within the window
	 */
	public Cube() {
		Random rand = new Random();
		SPEED = rand.nextInt(5) + 1;
		point = new Point(
				rand.nextInt(Settings.DISPLAY_SIZE.width - Settings.ICON_DVD.getIconWidth()),
				rand.nextInt(Settings.DISPLAY_SIZE.height - Settings.ICON_DVD.getIconHeight()));
		vertical = rand.nextBoolean();
		horizontal = rand.nextBoolean();
		flag = false;
	}
	
	/**
	 * paints the cube to the screen
	 * @param g2d the graphics to paint onto
	 */
	public void paint(Graphics2D g2d) {
		if (flag) {
			g2d.drawImage(Settings.ICON_DVD2.getImage(), point.x, point.y, null);
		}
		else {
			g2d.drawImage(Settings.ICON_DVD.getImage(), point.x, point.y, null);
		}
	}
	
	/**
	 * moves the cube one motion
	 */
	public void move() {
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
				if (point.x > Settings.DISPLAY_SIZE.width - Settings.ICON_DVD.getIconWidth()) {
					point.x = Settings.DISPLAY_SIZE.width - Settings.ICON_DVD.getIconWidth();
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
				if (point.y > Settings.DISPLAY_SIZE.height - Settings.ICON_DVD.getIconHeight()) {
					point.y = Settings.DISPLAY_SIZE.height - Settings.ICON_DVD.getIconHeight();
					horizontal = true;
					if (vertHit) {
						flag = true;
					}
				}
			}
		}
	}
}