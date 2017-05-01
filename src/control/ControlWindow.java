package control;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Rectangle;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import main.Display;
import main.Main;
import main.Mode;
import main.Settings;

public class ControlWindow extends JFrame {
	
	private static final long serialVersionUID = -2980231396321368085L;
	
	private JButton[] controlButtons;
	private JButton[] displayButtons;
	
	public ControlWindow(Rectangle control) {
		
		setIconImage(Settings.ICON.getImage());
		setTitle(Settings.NAME);
		setSize(Settings.CONTROL_SIZE);
		setLocation(
				(control.width - Settings.CONTROL_SIZE.width) / 2 + control.x,
				(control.height - Settings.CONTROL_SIZE.height) / 2 + control.y);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		controlButtons = new JButton[Mode.values().length];
		displayButtons = new JButton[Mode.values().length];
		for (int i = 0; i < controlButtons.length; i++) {
			controlButtons[i] = Settings.createButton(Mode.values()[i].name());
			controlButtons[i].setBackground(Settings.INACTIVE);
			controlButtons[i].addActionListener(new ModeListener(Display.CONTROL, Mode.values()[i]));
			
			displayButtons[i] = Settings.createButton(Mode.values()[i].name());
			displayButtons[i].setBackground(Settings.INACTIVE);
			displayButtons[i].addActionListener(new ModeListener(Display.DISPLAY, Mode.values()[i]));
		}
		
		JPanel northPanel = new JPanel(new GridLayout(1, 2));
		northPanel.add(createButtonGroup("Controls", controlButtons));
		northPanel.add(createButtonGroup("Displaying", displayButtons));
		
		add(northPanel, BorderLayout.NORTH);
	}
	
	private JPanel createButtonGroup(String title, JButton[] buttons) {
		JPanel panel = new JPanel();
		panel.setBackground(Settings.CONTROL_BACKGROUND);
		panel.setLayout(new GridLayout(2, 1));
		panel.setBorder(BorderFactory.createLineBorder(Settings.BACKGROUND, 2));
		
		JPanel south = new JPanel();
		south.setBackground(Settings.CONTROL_BACKGROUND);
		south.setLayout(new GridLayout(1, buttons.length));
		
		for (int i = 0; i < buttons.length; i++) {
			south.add(buttons[i]);
		}
		
		panel.add(new JLabel(title, SwingConstants.CENTER));
		panel.add(south);
		
		return panel;
	}
	
	public void setMode(Mode newMode, Mode oldMode) {
		remove(Main.getControl(oldMode));
		add(Main.getControl(newMode), BorderLayout.CENTER);
		validate();
		repaint();
	}
	
	public void setButton(Display display, Mode mode, boolean value) {
		switch (display) {
		case CONTROL:
			controlButtons[mode.ordinal()].setBackground(value ? Settings.ACTIVE : Settings.INACTIVE);
			break;
		case DISPLAY:
			displayButtons[mode.ordinal()].setBackground(value ? Settings.ACTIVE : Settings.INACTIVE);
			break;
		}
	}
}