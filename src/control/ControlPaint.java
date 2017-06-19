package control;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import main.Main;
import main.Mode;
import main.Settings;
import paint.DrawPanel;

/**
 * a {@code Control} for the Paint Utility
 * @author McAJBen <McAJBen@gmail.com>
 * @since 1.0
 */
public class ControlPaint extends Control {
	
	private static final long serialVersionUID = -3231530555502467648L;
	
	/**
	 * the main panel the user draws onto to create a mask
	 */
	private DrawPanel drawPanel;
	
	/**
	 * the drop down menu for selecting a file
	 */
	private JComboBox<String> fileBox;
	
	/**
	 * the text field for changing zoom
	 */
	private JTextField zoomText;
	
	/**
	 * the slider for changing zoom
	 */
	private JSlider zoomSlider;
	
	/**
	 * the maximum zoom this image is allowed
	 */
	private double maxZoom;
	
	/**
	 * the {@code JPanel} holding options for which file in a folder to display
	 */
	private JPanel folderControlPanel;
	
	/**
	 * creates an instance of the {@code ControlPaint} class
	 */
	public ControlPaint() {
		
		JPanel northPanel = new JPanel();
		northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.Y_AXIS));
		
		folderControlPanel = getEmptyNorthPanel();
		folderControlPanel.setVisible(Settings.PAINT_FOLDER_MODE);
		
		JPanel innerNorthPanel = getNorthPanel();
		
		maxZoom = 10.0;
		
		drawPanel = new DrawPanel();
		
		setFocusable(true);
		
		JButton settingsButton = Settings.createButton(Settings.ICON_SETTINGS);
		settingsButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showSettings();
			}
		});
		innerNorthPanel.add(settingsButton);
		
		fileBox = new JComboBox<>();
		fileBox.setBackground(Settings.CONTROL_BACKGROUND);
		fileBox.addItem("");
		load();
		fileBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!fileBox.getSelectedItem().equals("")) {
					if (Settings.PAINT_FOLDER_MODE) {
						setFolder(fileBox.getSelectedItem().toString());
					}
					else {
						setFile(fileBox.getSelectedItem().toString());
					}
				}
			}
		});
		innerNorthPanel.add(fileBox);
		
		JButton drawStyleButton = Settings.createButton(Settings.DRAW_STYLE[0]);
		drawStyleButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				drawPanel.toggleStyle();
				drawStyleButton.setIcon(Settings.DRAW_STYLE[drawPanel.getStyle()]);
			}
		});
		innerNorthPanel.add(drawStyleButton);
		
		JButton shape = Settings.createButton(Settings.PEN_TYPE[0]);
		shape.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				drawPanel.togglePen();
				shape.setIcon(Settings.PEN_TYPE[drawPanel.getPen()]);
			}
		});
		innerNorthPanel.add(shape);
		
		JButton drawModeButton = Settings.createButton(Settings.DRAW_MODE[0]);
		drawModeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				drawPanel.toggleDrawMode();
				drawModeButton.setIcon(Settings.DRAW_MODE[drawPanel.getDrawMode()]);
			}
		});
		innerNorthPanel.add(drawModeButton);
		
		JButton showButton = Settings.createButton("Show");
		showButton.setBackground(Settings.ACTIVE);
		showButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				drawPanel.showAll();
			}
		});
		innerNorthPanel.add(showButton);
		
		JButton hideButton = Settings.createButton("Hide");
		hideButton.setBackground(Settings.INACTIVE);
		hideButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				drawPanel.hideAll();
			}
		});
		innerNorthPanel.add(hideButton);
		
		JSlider slider = new JSlider(SwingConstants.HORIZONTAL, 10, 100, 25);
		slider.setBackground(Settings.CONTROL_BACKGROUND);
		slider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				drawPanel.setRadius(slider.getValue());
			}
		});
		innerNorthPanel.add(slider);
		
		innerNorthPanel.add(drawPanel.getUpdateButton());
		
		JPanel westPanel = new JPanel();
		westPanel.setBackground(Settings.CONTROL_BACKGROUND);
		westPanel.setLayout(new BoxLayout(westPanel, BoxLayout.Y_AXIS));
		
		westPanel.add(new JLabel("Zoom", SwingConstants.LEFT));
		
		zoomText = new JTextField("1.00", 1);
		zoomText.setMaximumSize(new Dimension(5000, 25));
		zoomText.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				double zoom = 1;
				try {
					zoom = Double.parseDouble(zoomText.getText());
					if (zoom < 0.01) {
						zoom = 0.01;
					}
					else if (zoom > maxZoom) {
						zoom = maxZoom;
					}
					drawPanel.setZoom(zoom);
				}
				catch (NumberFormatException nfe) {
					zoom = zoomSlider.getValue() / 100.0;
				}
				zoomText.setText(String.format("%.2f", zoom));
			}
		});
		westPanel.add(zoomText);
		
		zoomSlider = new JSlider(SwingConstants.VERTICAL, 1, (int)(maxZoom * 100), 100);
		zoomSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				double zoom = zoomSlider.getValue() / 100.0;
				if (zoom < 0.01) {
					zoom = 0.01;
				}
				else if (zoom > maxZoom) {
					zoom = maxZoom;
				}
				zoomText.setText(String.format("%.2f", zoom));
				drawPanel.setZoom(zoom);
			}
		});
		westPanel.add(zoomSlider);
		
		northPanel.add(folderControlPanel);
		northPanel.add(innerNorthPanel);
		
		add(westPanel, BorderLayout.WEST);
		add(northPanel, BorderLayout.NORTH);
		add(drawPanel, BorderLayout.CENTER);
		
		setVisible(true);
	}
	
	/**
	 * sets up the selected folder for painting<br>
	 * loads it into control and the display
	 * @param name the folder name to load
	 */
	private void setFolder(String name) {

		Settings.PAINT_FOLDER = new File(Settings.FOLDERS[Mode.PAINT.ordinal()].getAbsolutePath() +  "/" + name);
		
		File guide = new File(Settings.PAINT_FOLDER.getAbsolutePath() + "/Guide.png");
		
		Settings.PAINT_FOLDER_SIZE = 0;
		for (File f: Settings.PAINT_FOLDER.listFiles(File::isFile)) {
			String fileName = f.getName();
			String prefix = fileName.substring(0, fileName.lastIndexOf('.'));
			String suffix = fileName.substring(fileName.lastIndexOf('.') + 1);
			try {
				if (suffix.equalsIgnoreCase("PNG") && Integer.parseInt(prefix) == Settings.PAINT_FOLDER_SIZE + 1) {
					Settings.PAINT_FOLDER_SIZE++;
				}
			} catch (NumberFormatException e) {
				// file not in valid format, therefore ignore
			}
		}
		
		Settings.PAINT_IMAGES = new boolean[Settings.PAINT_FOLDER_SIZE];
		
		folderControlPanel.removeAll();
		for (int i = 1; i <= Settings.PAINT_FOLDER_SIZE; i++) {
			JButton button = Settings.createButton(i + "");
			button.setBackground(Settings.INACTIVE);
			button.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					int number = Integer.parseInt(((JButton) e.getSource()).getText());
					
					if (button.getBackground().equals(Settings.ACTIVE)) {
						button.setBackground(Settings.INACTIVE);
						Settings.PAINT_IMAGES[number - 1] = false;
					}
					else {
						button.setBackground(Settings.ACTIVE);
						Settings.PAINT_IMAGES[number - 1] = true;
					}
					Thread fileLoadingThread = new Thread("fileLoadingThread") {
						public void run() {
							try {
								synchronized (Settings.PAINT_IMAGE) {
									Settings.PAINT_IMAGE = new BufferedImage(
											Settings.PAINT_IMAGE.getWidth(),
											Settings.PAINT_IMAGE.getHeight(),
											Settings.PAINT_IMAGE.getType());
									
									Graphics2D g2d = Settings.PAINT_IMAGE.createGraphics();
									
									for (int i = Settings.PAINT_FOLDER_SIZE; i > 0; i--) {
										if (Settings.PAINT_IMAGES[i - 1]) {
											File f = new File(Settings.PAINT_FOLDER + "/" + i + ".png");
											g2d.drawImage(ImageIO.read(f), 0, 0, null);
										}
									}
									
									g2d.dispose();
									
									if (Settings.PAINT_IMAGE != null) {
										Main.DISPLAY_PAINT.setMask(drawPanel.getMask());
										Main.DISPLAY_PAINT.setImageSize();
										setZoomMax();
									}
								}
							} catch (IOException | OutOfMemoryError error) {
								drawPanel.resetImage();
								Main.DISPLAY_PAINT.resetImage();
								Settings.PAINT_IMAGE = null;
								Settings.showError("Cannot load Image, file is probably too large", error);
							}
							Main.DISPLAY_PAINT.repaint();
							drawPanel.repaint();
							drawPanel.setImageLoading(false);
						}
					};
					fileLoadingThread.start();
				}
			});
			folderControlPanel.add(button);
		}
		folderControlPanel.revalidate();
		
		if (Settings.PAINT_FOLDER != null && Settings.PAINT_FOLDER.exists()) {
			drawPanel.setImageLoading(true);
			Thread folderLoadingThread = new Thread("folderLoadingThread") {
				public void run() {
					try {
						Settings.PAINT_IMAGE = null;
						Settings.PAINT_IMAGE = ImageIO.read(guide);
						if (Settings.PAINT_IMAGE != null) {
							drawPanel.setImage();
							Main.DISPLAY_PAINT.setMask(drawPanel.getMask());
							Main.DISPLAY_PAINT.setImageSize();
							setZoomMax();
						}
					} catch (IOException | OutOfMemoryError error) {
						drawPanel.resetImage();
						Main.DISPLAY_PAINT.resetImage();
						Settings.PAINT_IMAGE = null;
						Settings.showError("Cannot load Image, file is probably too large", error);
					}
					Main.DISPLAY_PAINT.repaint();
					drawPanel.repaint();
					drawPanel.setImageLoading(false);
				}
			};
			folderLoadingThread.start();
		}
	}

	/**
	 * sets up the selected file for painting<br>
	 * loads it into control and the display
	 * @param name the file name to load
	 */
	private void setFile(String name) {
		
		File file = new File(Settings.FOLDERS[Mode.PAINT.ordinal()].getAbsolutePath() +  "/" + name);
		
		if (file.exists()) {
			drawPanel.setImageLoading(true);
			Thread fileLoadingThread = new Thread("fileLoadingThread") {
				public void run() {
					try {
						Dimension oldImageSize = new Dimension(0, 0);
						if (Settings.PAINT_IMAGE != null) {
							oldImageSize = new Dimension(Settings.PAINT_IMAGE.getWidth(), Settings.PAINT_IMAGE.getHeight());
						}
						Settings.PAINT_IMAGE = null;
						Settings.PAINT_IMAGE = ImageIO.read(file);
						if (Settings.PAINT_IMAGE != null) {
							
							if (oldImageSize == null ||
									Settings.PAINT_IMAGE.getWidth() != oldImageSize.getWidth() ||
									Settings.PAINT_IMAGE.getHeight() != oldImageSize.getHeight() ||
									JOptionPane.showConfirmDialog(drawPanel,
										"Would you like to keep the same visibility mask?",
										"Paint Image has been changed",
										JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) {
								
								drawPanel.setImage();
							}
							
							Main.DISPLAY_PAINT.setMask(drawPanel.getMask());
							Main.DISPLAY_PAINT.setImageSize();
							setZoomMax();
						}
					} catch (IOException | OutOfMemoryError error) {
						drawPanel.resetImage();
						Main.DISPLAY_PAINT.resetImage();
						Settings.PAINT_IMAGE = null;
						Settings.showError("Cannot load Image, file is probably too large", error);
					}
					Main.DISPLAY_PAINT.repaint();
					drawPanel.repaint();
					drawPanel.setImageLoading(false);
				}
			};
			fileLoadingThread.start();
		}
		else {
			Settings.showError("Cannot load Image, file does not exist");
		}
	}
	
	/**
	 * changes the maximum zoom so the image cannot be smaller than the screen
	 */
	private void setZoomMax() {
		double w = Settings.PAINT_IMAGE.getWidth() / Settings.DISPLAY_SIZE.getWidth();
		double h = Settings.PAINT_IMAGE.getHeight() / Settings.DISPLAY_SIZE.getHeight();
		maxZoom = h > w ? h : w;
		zoomSlider.setMaximum((int) (maxZoom * 100));
	}
	
	/**
	 * opens the settings dialog
	 */
	public void showSettings() {
		JDialog settings = new JDialog(Main.getControl(), "Settings", true);
		settings.setLocationRelativeTo(Main.getControl());
		settings.setSize(new Dimension(400, 400));
		settings.setLayout(new BoxLayout(settings.getContentPane(), BoxLayout.Y_AXIS));
		
		JPanel paintMaskPanel = new JPanel();
		paintMaskPanel.setLayout(new BoxLayout(paintMaskPanel, BoxLayout.X_AXIS));
		paintMaskPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		paintMaskPanel.add(new JLabel("Paint Mask Quality: "));
		JLabel maskQualityLabel = new JLabel(Settings.PIXELS_PER_MASK + "");
		paintMaskPanel.add(maskQualityLabel);
		JSlider maskQualitySlider = new JSlider(JSlider.HORIZONTAL, 1, 20, Settings.PIXELS_PER_MASK);
		maskQualitySlider.setMajorTickSpacing(5);
		maskQualitySlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				maskQualityLabel.setText(maskQualitySlider.getValue() + "");
			}
		});
		paintMaskPanel.add(maskQualitySlider);
		settings.add(paintMaskPanel);
		
		JPanel allowFolderPanel = new JPanel();
		allowFolderPanel.setLayout(new BoxLayout(allowFolderPanel, BoxLayout.X_AXIS));
		allowFolderPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		allowFolderPanel.add(new JLabel("Paint with a Folder?"));
		JCheckBox allowFolderCheckBox = new JCheckBox();
		allowFolderCheckBox.setSelected(Settings.PAINT_FOLDER_MODE);
		allowFolderPanel.add(allowFolderCheckBox);
		settings.add(allowFolderPanel);
		
		JButton saveButton = Settings.createButton("Save");
		saveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Settings.PIXELS_PER_MASK = maskQualitySlider.getValue();
				Settings.PAINT_FOLDER_MODE = allowFolderCheckBox.isSelected();
				folderControlPanel.setVisible(Settings.PAINT_FOLDER_MODE);
				load();
				drawPanel.setImage();
				settings.dispose();
			}
		});
		settings.add(saveButton);
		
		settings.setVisible(true);
	}

	@Override
	protected void load() {
		while (fileBox.getItemCount() > 1) {
			fileBox.removeItemAt(1);
		}
		File folder = Settings.FOLDERS[Mode.PAINT.ordinal()];
		
		if (folder.exists()) {
			if (Settings.PAINT_FOLDER_MODE) {
				for (File f: folder.listFiles(File::isDirectory)) {
					String name = f.getName();
					fileBox.addItem(name);
				}
			}
			else {
				for (File f: folder.listFiles(File::isFile)) {
					String name = f.getName();
					String suffix = name.substring(name.lastIndexOf('.') + 1);
					if (suffix.equalsIgnoreCase("PNG") || suffix.equalsIgnoreCase("JPG") || suffix.equalsIgnoreCase("JPEG")) {
						fileBox.addItem(name);
					}
				}
			}
		}
	}
}