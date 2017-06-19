package display;

import java.awt.Graphics2D;

import javax.swing.JPanel;

import main.Main;

/**
 * a {@code JPanel} for all Displays
 * @author McAJBen <McAJBen@gmail.com>
 * @since 1.0
 */
public abstract class Display extends JPanel {

	private static final long serialVersionUID = 3464890244015717841L;
	
	/**
	 * paints the mouse over the screen
	 * @param g2d the graphics to paint onto
	 */
	protected void paintMouse(Graphics2D g2d) {
		Main.getDisplay().paintMouse(g2d);
	}

	/**
	 * tells the Display if it is visible
	 * @param b <br>
	 * - true if it is now the main display<br>
	 * - false if it is not the main display
	 */
	public void setMainDisplay(boolean b) {
		
	}
}