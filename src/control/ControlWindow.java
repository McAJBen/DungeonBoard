package control;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import display.DisplayPanel;
import display.DisplayWindow;
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
		
		JPanel northPanel = new JPanel();
		northPanel.setLayout(new GridLayout());
		
		controlButtons = new JButton[3];
		for (int i = 0; i < 3; i++) {
			controlButtons[i] = new JButton(Mode.values()[i].name());
			controlButtons[i].addActionListener(new ButtonListener(Display.CONTROL, Mode.values()[i]));
			northPanel.add(controlButtons[i]);
		}
		
		northPanel.add(new JLabel());
		
		displayButtons = new JButton[3];
		for (int i = 0; i < 3; i++) {
			displayButtons[i] = new JButton(Mode.values()[i].name());
			displayButtons[i].addActionListener(new ButtonListener(Display.DISPLAY, Mode.values()[i]));
			northPanel.add(displayButtons[i]);
		}
		
		add(northPanel, BorderLayout.NORTH);
		
		window = new DisplayWindow(display);
		
		controls = new ControlPanel[3];
		displays = new DisplayPanel[3];
		
		displays[0] = new DisplayLayerPanel();
		controls[0] = new ControlLayer((DisplayLayerPanel) displays[0]);
		
		displays[1] = new DisplayPaintPanel();
		controls[1] = new ControlPaint(display.getSize(), (DisplayPaintPanel) displays[1]);
		
		displays[2] = new DisplayLoadingPanel();
		controls[2] = new ControlLoading((DisplayLoadingPanel) displays[2]);
		
		displayButtons[2].doClick();
		controlButtons[0].doClick();
		
		setVisible(true);
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
			for (int i = 0; i < 3; i++) {
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
			for (int i = 0; i < 3; i++) {
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