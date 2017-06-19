package control;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import main.Window;
import main.Main;
import main.Mode;

/**
 * an {@code ActionListener} class for buttons that change the control or display window
 * @author McAJBen <McAJBen@gmail.com>
 * @since 1.0
 */
public class ModeListener implements ActionListener {
	
	/**
	 * the specific display to change to when this button is pressed
	 */
	private final Window disp;
	
	/**
	 * the specific mode to change to when this button is pressed
	 */
	private final Mode mode;
	
	/**
	 * creates an instance of the {@code ModeListner} class
	 * @param disp the display this button is for
	 * @param mode the mode this button is for
	 */
	public ModeListener(Window disp, Mode mode) {
		this.disp = disp;
		this.mode = mode;
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		Main.changeButton(disp, mode);
	}
}