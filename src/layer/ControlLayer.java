package layer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import control.ControlPanel;
import main.Main;
import main.Mode;
import main.Settings;

public class ControlLayer extends ControlPanel {
	
	private static final long serialVersionUID = 111613405297226375L;
	
	private PictureLayerPanel pp;
	
	public ControlLayer() {
		JPanel northPanel = getNorthPanel();
		
		JComboBox<Scale> scaleComboBox = new JComboBox<>(Scale.values());
		scaleComboBox.setBackground(Settings.CONTROL_BACKGROUND);
		scaleComboBox.setMaximumSize(new Dimension(100, 5000));
		scaleComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Main.DISPLAY_LAYER.setScaleMode((Scale) scaleComboBox.getSelectedItem());
			}
		});
		northPanel.add(scaleComboBox);
		
		pp = new PictureLayerPanel();
		
		add(northPanel, BorderLayout.NORTH);
		
		JScrollPane jsp = new JScrollPane(pp);
		jsp.setBackground(Settings.CONTROL_BACKGROUND);
		jsp.setBorder(BorderFactory.createEmptyBorder());
		add(jsp, BorderLayout.CENTER);
		
		load();
		
		setVisible(true);
	}
	
	@Override
	protected void load() {
		File folder = Settings.FOLDERS[Mode.LAYER.ordinal()];
		if (folder.exists()) {
			pp.clearImages();
			for (File f: folder.listFiles()) {
				String name = f.getName();
				String suffix = name.substring(name.lastIndexOf('.') + 1);
				if (suffix.equalsIgnoreCase("PNG") || suffix.equalsIgnoreCase("JPG") || suffix.equalsIgnoreCase("JPEG")) {
					pp.addImage(f);
				}
			}
			repaint();
			revalidate();
			Main.DISPLAY_LAYER.removeAllImages();
		}
	}
}