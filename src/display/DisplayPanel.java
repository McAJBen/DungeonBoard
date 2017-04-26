package display;

import java.awt.Graphics2D;

import javax.swing.JPanel;

import main.Main;

public class DisplayPanel extends JPanel {

	private static final long serialVersionUID = 3464890244015717841L;
	
	public DisplayPanel() {
		
	}
	
	protected void paintMouse(Graphics2D g2d) {
		Main.DISPLAY_WINDOW.paintMouse(g2d);
	}

	public void setMainDisplay(boolean b) {
		
	}
}