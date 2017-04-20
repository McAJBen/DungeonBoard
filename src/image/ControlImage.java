package image;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import control.ControlPanel;
import layer.Scale;
import main.FileChooser;
import main.Mode;
import main.Settings;

public class ControlImage extends ControlPanel {

	private static final long serialVersionUID = 3622994265203390348L;
	
	private DisplayImagePanel imageDisplay;
	private PictureImagePanel pp;
	private JLabel folder;
	
	public ControlImage(DisplayImagePanel imageDisplay) {
		this.imageDisplay = imageDisplay;
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createLineBorder(Settings.BACKGROUND, 5));
		JPanel northPanel = new JPanel();
		northPanel.setBackground(Settings.CONTROL_BACKGROUND);
		northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.X_AXIS));
		
		FileChooser fc = Settings.createFileChooser();
		fc.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setDirectory(fc.getFolder());
			}
		});
		northPanel.add(fc);
		
		JComboBox<Scale> scaleComboBox = new JComboBox<>(Scale.values());
		scaleComboBox.setBackground(Settings.CONTROL_BACKGROUND);
		scaleComboBox.setMaximumSize(new Dimension(100, 5000));
		scaleComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				imageDisplay.setScaleMode((Scale) scaleComboBox.getSelectedItem());
			}
		});
		northPanel.add(scaleComboBox);
		
		JButton flipButton = Settings.createButton(Settings.ICON_FLIP);
		flipButton.setBackground(Settings.CONTROL_BACKGROUND);
		flipButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				imageDisplay.flip();
			}
		});
		northPanel.add(flipButton);
		
		folder = new JLabel();
		folder.setBackground(Settings.CONTROL_BACKGROUND);
		northPanel.add(folder);
		
		pp = new PictureImagePanel(imageDisplay);
		
		add(northPanel, BorderLayout.NORTH);
		
		JScrollPane jsp = new JScrollPane(pp);
		jsp.setBackground(Settings.CONTROL_BACKGROUND);
		jsp.setBorder(BorderFactory.createEmptyBorder());
		add(jsp, BorderLayout.CENTER);
		
		setDirectory(Settings.FOLDERS[Mode.IMAGE.ordinal()]);
		
		setVisible(true);
	}
	
	public void setDirectory(File folder) {
		if (folder != null && folder.exists()) {
			this.folder.setText(folder.getPath());
			imageDisplay.setFolder(folder);
			pp.clearImages();
			for (File f: folder.listFiles()) {
				String name = f.getName();
				String suffix = name.substring(name.lastIndexOf('.') + 1);
				if (suffix.equalsIgnoreCase("PNG")) {
					pp.addImage(f);
				}
			}
			repaint();
			revalidate();
		}
	}
}
