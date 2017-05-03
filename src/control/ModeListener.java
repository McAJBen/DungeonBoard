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
	 * the current display mode.
	 * Used to tell which button to disable
	 */
	private static Mode displayMode = Mode.LOADING;
	
	/**
	 * the current control mode.
	 * Used to tell which button to disable
	 */
	private static Mode controlMode = Mode.PAINT;
	
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
		switch (disp) {
		case CONTROL:
			synchronized (controlMode) {
				if (controlMode != mode) {
					Main.CONTROL_WINDOW.setButton(disp, controlMode, false);
					Main.CONTROL_WINDOW.setMode(mode, controlMode);
					controlMode = mode;
					Main.CONTROL_WINDOW.setButton(disp, controlMode, true);
				}
			}
			break;
		case DISPLAY:
			synchronized (displayMode) {
				if (displayMode != mode) {
					Main.CONTROL_WINDOW.setButton(disp, displayMode, false);
					Main.DISPLAY_WINDOW.setMode(mode, displayMode);
					displayMode = mode;
					Main.CONTROL_WINDOW.setButton(disp, displayMode, true);
				}
			}
			break;
		}
	}
}