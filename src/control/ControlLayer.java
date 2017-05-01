package control;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import display.Scale;
import main.Main;
import main.Mode;
import main.Settings;

public class ControlLayer extends Control {
	
	private static final long serialVersionUID = 111613405297226375L;
	
	private PicturePanel pp;
	
	public ControlLayer() {
		JPanel northPanel = getNorthPanel();
		
		JComboBox<Scale> scaleComboBox = new JComboBox<>(Scale.values());
		scaleComboBox.setBackground(Settings.CONTROL_BACKGROUND);
		scaleComboBox.setMaximumSize(new Dimension(100, 5000));
		scaleComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Main.DISPLAY_LAYER.setScaleMode(scaleComboBox.getSelectedItem());
			}
		});
		northPanel.add(scaleComboBox);
		
		pp = new PicturePanel() {
			
			private static final long serialVersionUID = 2972394170217781329L;
			
			@Override
			protected void select(String name) {
				Main.DISPLAY_LAYER.addImage(name);
			}

			@Override
			protected void deselect(String name) {
				Main.DISPLAY_LAYER.removeImage(name);
			}
		};
		
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
					pp.addButton(f);
				}
			}
			repaint();
			revalidate();
			Main.DISPLAY_LAYER.removeAllImages();
		}
	}
}