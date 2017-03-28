package loading;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import control.ControlPanel;
import main.FileChooser;
import main.Mode;
import main.Settings;

public class ControlLoading extends ControlPanel {
	
	private static final long serialVersionUID = 5986059033234358609L;
	
	private DisplayLoadingPanel loadingDisplay;
	private JLabel folder;
	
	public ControlLoading(DisplayLoadingPanel loadingDisplay) {
		this.loadingDisplay = loadingDisplay;
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createLineBorder(Settings.BACKGROUND, 5));
		JPanel northPanel = new JPanel();
		northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.X_AXIS));
		FileChooser fc = new FileChooser();
		fc.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setDirectory(fc.getFolder());
			}
		});
		northPanel.add(fc);
		
		JButton upScaleButton = new JButton("Up Scale");
		upScaleButton.setBackground(Settings.INACTIVE);
		upScaleButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (upScaleButton.getBackground() == Settings.ACTIVE) {
					loadingDisplay.setUpScale(false);
					upScaleButton.setBackground(Settings.INACTIVE);
				}
				else if (upScaleButton.getBackground() == Settings.INACTIVE) {
					loadingDisplay.setUpScale(true);
					upScaleButton.setBackground(Settings.ACTIVE);
				}
			}
		});
		northPanel.add(upScaleButton);
		
		JLabel timeLabel = new JLabel("8");
		timeLabel.setMinimumSize(new Dimension(20, 0));
		timeLabel.setHorizontalAlignment(SwingConstants.CENTER);
		northPanel.add(timeLabel);
		
		JSlider timeSlider = new JSlider(SwingConstants.HORIZONTAL, 1, 20, 8);
		timeSlider.setMinimumSize(new Dimension(100, 0));
		timeSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				timeLabel.setText(String.format("%d", timeSlider.getValue()));
				loadingDisplay.setTotalWait(timeSlider.getValue());
			}
		});
		northPanel.add(timeSlider);
		
		folder = new JLabel();
		northPanel.add(folder);
		
		add(northPanel, BorderLayout.NORTH);
		
		setDirectory(Settings.FOLDERS[Mode.LOADING.ordinal()]);
		
		setVisible(true);
	}
	
	private void setDirectory(File folder) {
		if (folder != null && folder.exists()) {
			this.folder.setText(folder.getPath());
			loadingDisplay.setDirectory(folder);
		}
	}
}