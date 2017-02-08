package main;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.io.File;

import javax.swing.JOptionPane;

import control.ControlWindow;

public class Main {
	
	public static void main(String[] args) {
		
		makeDirs();
		
		new ControlWindow(getDisplays());
	}
	
	private static void makeDirs() {
		boolean created = true;
		
		File[] folders = {
				new File(System.getProperty("user.dir") + "\\DungeonBoard\\Layer"),
				new File(System.getProperty("user.dir") + "\\DungeonBoard\\Paint"),
				new File(System.getProperty("user.dir") + "\\DungeonBoard\\Loading")};
		
		for (File f: folders) {
			if (!f.exists()) {
				created = false;
			}
		}
		if (!created) {
			int response = JOptionPane.showConfirmDialog(
					null, 
					"Would you like to make a 'DungeonBoard' folder \nfor easier access and loading?",
					"Dungeon Board",
					JOptionPane.YES_NO_OPTION);
			if (response == 0) {
				for (File f: folders) {
					f.mkdirs();
				}
			}
		}
	}
	
	private static String[] toLabels(GraphicsDevice[] graphicsDevice) {
		String[] strings = new String[graphicsDevice.length];
		for (int i = 0; i < graphicsDevice.length; i++) {
			Dimension size = graphicsDevice[i].getDefaultConfiguration().getBounds().getSize();
			strings[i] = graphicsDevice[i].getIDstring().substring(1) + 
					"  " + size.width + "x" + size.height; 
		}
		return strings;
	}

	private static Rectangle[] getDisplays() {
		GraphicsDevice[] screens = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
		
		if (screens.length < 1) {
			JOptionPane.showMessageDialog(null, "Not enough screens!");
			System.exit(-1);
		}
		int displayIndex = JOptionPane.showOptionDialog(null, "Select Display Window", "Dungeon Board",
				JOptionPane.DEFAULT_OPTION,
				JOptionPane.QUESTION_MESSAGE,
				null, toLabels(screens), 0);
		if (displayIndex < 0) {
			System.exit(-2);
		}
		
		int controlIndex = displayIndex == 0 ? screens.length - 1 : 0;
		
		return new Rectangle[] {screens[displayIndex].getDefaultConfiguration().getBounds(), 
				screens[controlIndex].getDefaultConfiguration().getBounds()};
	}
}