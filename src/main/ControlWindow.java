package main;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import layer.ControlLayer;
import layer.DisplayLayerPanel;
import loading.ControlLoading;
import loading.DisplayLoadingPanel;
import paint.ControlPaint;
import paint.DisplayPaintPanel;

public class ControlWindow extends JFrame {
	
	private static final long serialVersionUID = 1L;
	private static final Dimension SIZE = new Dimension(676, 571);
	private static final Color ACTIVE = new Color(153, 255, 187);
	private static final Color NOT_ACTIVE = new Color(255, 128, 128);
	
	private static enum Display {CONTROL, DISPLAY};
	private static enum Mode {LAYER, PAINT, LOADING};
	
	private DisplayWindow window;
	
	private Mode controlMode;
	private ControlPanel[] controls;
	private JButton[] controlButtons;
	
	private Mode display;
	private DisplayPanel[] displays;
	private JButton[] displayButtons;
	
	public ControlWindow(Rectangle[] r) {
		setTitle("Dungeon Board");
		setSize(SIZE);
		setLocation((r[1].width - SIZE.width) / 2 + r[1].x, (r[1].height - SIZE.height) / 2 + r[1].y);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		controls = new ControlPanel[3];
		controlButtons = new JButton[3];
		displays = new DisplayPanel[3];
		displayButtons = new JButton[3];
		
		JPanel northPanel = new JPanel();
		northPanel.setLayout(new GridLayout());
		
		for (int i = 0; i < 3; i++) {
			controlButtons[i] = new JButton(Mode.values()[i].name());
			controlButtons[i].addActionListener(new ButtonListener(Display.CONTROL, Mode.values()[i]));
			northPanel.add(controlButtons[i]);
		}
		northPanel.add(new JLabel());
		for (int i = 0; i < 3; i++) {
			displayButtons[i] = new JButton(Mode.values()[i].name());
			displayButtons[i].addActionListener(new ButtonListener(Display.DISPLAY, Mode.values()[i]));
			northPanel.add(displayButtons[i]);
		}
		add(northPanel, BorderLayout.NORTH);
		window = new DisplayWindow(r[0]);
		
		
		displays[0] = new DisplayLayerPanel();
		controls[0] = new ControlLayer((DisplayLayerPanel) displays[0]);
		
		displays[1] = new DisplayPaintPanel();
		controls[1] = new ControlPaint(r[0].getSize(), (DisplayPaintPanel) displays[1]);
		
		displays[2] = new DisplayLoadingPanel();
		controls[2] = new ControlLoading((DisplayLoadingPanel) displays[2]);
		
		displayButtons[0].doClick();
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
				controlButtons[i].setBackground(NOT_ACTIVE);
			}
			controlMode = mode;
			add(controls[controlMode.ordinal()], BorderLayout.CENTER);
			controlButtons[controlMode.ordinal()].setBackground(ACTIVE);
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
				displayButtons[i].setBackground(NOT_ACTIVE);
			}
			window.add(displays[display.ordinal()]);
			displays[display.ordinal()].setMainDisplay(true);
			displayButtons[display.ordinal()].setBackground(ACTIVE);
			window.validate();
			window.repaint();
		}
	}
}