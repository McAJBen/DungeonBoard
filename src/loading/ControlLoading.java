package loading;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import control.ControlPanel;
import main.Main;
import main.Mode;
import main.Settings;

public class ControlLoading extends ControlPanel {
	
	private static final long serialVersionUID = 5986059033234358609L;
	
	private JLabel folder;
	
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
		
		JLabel timeLabel = new JLabel("8");
		timeLabel.setBackground(Settings.CONTROL_BACKGROUND);
		timeLabel.setMinimumSize(new Dimension(20, 0));
		timeLabel.setHorizontalAlignment(SwingConstants.CENTER);
		northPanel.add(timeLabel);
		
		JSlider timeSlider = new JSlider(SwingConstants.HORIZONTAL, 1, 20, 8);
		timeSlider.setBackground(Settings.CONTROL_BACKGROUND);
		timeSlider.setMinimumSize(new Dimension(100, 0));
		timeSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				timeLabel.setText(String.format("%d", timeSlider.getValue()));
				Main.DISPLAY_LOADING.setTotalWait(timeSlider.getValue());
			}
		});
		northPanel.add(timeSlider);
		
		folder = new JLabel();
		folder.setBackground(Settings.CONTROL_BACKGROUND);
		northPanel.add(folder);
		
		add(northPanel, BorderLayout.NORTH);
		
		setDirectory(Settings.FOLDERS[Mode.LOADING.ordinal()]);
		
		setVisible(true);
	}
	
	protected void setDirectory(File folder) {
		if (folder != null && folder.exists()) {
			this.folder.setText(folder.getPath());
			Main.DISPLAY_LOADING.setDirectory(folder);
		}
	}
}