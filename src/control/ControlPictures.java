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

/**
 * a {@code Control} for the Layer and Image Utility
 * @author McAJBen <McAJBen@gmail.com>
 * @since 2.0
 */
public class ControlPictures extends Control {

	private static final long serialVersionUID = -1679600820663944136L;
	
	/**
	 * the scroll menu of images inside the folder
	 */
	private PicturePanel pp;
	
	/**
	 * the folder which images are selected from
	 */
	private final File folder;
	
	/**
	 * the display to post images to
	 */
	private final DisplayPictures display;
	
	/**
	 * creates an instance of the {@code ControlPaint} class
	 * @param folder the folder that images are loaded from
	 * @param display the display to post images to
	 * @param allowList if more than 1 image should be allowed<br>
	 * - true will be for Layer Utility<br>
	 * - false will be for Image Utility
	 */
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
	public void setMainControl(boolean b) {
		if (b) {
			pp.rememberThumbnails(folder);
		}
		else {
			pp.forgetThumbnails();
		}
	}
	
	@Override
	protected void load() {
		if (folder.exists()) {
			pp.clearButtons();
			
			PPButtonCreator ppbc = new PPButtonCreator(pp, folder);
			ppbc.run();
			
			repaint();
			revalidate();
			display.removeAllImages();
		}
	}
}