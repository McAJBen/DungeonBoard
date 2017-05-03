package main;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;

import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import control.ControlLoading;
import control.ControlPaint;
import control.ControlPictures;
import control.Control;
import control.ControlWindow;
import display.DisplayLoading;
import display.DisplayPaint;
import display.DisplayPictures;
import display.Display;
import display.DisplayWindow;

/**
 * contains most of the starting code for Dungeon Board
 * @author McAJBen <McAJBen@gmail.com>
 * @since 1.0
 */
public class Main {
	
	/**
	 * {@code JFrame} for control window
	 */
	public static ControlWindow CONTROL_WINDOW;
	
	/**
	 * {@code JPanel} for controlling Layer Utility
	 */
	public static ControlPictures CONTROL_LAYER;
	
	/**
	 * {@code JPanel} for controlling Image Utility
	 */
	public static ControlPictures CONTROL_IMAGE;
	
	/**
	 * {@code JPanel} for controlling Paint Utility
	 */
	public static ControlPaint CONTROL_PAINT;
	
	/**
	 * {@code JPanel} for controlling Loading Utility
	 */
	public static ControlLoading CONTROL_LOADING;
	
	/**
	 * {@code JFrame} for display window
	 */
	public static DisplayWindow DISPLAY_WINDOW;
	
	/**
	 * {@code JPanel} for displaying Layer Utility
	 */
	public static DisplayPictures DISPLAY_LAYER;
	
	/**
	 * {@code JPanel} for displaying Image Utility
	 */
	public static DisplayPictures DISPLAY_IMAGE;
	
	/**
	 * {@code JPanel} for displaying Paint Utility
	 */
	public static DisplayPaint DISPLAY_PAINT;
	
	/**
	 * {@code JPanel} for displaying Loading Utility
	 */
	public static DisplayLoading DISPLAY_LOADING;
	
	public static void main(String[] args) {
		
		try {
			UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
			UIManager.put("Button.background", Settings.CONTROL_BACKGROUND);
			UIManager.put("Button.opaque", true);
			UIManager.put("OptionPane.background", Settings.BACKGROUND);
			UIManager.put("Panel.background", Settings.BACKGROUND);
			UIManager.put("Slider.background", Settings.CONTROL_BACKGROUND);
			
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			
			JOptionPane.showMessageDialog(null, e.getMessage(), "Error - Changing look and feel", JOptionPane.ERROR_MESSAGE);
		}
		
		try {
			Settings.load();
			
			Screen[] screens = getScreens();
			
			int displayIndex = JOptionPane.showOptionDialog(null, "Select Display Window", Settings.NAME,
					JOptionPane.DEFAULT_OPTION,
					JOptionPane.QUESTION_MESSAGE,
					null, screens, 0);
			
			if (displayIndex >= 0 && displayIndex < screens.length) {
				
				int controlIndex = (displayIndex == 0 ? screens.length - 1 : 0);
				
				Settings.DISPLAY_SIZE = screens[displayIndex].getSize();
				
				DISPLAY_WINDOW = new DisplayWindow(
						screens[displayIndex].getRectangle());
				
				CONTROL_WINDOW = new ControlWindow(
						screens[controlIndex].getRectangle());
				
				DISPLAY_LAYER = new DisplayPictures(Settings.FOLDERS[Mode.LAYER.ordinal()]);
				DISPLAY_IMAGE = new DisplayPictures(Settings.FOLDERS[Mode.IMAGE.ordinal()]);
				DISPLAY_PAINT = new DisplayPaint();
				DISPLAY_LOADING = new DisplayLoading();
				
				CONTROL_LAYER = new ControlPictures(Settings.FOLDERS[Mode.LAYER.ordinal()], DISPLAY_LAYER, true);
				CONTROL_IMAGE = new ControlPictures(Settings.FOLDERS[Mode.IMAGE.ordinal()], DISPLAY_IMAGE, false);
				CONTROL_PAINT = new ControlPaint();
				CONTROL_LOADING = new ControlLoading();
				
				CONTROL_WINDOW.setButton(Window.CONTROL, Mode.PAINT, true);
				CONTROL_WINDOW.setButton(Window.DISPLAY, Mode.LOADING, true);
				CONTROL_WINDOW.setMode(Mode.PAINT, Mode.IMAGE);
				DISPLAY_WINDOW.setMode(Mode.LOADING, Mode.IMAGE);
				
				DISPLAY_WINDOW.setVisible(true);
				CONTROL_WINDOW.setVisible(true);
			}
		} catch (SecurityException e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "Error - Loading resources", JOptionPane.ERROR_MESSAGE);
		} catch (HeadlessException e) {
			System.out.println("Error - Cannot find any screens\n" + e.getMessage());
		}
	}
	
	/**
	 * @param mode The {@code Mode} of the {@code JPanel} that will be returned
	 * @return The {@code JPanel} which contains the controls for the given {@code Mode}
	 */
	public static Control getControl(Mode mode) {
		switch (mode) {
		case IMAGE:
			return CONTROL_IMAGE;
		case LAYER:
			return CONTROL_LAYER;
		case LOADING:
			return CONTROL_LOADING;
		case PAINT:
			return CONTROL_PAINT;
		default:
			return null;
		}
	}
	
	/**
	 * @param mode The {@code Mode} of the {@code JPanel} that will be returned
	 * @return The {@code JPanel} which contains the display for the given {@code Mode}
	 */
	public static Display getDisplay(Mode mode) {
		switch (mode) {
		case IMAGE:
			return DISPLAY_IMAGE;
		case LAYER:
			return DISPLAY_LAYER;
		case LOADING:
			return DISPLAY_LOADING;
		case PAINT:
			return DISPLAY_PAINT;
		default:
			return null;
		}
	}
	
	/**
	 * 
	 * @return an array of all the {@code Screens} usable by the system
	 * @throws HeadlessException if the environment does not support a display
	 */
	private static Screen[] getScreens() throws HeadlessException {
		GraphicsDevice[] graphicsDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
		Screen[] screens = new Screen[graphicsDevice.length];
		for (int i = 0; i < graphicsDevice.length; i++) {
			screens[i] = new Screen(graphicsDevice[i]);
		}
		return screens;
	}
}