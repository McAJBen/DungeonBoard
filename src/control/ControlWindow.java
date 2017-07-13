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

import main.Window;
import main.Main;
import main.Mode;
import main.Settings;

/**
 * a {@code JFrame} for controlling the whole program
 * @author McAJBen <McAJBen@gmail.com>
 * @since 1.0
 */
public class ControlWindow extends JFrame {
	
	private static final long serialVersionUID = -2980231396321368085L;
	
	/**
	 * an array of the buttons to change control mode
	 */
	private JButton[] controlButtons;
	
	/**
	 * an array of the buttons to change display mode
	 */
	private JButton[] displayButtons;
	
	/**
	 * creates an instance of the {@code ControlWindow} class
	 * @param r the position and dimension of the screen
	 */
	public ControlWindow(Rectangle r) {
		
		setIconImage(Settings.ICON.getImage());
		setTitle(Settings.NAME);
		setSize(Settings.CONTROL_SIZE);
		setLocation(
				(r.width - Settings.CONTROL_SIZE.width) / 2 + r.x,
				(r.height - Settings.CONTROL_SIZE.height) / 2 + r.y);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		controlButtons = new JButton[Mode.values().length];
		displayButtons = new JButton[Mode.values().length];
		for (int i = 0; i < controlButtons.length; i++) {
			controlButtons[i] = Settings.createButton(Mode.values()[i].name());
			controlButtons[i].setBackground(Settings.INACTIVE);
			controlButtons[i].addActionListener(new ModeListener(Window.CONTROL, Mode.values()[i]));
			
			displayButtons[i] = Settings.createButton(Mode.values()[i].name());
			displayButtons[i].setBackground(Settings.INACTIVE);
			displayButtons[i].addActionListener(new ModeListener(Window.DISPLAY, Mode.values()[i]));
		}
		
		JPanel northPanel = new JPanel(new GridLayout(1, 2));
		northPanel.add(createButtonGroup("Controls", controlButtons));
		northPanel.add(createButtonGroup("Displaying", displayButtons));
		
		add(northPanel, BorderLayout.NORTH);
	}
	
	/**
	 * combines buttons into a menu with a header. This is a mock radio button group
	 * @param title the title for the header
	 * @param buttons the buttons to place inside the group
	 * @return a {@code JPanel} which is formatted for a mock radio button group
	 */
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
	
	/**
	 * changes the mode of control being displayed
	 * @param newMode the new mode to display
	 * @param oldMode the old mode displayed before
	 */
	public void setMode(Mode newMode, Mode oldMode) {
		remove(Main.getControl(oldMode));
		Main.getControl(oldMode).setMainControl(false);
		add(Main.getControl(newMode), BorderLayout.CENTER);
		Main.getControl(newMode).setMainControl(true);
		validate();
		repaint();
	}
	
	/**
	 * changes the background of a button
	 * @param display whether it is a display or a control button
	 * @param mode the button mode
	 * @param value true for active, false for inactive
	 */
	public void setButton(Window display, Mode mode, boolean value) {
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