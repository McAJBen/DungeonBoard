package control;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import main.Main;
import main.Settings;

/**
 * a {@code Control} for the Loading Utility
 * @author McAJBen <McAJBen@gmail.com>
 * @since 1.0
 */
public class ControlLoading extends Control {
	
	private static final long serialVersionUID = 5986059033234358609L;
	
	/**
	 * creates an instance of {@code ControlLoading}
	 */
	public ControlLoading() {
		JPanel northPanel = getNorthPanel();
		
		JButton upScaleButton = Settings.createButton("Up Scale");
		upScaleButton.setBackground(Settings.INACTIVE);
		upScaleButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (upScaleButton.getBackground() == Settings.ACTIVE) {
					Main.DISPLAY_LOADING.setUpScale(false);
					upScaleButton.setBackground(Settings.INACTIVE);
				}
				else if (upScaleButton.getBackground() == Settings.INACTIVE) {
					Main.DISPLAY_LOADING.setUpScale(true);
					upScaleButton.setBackground(Settings.ACTIVE);
				}
			}
		});
		northPanel.add(upScaleButton);
		
		JButton addCubeButton = Settings.createButton("Add Cube");
		addCubeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Main.DISPLAY_LOADING.addCube();
			}
		});
		northPanel.add(addCubeButton);
		
		JButton clearCubeButton = Settings.createButton("Clear Cubes");
		clearCubeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Main.DISPLAY_LOADING.clearCubes();
			}
		});
		northPanel.add(clearCubeButton);
		
		JLabel timeLabel = new JLabel("08");
		timeLabel.setBackground(Settings.CONTROL_BACKGROUND);
		timeLabel.setHorizontalAlignment(SwingConstants.CENTER);
		northPanel.add(timeLabel);
		
		JSlider timeSlider = new JSlider(SwingConstants.HORIZONTAL, 1, 20, 8);
		timeSlider.setBackground(Settings.CONTROL_BACKGROUND);
		timeSlider.setMinimumSize(new Dimension(100, 0));
		timeSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				timeLabel.setText(String.format("%02d", timeSlider.getValue()));
				Main.DISPLAY_LOADING.setTotalWait(timeSlider.getValue());
			}
		});
		northPanel.add(timeSlider);
		
		JButton createTimerButton = Settings.createButton("Create Timer");
		createTimerButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String input = JOptionPane.showInputDialog(Main.getControl(), "Enter minutes or M:SS", "");
				try {
					int seconds = 0;
					if (input.contains(":")) {
						String[] split = input.split(":");
						seconds += Integer.parseInt(split[0]) * 60;
						seconds += Integer.parseInt(split[1]);
					}
					else {
						seconds += Integer.parseInt(input) * 60;
					}
					Main.getDisplay().setTimer(seconds);
				} catch (NumberFormatException | NullPointerException e2) {
					
				}
			}
		});
		northPanel.add(createTimerButton);
		
		JButton clearTimerButton = Settings.createButton("Clear Timer");
		clearTimerButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Main.getDisplay().clearTimer();
			}
		});
		northPanel.add(clearTimerButton);
		
		add(northPanel, BorderLayout.NORTH);
		
		setVisible(true);
	}

	@Override
	protected void load() {
		
	}
}