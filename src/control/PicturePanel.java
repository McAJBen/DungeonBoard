package control;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import main.Settings;

/**
 * a scroll menu to display images on as buttons
 * @author McAJBen <McAJBen@gmail.com>
 * @since 1.0
 */
public abstract class PicturePanel extends JPanel {
	
	private static final long serialVersionUID = 2972394170217781329L;
	
	
	/**
	 * The number of picture per row of a picture panel
	 */
	private static final int GRID_WIDTH = 4;
	
	/**
	 * The size of the {@code ImageIcon} in each of the buttons
	 */
	private static final Dimension IMAGE_SIZE = new Dimension(100, 60);
	
	/**
	 * creates an instance of the {@code Picture Panel}
	 */
	public PicturePanel() {
		setLayout(new GridLayout(0, GRID_WIDTH));
		setBorder(BorderFactory.createEmptyBorder());
	}
	
	/**
	 * creates a button for a {@code PicturePanel} by loading an image from file
	 * @param file the file of an image to add
	 * @return a button with the proper settings for a {@code PicturePanel}
	 */
	public JButton createPPButton(File file) {
		createThumbnail(file);
		JButton button = new JButton(file.getName());
		button.setMargin(new Insets(0, 0, 0, 0));
		button.setFocusPainted(false);
		button.setVerticalTextPosition(SwingConstants.TOP);
		button.setHorizontalTextPosition(SwingConstants.CENTER);
		button.setBackground(Settings.DISABLE_COLOR);
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JButton button = (JButton) arg0.getSource();
				String name = button.getText();
				if (button.getBackground() == Settings.DISABLE_COLOR) {
					select(name);
					button.setBackground(Settings.ENABLE_COLOR);
				}
				else {
					deselect(name);
					button.setBackground(Settings.DISABLE_COLOR);
				}
			}
		});
		return button;
	}
	
	/**
	 * resizes an image and saves a lower quality version as a thumbnail
	 * @param file the file input of the full size image
	 */
	private void createThumbnail(File file) {
		File tFile = Settings.fileToThumb(file);
		if (tFile.exists()) {
			tFile.delete();
		}
		try {
			BufferedImage bufferedImage = new BufferedImage(IMAGE_SIZE.width, IMAGE_SIZE.height, BufferedImage.TYPE_INT_RGB);
			bufferedImage.getGraphics().drawImage(
					ImageIO.read(file).getScaledInstance(IMAGE_SIZE.width, IMAGE_SIZE.height, BufferedImage.SCALE_SMOOTH),
					0, 0, null);
			ImageIO.write(bufferedImage, "GIF", tFile);
			tFile.deleteOnExit();
		} catch (OutOfMemoryError | IOException e) {
			Settings.showError("Cannot create Thumbnail, file is probably too large", e);
			e.printStackTrace();
		}
	}
	
	/**
	 * removes all images
	 */
	public void clearButtons() {
		for (Component c: getComponents()) {
			if (c.getClass().equals(JButton.class)) {
				remove(c);
			}
		}
	}
	
	/**
	 * called when an image is selected
	 * @param name the name of the image
	 */
	protected abstract void select(String name);
	
	/**
	 * called when an image is deselected
	 * @param name the name of the image
	 */
	protected abstract void deselect(String name);

	/**
	 * loads the thumbnails from file
	 * @param folder the folder that the original image was in
	 */
	public void rememberThumbnails(File folder) {
		for (Component c: getComponents()) {
			if (c.getClass().equals(JButton.class)) {
				JButton b = (JButton) c;
				File f = new File(folder + File.separator + b.getText());
				f = Settings.fileToThumb(f);
				try {
					b.setIcon(new ImageIcon(ImageIO.read(f)));
				} catch (OutOfMemoryError | IOException e) {
					Settings.showError("Cannot load Thumbnail, file is probably too large", e);
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * removes the thumbnails from local memory
	 */
	public void forgetThumbnails() {
		for (Component c: getComponents()) {
			if (c.getClass().equals(JButton.class)) {
				JButton b = (JButton) c;
				b.setIcon(null);
			}
		}
	}
}