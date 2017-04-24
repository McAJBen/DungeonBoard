package paint;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
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
import main.FileChooser;
import main.Mode;
import main.Settings;

public class ControlPaint extends ControlPanel {
	
	private static final long serialVersionUID = -3231530555502467648L;
	
	private DrawPanel drawPanel;
	private DisplayPaintPanel paintDisplay;
	
	private JComboBox<String> fileBox;
	private JButton updateScreen;
	private JTextField zoomText;
	private JSlider zoomSlider;
	
	private double maxZoom;
	
	public ControlPaint(DisplayPaintPanel disp) {
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createLineBorder(Settings.BACKGROUND, 5));
		
		paintDisplay = disp;
		maxZoom = 10.0;
		
		drawPanel = new DrawPanel(disp);
		
		JPanel northPanel = new JPanel();
		northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.X_AXIS));
		northPanel.setBackground(Settings.CONTROL_BACKGROUND);
		
		setFocusable(true);
		
		FileChooser fc = Settings.createFileChooser();
		fc.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setFile(fc.getFile());
			}
		});
		northPanel.add(fc);
		
		fileBox = new JComboBox<>();
		fileBox.addItem("");
		fileBox.setBackground(Settings.CONTROL_BACKGROUND);
		File folder = Settings.FOLDERS[Mode.PAINT.ordinal()];
		if (folder.exists()) {
			for (File f: folder.listFiles()) {
				String name = f.getName();
				String suffix = name.substring(name.lastIndexOf('.') + 1);
				if (suffix.equalsIgnoreCase("PNG") || suffix.equalsIgnoreCase("JPG")) {
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
		
		updateScreen = Settings.createButton("Update Screen");
		updateScreen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (drawPanel.hasImage()) {
					paintDisplay.setMask(drawPanel.getMask());
					paintDisplay.setWindowPos(drawPanel.getWindowPos());
					updateScreen.setEnabled(false);
					updateScreen.setBackground(Settings.CONTROL_BACKGROUND);
				}
			}
		});
		northPanel.add(updateScreen);
		drawPanel.setUpdateButton(updateScreen);
		
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
	
	public void setFile(File f) {
		if (f != null) {
			drawPanel.setImageLoading();
			Thread fileLoadingThread = new Thread("fileLoadingThread") {
				public void run() {
					try {
						BufferedImage image = ImageIO.read(f);
						if (image != null) {
							drawPanel.setImage(image);
							paintDisplay.setMask(drawPanel.getMask());
							paintDisplay.setImage(image);
							setZoomMax(image.getWidth(), image.getHeight());
						}
					} catch (IOException | OutOfMemoryError error) {
						drawPanel.resetImage();
						paintDisplay.resetImage();
						System.out.println("Cannot load image, too big");
						JOptionPane.showMessageDialog(drawPanel, "Cannot load Image, file is too large");
					}
					paintDisplay.repaint();
					drawPanel.repaint();
				}
			};
			fileLoadingThread.start();
		}
	}
	
	protected void setFile(String selectedItem) {
		setFile(new File(
				Settings.FOLDERS[Mode.PAINT.ordinal()].getAbsolutePath() + "\\" + selectedItem
		));
	}
	
	private void setZoomMax(double width, double height) {
		double w = width / Settings.DISPLAY_SIZE.getWidth();
		double h = height / Settings.DISPLAY_SIZE.getHeight();
		double maxZoom = h > w ? h : w;
		zoomSlider.setMaximum((int) (maxZoom * 100));
	}
}