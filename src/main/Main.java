package main;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import control.ControlPanel;
import control.ControlWindow;
import display.DisplayPanel;
import display.DisplayWindow;
import image.ControlImage;
import image.DisplayImagePanel;
import layer.ControlLayer;
import layer.DisplayLayerPanel;
import loading.ControlLoading;
import loading.DisplayLoadingPanel;
import paint.ControlPaint;
import paint.DisplayPaintPanel;

public class Main {
	
	public static ControlWindow CONTROL_WINDOW;
	public static ControlLayer CONTROL_LAYER;
	public static ControlImage CONTROL_IMAGE;
	public static ControlPaint CONTROL_PAINT;
	public static ControlLoading CONTROL_LOADING;
	
	public static DisplayWindow DISPLAY_WINDOW;
	public static DisplayLayerPanel DISPLAY_LAYER;
	public static DisplayImagePanel DISPLAY_IMAGE;
	public static DisplayPaintPanel DISPLAY_PAINT;
	public static DisplayLoadingPanel DISPLAY_LOADING;
	
	public static void main(String[] args) {
		
		try {
			UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			
			JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
		
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
			
			DISPLAY_LAYER = new DisplayLayerPanel();
			DISPLAY_IMAGE = new DisplayImagePanel();
			DISPLAY_PAINT = new DisplayPaintPanel();
			DISPLAY_LOADING = new DisplayLoadingPanel();
			
			CONTROL_LAYER = new ControlLayer();
			CONTROL_IMAGE = new ControlImage();
			CONTROL_PAINT = new ControlPaint();
			CONTROL_LOADING = new ControlLoading();
			
			CONTROL_WINDOW.setButton(Display.CONTROL, Mode.PAINT, true);
			CONTROL_WINDOW.setButton(Display.DISPLAY, Mode.LOADING, true);
			CONTROL_WINDOW.setMode(Mode.PAINT, Mode.IMAGE);
			DISPLAY_WINDOW.setMode(Mode.LOADING, Mode.IMAGE);
			
			DISPLAY_WINDOW.setVisible(true);
			CONTROL_WINDOW.setVisible(true);
		}
	}
	
	public static ControlPanel getControl(Mode mode) {
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
	
	public static DisplayPanel getDisplay(Mode mode) {
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
	
	private static Screen[] getScreens() {
		GraphicsDevice[] graphicsDevice = 
				GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
		Screen[] screens = new Screen[graphicsDevice.length];
		for (int i = 0; i < graphicsDevice.length; i++) {
			screens[i] = new Screen(graphicsDevice[i]);
		}
		return screens;
	}
}