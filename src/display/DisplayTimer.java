package display;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;

import main.Main;

/**
 * container for count down timer
 * @author McAJBen <McAJBen@gmail.com>
 * @since 2.3.3
 */
public class DisplayTimer {
	
	/**
	 * width of the timer block
	 */
	private static final int TIMER_WIDTH = 220;
	
	/**
	 * width added for extra length timers
	 */
	private static final int TIMER_WIDTH_MULT = 60;
	
	/**
	 * height of the timer block
	 */
	private static final int TIMER_HEIGHT = 90;
	
	/**
	 * the number of seconds to display a completed timer
	 */
	private static final int TIMER_DONE_TIME = 60;
	
	/**
	 * the font used for {@code DisplayTimer}
	 */
	private static final Font FONT = new Font("TimesRoman", Font.BOLD, 120);
	
	/**
	 * the number of seconds the timer has left
	 */
	private int timerSeconds;
	
	/**
	 * the thread that is repainting the {@code DisplayTimer}
	 * and calculating timers and keeping track of time
	 */
	private Thread paintThread;
	
	/**
	 * the x position of the left side of {@code DisplayTimer}
	 */
	private final int left;
	
	/**
	 * the y position of the top side of {@code DisplayTimer}
	 */
	private final int top;
	
	/**
	 * the y position of the bottom side of {@code DisplayTimer}
	 */
	private final int bottom;
	
	/**
	 * creates an instance of {@code DisplayTimer}
	 * @param size the size of the {@code JFrame}
	 */
	public DisplayTimer(Dimension size) {
		left = (size.width - TIMER_WIDTH) / 2;
		top = size.height - TIMER_HEIGHT;
		bottom = size.height - 4;
		timerSeconds = -TIMER_DONE_TIME;
		paintThread = new Thread();
	}
	
	/**
	 * draws the timer to a {@code Graphics2D}
	 * @param g2d the {@code Graphics2D} to paint to
	 */
	public void paint(Graphics2D g2d) {
		if (timerEnabled()) {
			g2d.setFont(FONT);
			if (timerSeconds <= 0) {
				paintTimer(g2d, Color.RED, 0);
			}
			else {
				paintTimer(g2d, Color.LIGHT_GRAY, timerSeconds);
			}
		}
	}
	
	/**
	 * paints the number of seconds to a point on screen
	 * @param g2d the graphics to draw to
	 * @param background the color for the background
	 * @param seconds the number of seconds to display
	 */
	private void paintTimer(Graphics2D g2d, Color background, int seconds) {
		int digits = Math.max((int)Math.log10(seconds / 60), 0);
		int x = left - TIMER_WIDTH_MULT * digits / 2;
		int w = TIMER_WIDTH + TIMER_WIDTH_MULT * digits;
		g2d.setColor(background);
		g2d.fillRoundRect(x, top, w, TIMER_HEIGHT, 15, 15);
		g2d.setColor(Color.BLACK);
		g2d.drawString(
				String.format("%d:%02d", (int)(seconds / 60), seconds % 60),
				x, bottom);
	}
	
	/**
	 * tells if the timer is currently on
	 * @return true if the timer is counting down or at 0
	 */
	public boolean timerEnabled() {
		return timerSeconds > -TIMER_DONE_TIME;
	}

	/**
	 * enables a timer for a set amount of seconds
	 * @param seconds number of seconds for the timer to count down
	 */
	public void setTimer(int seconds) {
		clearTimer();
		timerSeconds = seconds + 1;
		paintThread = new Thread("paintThread") {
			@Override
			public void run() {
				while (timerEnabled()) {
					try {
						timerSeconds--;
						Main.getDisplay().repaint();
						sleep(1000);
					} catch (InterruptedException e) {
						break;
					}
				}
			}
		};
		paintThread.start();
	}
	
	/**
	 * removes the timer from being displayed
	 */
	public void clearTimer() {
		timerSeconds = -TIMER_DONE_TIME;
		paintThread.interrupt();
		try {
			paintThread.join();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		Main.getDisplay().repaint();
	}
}