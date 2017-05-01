package control;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import display.DisplayPictures;
import display.Scale;
import main.Settings;

public class ControlPictures extends Control {

	private static final long serialVersionUID = -1679600820663944136L;
	
	private PicturePanel pp;
	
	private final File folder;
	private final DisplayPictures display;
	
	public ControlPictures(File folder, DisplayPictures display, boolean allowList) {
		this.folder = folder;
		this.display = display;
		
		JPanel northPanel = getNorthPanel();
		
		JComboBox<Scale> scaleComboBox = new JComboBox<>(Scale.values());
		scaleComboBox.setBackground(Settings.CONTROL_BACKGROUND);
		scaleComboBox.setMaximumSize(new Dimension(100, 5000));
		scaleComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				display.setScaleMode(scaleComboBox.getSelectedItem());
			}
		});
		northPanel.add(scaleComboBox);
		
		JButton flipButton = Settings.createButton(Settings.ICON_FLIP);
		flipButton.setBackground(Settings.CONTROL_BACKGROUND);
		flipButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				display.flip();
			}
		});
		northPanel.add(flipButton);
		
		pp = new PicturePanel() {
			
			private static final long serialVersionUID = 2972394170217781329L;
			
			@Override
			protected void select(String name) {
				if (!allowList) {
					display.removeAllImages();
					for (Component c: getComponents()) {
						c.setBackground(Settings.DISABLE_COLOR);
					}
				}
				display.addImage(name);
			}

			@Override
			protected void deselect(String name) {
				display.removeImage(name);
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
			display.removeAllImages();
		}
	}
}