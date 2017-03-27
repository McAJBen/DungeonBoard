package layer;

import java.awt.BorderLayout;
import java.awt.Color;
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
import main.FileChooser;
import main.Mode;
import main.Settings;

public class ControlLayer extends ControlPanel {
	
	private static final long serialVersionUID = 111613405297226375L;
	
	private DisplayLayerPanel layerDisplay;
	private PicturePanel pp;
	private JLabel folder;
	
	public ControlLayer(DisplayLayerPanel layerDisplay) {
		this.layerDisplay = layerDisplay;
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
		
		JComboBox<Scale> scaleComboBox = new JComboBox<>(Scale.values());
		scaleComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				layerDisplay.setScaleMode((Scale) scaleComboBox.getSelectedItem());
			}
		});
		northPanel.add(scaleComboBox);
		
		JButton showOneButton = new JButton("Show One");
		showOneButton.setBackground(Settings.INACTIVE);
		showOneButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (showOneButton.getBackground() == Settings.ACTIVE) {
					layerDisplay.setShowOne(false);
					showOneButton.setBackground(Settings.INACTIVE);
				}
				else if (showOneButton.getBackground() == Settings.INACTIVE) {
					layerDisplay.setShowOne(true);
					showOneButton.setBackground(Settings.ACTIVE);
				}
			}
		});
		northPanel.add(showOneButton);
		
		folder = new JLabel();
		northPanel.add(folder);
		
		pp = new PicturePanel(layerDisplay);
		
		add(northPanel, BorderLayout.NORTH);
		
		JScrollPane jsp = new JScrollPane(pp);
		jsp.setBorder(BorderFactory.createEmptyBorder());
		add(jsp, BorderLayout.CENTER);
		
		setDirectory(Settings.FOLDERS[Mode.LAYER.ordinal()]);
		
		setVisible(true);
	}
	
	public void setDirectory(File folder) {
		if (folder != null && folder.exists()) {
			this.folder.setText(folder.getPath());
			layerDisplay.setFolder(folder);
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
