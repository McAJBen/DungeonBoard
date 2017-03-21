package loading;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import control.ControlPanel;
import main.FileChooser;

public class ControlLoading extends ControlPanel {
	private static final long serialVersionUID = 1L;
	
	private DisplayLoadingPanel loadingDisplay;
	private JLabel folder;
	
	public ControlLoading(DisplayLoadingPanel loadingDisplay) {
		this.loadingDisplay = loadingDisplay;
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createLineBorder(Color.GRAY, 10));
		JPanel northPanel = new JPanel();
		northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.X_AXIS));
		FileChooser fc = new FileChooser();
		fc.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setDirectory(fc.getFolder());
			}
		});
		northPanel.add(fc);
		
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
		
		setDirectory(new File(System.getProperty("user.dir") + "\\DungeonBoard\\Loading"));
		
		setVisible(true);
	}
	
	private void setDirectory(File folder) {
		if (folder != null && folder.exists()) {
			this.folder.setText(folder.getPath());
			loadingDisplay.setDirectory(folder);
		}
	}
}