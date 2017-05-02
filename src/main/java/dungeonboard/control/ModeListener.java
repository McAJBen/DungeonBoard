package dungeonboard.control;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import dungeonboard.Window;
import dungeonboard.Main;
import dungeonboard.Mode;

public class ModeListener implements ActionListener {
	
	private static Mode displayMode = Mode.LOADING;
	private static Mode controlMode = Mode.PAINT;
	
	private final Window disp;
	private final Mode mode;
	
	public ModeListener(Window disp, Mode mode) {
		this.disp = disp;
		this.mode = mode;
	}
	
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