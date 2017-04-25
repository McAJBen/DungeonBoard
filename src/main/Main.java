package main;

import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.io.File;
import javax.swing.JOptionPane;
import control.ControlWindow;

public class Main {
	
	public static void main(String[] args) {
		
		Settings.load();
		
		for (File f: Settings.FOLDERS) {
			f.mkdirs();
		}
		
		GraphicsDevice[] screens = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
		
		int displayIndex = JOptionPane.showOptionDialog(null, "Select Display Window", Settings.NAME,
				JOptionPane.DEFAULT_OPTION,
				JOptionPane.QUESTION_MESSAGE,
				null, toLabels(screens), 0);
		
		if (displayIndex >= 0 && displayIndex < screens.length) {
			int controlIndex = (displayIndex == 0 ? screens.length - 1 : 0);
			Settings.DISPLAY_SIZE = screens[displayIndex].getDefaultConfiguration().getBounds().getSize();
			Settings.CONTROL_WINDOW = new ControlWindow(
					screens[displayIndex].getDefaultConfiguration().getBounds(), 
					screens[controlIndex].getDefaultConfiguration().getBounds());
		}
	}
	
	private static String[] toLabels(GraphicsDevice[] gd) {
		String[] strings = new String[gd.length];
		for (int i = 0; i < gd.length; i++) {
			Dimension size = gd[i].getDefaultConfiguration().getBounds().getSize();
			strings[i] = gd[i].getIDstring().substring(1) + 
					"  " + size.width + "x" + size.height; 
		}
		return strings;
	}
}