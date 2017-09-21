package control;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
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
		folderControlPanel.setVisible(false);
		
		JPanel innerNorthPanel = getNorthPanel();
		
		maxZoom = 10.0;
		
		drawPanel = new DrawPanel();
		
		setFocusable(true);
		
		fileBox = new JComboBox<>();
		fileBox.setBackground(Settings.CONTROL_BACKGROUND);
		fileBox.addItem("");
		load();
		fileBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (fileBox.getSelectedIndex() != 0) {
					File file = new File(
							Settings.FOLDERS[Mode.PAINT.ordinal()].getAbsolutePath() +
							File.separator + fileBox.getSelectedItem().toString());
					
					if (file.exists()) {
						drawPanel.saveMask();
						File maskFile = Settings.fileToMaskFile(file);
						File dataFile = new File(Settings.DATA_FOLDER + File.separator + "Paint" + File.separator + maskFile.getName() + ".data");
						if (dataFile.exists()) {
							try {
								BufferedReader br = new BufferedReader(new FileReader(dataFile));
								
								String data[] = br.readLine().split(" ");
								double zoom = Double.parseDouble(data[0]);
								Point p = new Point(Integer.parseInt(data[1]), Integer.parseInt(data[2]));
								zoomSlider.setMaximum(10_000);
								zoomSlider.setValue((int) (zoom * 100));
								zoomText.setText(String.format("%.2f", zoom));
								drawPanel.setWindow(zoom, p);
								br.close();
							} catch (IOException e2) {
								Settings.showError("Cannot load Mask Data", e2);
							}
						}
						if (file.isDirectory()) {
							folderControlPanel.setVisible(true);
							setFolder(file);
						}
						else {
							folderControlPanel.setVisible(false);
							setFile(file);
						}
					}
					else {
						Settings.showError("Cannot load Image, file does not exist");
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
				zoomSlider.setValue((int) (zoom * 100));
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
	private void setFolder(File folder) {
		Settings.PAINT_FOLDER = folder;
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
				if (!fileName.equalsIgnoreCase("Guide.png")) {
					System.out.println("File not in correct format: " + fileName + 
							". Should be named with a number, ex: '" + Settings.PAINT_FOLDER_SIZE + ".png'");
				}
			}
		}
		
		Settings.PAINT_IMAGES = new boolean[Settings.PAINT_FOLDER_SIZE];
		
		folderControlPanel.removeAll();
		// creates all buttons
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
									
									Graphics2D g2d = Settings.PAINT_IMAGE.createGraphics();
									g2d.setColor(Color.BLACK);
									g2d.fillRect(0, 0, Settings.PAINT_IMAGE.getWidth(), Settings.PAINT_IMAGE.getHeight());
									
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
		
		// loads guide
		File guide = new File(Settings.PAINT_FOLDER.getAbsolutePath() + "/Guide.png");
		
		if (Settings.PAINT_FOLDER != null && Settings.PAINT_FOLDER.exists()) {
			drawPanel.setImageLoading(true);
			Thread folderLoadingThread = new Thread("folderLoadingThread") {
				public void run() {
					try {
						Settings.PAINT_IMAGE = null;
						Settings.PAINT_CONTROL_IMAGE = null;
						Dimension imageSize;
						{
							BufferedImage guideImg = ImageIO.read(guide);
							imageSize = new Dimension(guideImg.getWidth(), guideImg.getHeight());
							Settings.PAINT_CONTROL_IMAGE = new BufferedImage(
									imageSize.width / Settings.PAINT_GUIDE_SCALE,
									imageSize.height / Settings.PAINT_GUIDE_SCALE,
									BufferedImage.TYPE_INT_RGB);
							Settings.PAINT_CONTROL_IMAGE.getGraphics().drawImage(guideImg.getScaledInstance(
									imageSize.width / Settings.PAINT_GUIDE_SCALE,
									imageSize.height / Settings.PAINT_GUIDE_SCALE,
									BufferedImage.SCALE_SMOOTH), 0, 0, null);
						}
						Settings.PAINT_IMAGE = new BufferedImage(
								imageSize.width,
								imageSize.height,
								BufferedImage.TYPE_INT_ARGB);
						drawPanel.setImage();
						Main.DISPLAY_PAINT.setMask(drawPanel.getMask());
						Main.DISPLAY_PAINT.setImageSize();
						setZoomMax();
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
	private void setFile(File file) {
		drawPanel.setImageLoading(true);
		Settings.PAINT_FOLDER = file;
		Thread fileLoadingThread = new Thread("fileLoadingThread") {
			public void run() {
				try {
					Settings.PAINT_IMAGE = null;
					Settings.PAINT_IMAGE = ImageIO.read(file);
					Settings.PAINT_CONTROL_IMAGE = Settings.PAINT_IMAGE;
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
		fileLoadingThread.start();
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
	
	@Override
	protected void load() {
		while (fileBox.getItemCount() > 1) {
			fileBox.removeItemAt(1);
		}
		File folder = Settings.FOLDERS[Mode.PAINT.ordinal()];
		
		if (folder.exists()) {
			for (File f: folder.listFiles()) {
				if (f.isDirectory()) {
					fileBox.addItem(f.getName());
				}
				else {
					String name = f.getName();
					String suffix = name.substring(name.lastIndexOf('.') + 1);
					if (suffix.equalsIgnoreCase("PNG") || suffix.equalsIgnoreCase("JPG") || suffix.equalsIgnoreCase("JPEG")) {
						fileBox.addItem(name);
					}
				}
			}
		}
	}

	/**
	 * saves the mask in {@code DrawPanel} to file
	 */
	public void saveMask() {
		drawPanel.saveMask();
	}
}