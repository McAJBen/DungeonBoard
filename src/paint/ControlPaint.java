package paint;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import control.ControlPanel;
import main.Main;
import main.Mode;
import main.Settings;

public class ControlPaint extends ControlPanel {
	
	private static final long serialVersionUID = -3231530555502467648L;
	
	private DrawPanel drawPanel;
	
	private JComboBox<String> fileBox;
	private JTextField zoomText;
	private JSlider zoomSlider;
	
	private double maxZoom;
	
	public ControlPaint() {
		JPanel northPanel = getNorthPanel();
		
		maxZoom = 10.0;
		
		drawPanel = new DrawPanel();
		
		setFocusable(true);
		
		JButton settingsButton = Settings.createButton(Settings.ICON_SETTINGS);
		settingsButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Settings.showSettings(drawPanel);
			}
		});
		northPanel.add(settingsButton);
		
		fileBox = new JComboBox<>();
		fileBox.addItem("");
		fileBox.setBackground(Settings.CONTROL_BACKGROUND);
		File folder = Settings.FOLDERS[Mode.PAINT.ordinal()];
		if (folder.exists()) {
			for (File f: folder.listFiles()) {
				String name = f.getName();
				String suffix = name.substring(name.lastIndexOf('.') + 1);
				if (suffix.equalsIgnoreCase("PNG") || suffix.equalsIgnoreCase("JPG") || suffix.equalsIgnoreCase("JPEG")) {
					fileBox.addItem(name);
				}
			}
		}
		fileBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!fileBox.getSelectedItem().equals("")) {
					setFile((String) fileBox.getSelectedItem());
				}
			}
		});
		northPanel.add(fileBox);
		
		JButton drawStyleButton = Settings.createButton(Settings.DRAW_STYLE[0]);
		drawStyleButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				drawPanel.toggleStyle();
				drawStyleButton.setIcon(Settings.DRAW_STYLE[drawPanel.getStyle()]);
			}
		});
		northPanel.add(drawStyleButton);
		
		JButton shape = Settings.createButton(Settings.PEN_TYPE[0]);
		shape.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				drawPanel.togglePen();
				shape.setIcon(Settings.PEN_TYPE[drawPanel.getPen()]);
			}
		});
		northPanel.add(shape);
		
		JButton drawModeButton = Settings.createButton(Settings.DRAW_MODE[0]);
		drawModeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				drawPanel.toggleDrawMode();
				drawModeButton.setIcon(Settings.DRAW_MODE[drawPanel.getDrawMode()]);
			}
		});
		northPanel.add(drawModeButton);
		
		JSlider slider = new JSlider(SwingConstants.HORIZONTAL, 10, 100, 25);
		slider.setBackground(Settings.CONTROL_BACKGROUND);
		slider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				drawPanel.setRadius(slider.getValue());
			}
		});
		northPanel.add(slider);
		
		northPanel.add(drawPanel.getUpdateButton());
		
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
		zoomSlider.setBackground(Settings.CONTROL_BACKGROUND);
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
		
		add(westPanel, BorderLayout.WEST);
		add(northPanel, BorderLayout.NORTH);
		add(drawPanel, BorderLayout.CENTER);
		
		setVisible(true);
	}

	protected void setDirectory(File f) {
		if (f != null) {
			drawPanel.setImageLoading(true);
			Thread fileLoadingThread = new Thread("fileLoadingThread") {
				public void run() {
					try {
						Dimension oldImageSize = new Dimension(0, 0);
						if (Settings.PAINT_IMAGE != null) {
							oldImageSize = new Dimension(Settings.PAINT_IMAGE.getWidth(), Settings.PAINT_IMAGE.getHeight());
						}
						Settings.PAINT_IMAGE = null;
						Settings.PAINT_IMAGE = ImageIO.read(f);
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
						JOptionPane.showMessageDialog(drawPanel, "Cannot load Image, file is too large");
					}
					Main.DISPLAY_PAINT.repaint();
					drawPanel.repaint();
					drawPanel.setImageLoading(false);
				}
			};
			fileLoadingThread.start();
		}
	}
	
	protected void setFile(String selectedItem) {
		setDirectory(new File(
				Settings.FOLDERS[Mode.PAINT.ordinal()].getAbsolutePath() + "/" + selectedItem
		));
	}
	
	private void setZoomMax() {
		double w = Settings.PAINT_IMAGE.getWidth() / Settings.DISPLAY_SIZE.getWidth();
		double h = Settings.PAINT_IMAGE.getHeight() / Settings.DISPLAY_SIZE.getHeight();
		double maxZoom = h > w ? h : w;
		zoomSlider.setMaximum((int) (maxZoom * 100));
	}
}