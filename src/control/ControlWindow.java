package control;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import display.DisplayPanel;
import display.DisplayWindow;
import image.ControlImage;
import image.DisplayImagePanel;
import layer.ControlLayer;
import layer.DisplayLayerPanel;
import loading.ControlLoading;
import loading.DisplayLoadingPanel;
import main.Display;
import main.Mode;
import main.Settings;
import paint.ControlPaint;
import paint.DisplayPaintPanel;

public class ControlWindow extends JFrame {
	
	private static final long serialVersionUID = -2980231396321368085L;
	
	private DisplayWindow window;
	
	private Mode controlMode;
	private ControlPanel[] controls;
	private JButton[] controlButtons;
	
	private Mode display;
	private DisplayPanel[] displays;
	private JButton[] displayButtons;
	
	public ControlWindow(Rectangle display, Rectangle control) {
		
		setTitle(Settings.NAME);
		setSize(Settings.CONTROL_SIZE);
		setLocation(
				(control.width - Settings.CONTROL_SIZE.width) / 2 + control.x,
				(control.height - Settings.CONTROL_SIZE.height) / 2 + control.y);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		controlButtons = new JButton[Mode.values().length];
		displayButtons = new JButton[Mode.values().length];
		for (int i = 0; i < controlButtons.length; i++) {
			controlButtons[i] = new JButton(Mode.values()[i].name());
			controlButtons[i].addActionListener(new ButtonListener(Display.CONTROL, Mode.values()[i]));
			displayButtons[i] = new JButton(Mode.values()[i].name());
			displayButtons[i].addActionListener(new ButtonListener(Display.DISPLAY, Mode.values()[i]));
		}
		
		JPanel northPanel = new JPanel(new GridLayout(1, 2));
		northPanel.add(createButtonGroup("Controls", controlButtons));
		northPanel.add(createButtonGroup("Displaying", displayButtons));
		
		add(northPanel, BorderLayout.NORTH);
		
		window = new DisplayWindow(display);
		
		controls = new ControlPanel[Mode.values().length];
		displays = new DisplayPanel[Mode.values().length];
		
		displays[Mode.LAYER.ordinal()] = new DisplayLayerPanel(window);
		controls[Mode.LAYER.ordinal()] = new ControlLayer((DisplayLayerPanel) displays[Mode.LAYER.ordinal()]);
		
		displays[Mode.IMAGE.ordinal()] = new DisplayImagePanel(window);
		controls[Mode.IMAGE.ordinal()] = new ControlImage((DisplayImagePanel)displays[Mode.IMAGE.ordinal()]);
		
		displays[Mode.PAINT.ordinal()] = new DisplayPaintPanel(window);
		controls[Mode.PAINT.ordinal()] = new ControlPaint(
				display.getSize(), (DisplayPaintPanel) displays[Mode.PAINT.ordinal()]);
		
		displays[Mode.LOADING.ordinal()] = new DisplayLoadingPanel(window);
		controls[Mode.LOADING.ordinal()] = new ControlLoading((DisplayLoadingPanel) displays[Mode.LOADING.ordinal()]);
		
		displayButtons[Mode.LOADING.ordinal()].doClick();
		controlButtons[Mode.LAYER.ordinal()].doClick();
		
		setVisible(true);
	}
	
	private JPanel createButtonGroup(String title, JButton[] buttons) {
		JPanel panel = new JPanel();
		panel.setBackground(Settings.CONTROL_BACKGROUND);
		panel.setLayout(new GridLayout(2, 1));
		panel.setBorder(BorderFactory.createLineBorder(Settings.BACKGROUND, 2));
		
		JPanel south = new JPanel();
		south.setBackground(Settings.CONTROL_BACKGROUND);
		south.setLayout(new GridLayout(1, buttons.length));
		
		for (JButton b: buttons) {
			south.add(b);
		}
		
		panel.add(new JLabel(title, SwingConstants.CENTER));
		panel.add(south);
		
		return panel;
	}

	private class ButtonListener implements ActionListener {
		
		private final Display disp;
		private final Mode mode;
		
		public ButtonListener(Display d, Mode m) {
			disp = d;
			mode = m;
		}
		
		public void actionPerformed(ActionEvent arg0) {
			switch (disp) {
				case CONTROL:
					setControl(mode);
					break;
				case DISPLAY:
					setDisplay(mode);
					break;
			}
		}
	}
	
	private void setControl(Mode mode) {
		if (mode != controlMode) {
			for (int i = 0; i < controls.length; i++) {
				remove(controls[i]);
				controlButtons[i].setBackground(Settings.INACTIVE);
			}
			controlMode = mode;
			add(controls[controlMode.ordinal()], BorderLayout.CENTER);
			controlButtons[controlMode.ordinal()].setBackground(Settings.ACTIVE);
			validate();
			repaint();
		}
	}
	
	private void setDisplay(Mode mode) {
		if (mode != display) {
			display = mode;
			for (int i = 0; i < displays.length; i++) {
				window.remove(displays[i]);
				displays[i].setMainDisplay(false);
				displayButtons[i].setBackground(Settings.INACTIVE);
			}
			window.add(displays[display.ordinal()]);
			displays[display.ordinal()].setMainDisplay(true);
			displayButtons[display.ordinal()].setBackground(Settings.ACTIVE);
			window.validate();
			window.repaint();
		}
	}
}