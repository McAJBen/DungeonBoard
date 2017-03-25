package paint;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import control.ControlPanel;
import main.FileChooser;

public class ControlPaint extends ControlPanel {
	
	private static final long serialVersionUID = 1L;
	
	private ImageIcon[] drawStyle;
	private ImageIcon[] drawMode;
	private ImageIcon[] penType;
	
	private DrawPanel drawPanel;
	private DisplayPaintPanel paintDisplay;
	
	private JComboBox<String> fileBox;
	private JButton updateScreen;
	private JTextField zoomText;
	private JSlider zoomSlider;
	
	public ControlPaint(Dimension displaySize, DisplayPaintPanel disp) {
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createLineBorder(Color.GRAY, 10));
		drawStyle = new ImageIcon[3];
		drawStyle[0] = createImageIcon("/resources/squigle.gif");
		drawStyle[1] = createImageIcon("/resources/vertical.gif");
		drawStyle[2] = createImageIcon("/resources/horizontal.gif");
		drawMode = new ImageIcon[4];
		drawMode[0] = createImageIcon("/resources/mouse.gif");
		drawMode[1] = createImageIcon("/resources/visible.gif");
		drawMode[2] = createImageIcon("/resources/invisible.gif");
		drawMode[3] = createImageIcon("/resources/move.gif");
		penType = new ImageIcon[2];
		penType[0] = createImageIcon("/resources/circle.gif");
		penType[1] = createImageIcon("/resources/square.gif");
		
		paintDisplay = disp;
		
		drawPanel = new DrawPanel(displaySize, disp);
		
		JPanel northPanel = new JPanel();
		northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.X_AXIS));
		
		setFocusable(true);
		
		FileChooser fc = new FileChooser();
		fc.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setFile(fc.getFile());
			}
		});
		northPanel.add(fc);
		
		fileBox = new JComboBox<>();
		fileBox.addItem("");
		File folder = new File(System.getProperty("user.dir") + "\\DungeonBoard\\Paint");
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
		
		JButton drawStyleButton = new JButton(drawStyle[0]);
		drawStyleButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				drawPanel.toggleStyle();
				drawStyleButton.setIcon(drawStyle[drawPanel.getStyle()]);
			}
		});
		northPanel.add(drawStyleButton);
		
		JButton shape = new JButton(penType[0]);
		shape.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				drawPanel.togglePen();
				shape.setIcon(penType[drawPanel.getPen()]);
			}
		});
		northPanel.add(shape);
		
		JButton drawModeButton = new JButton(drawMode[0]);
		drawModeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				drawPanel.toggleDrawMode();
				drawModeButton.setIcon(drawMode[drawPanel.getDrawMode()]);
			}
		});
		northPanel.add(drawModeButton);
		
		JSlider slider = new JSlider(SwingConstants.HORIZONTAL, 10, 100, 25);
		slider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				drawPanel.setRadius(slider.getValue());
			}
		});
		northPanel.add(slider);
		
		updateScreen = new JButton("Update Screen");
		updateScreen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				paintDisplay.setMask(drawPanel.getMask());
				paintDisplay.changeWindowPos(drawPanel.getWindowPos());
				updateScreen.setEnabled(false);
			}
		});
		northPanel.add(updateScreen);
		drawPanel.setUpdateButton(updateScreen);
		
		JPanel westPanel = new JPanel();
		westPanel.setLayout(new BoxLayout(westPanel, BoxLayout.Y_AXIS));
		
		westPanel.add(new JLabel("Zoom", SwingConstants.LEFT));
		
		zoomText = new JTextField("1.00", 1);
		zoomText.setMaximumSize(new Dimension(5000, 25));
		zoomText.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				double result = 1;
				try {
					result = Double.parseDouble(zoomText.getText());
				}
				catch (NumberFormatException nfe) {
					result = zoomSlider.getValue() / 100.0;
				}
				setZoom(result);
			}
		});
		westPanel.add(zoomText);
		
		zoomSlider = new JSlider(SwingConstants.VERTICAL, 1, 1000, 100);
		zoomSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				setZoom(zoomSlider.getValue() / 100.0);
			}
		});
		westPanel.add(zoomSlider);
		
		add(westPanel, BorderLayout.WEST);
		add(northPanel, BorderLayout.NORTH);
		add(drawPanel, BorderLayout.CENTER);
		
		setVisible(true);
	}
	
	private void setZoom(double zoom) {
		if (zoom < 0.01) {
			zoom = 0.01;
		}
		else if (zoom > 10.0) {
			zoom = 10.0;
		}
		zoomText.setText(String.format("%.2f", zoom));
		zoomSlider.setValue((int)(zoom * 100));
		drawPanel.setZoom(zoom);
	}
	
	protected void setFile(String selectedItem) {
		File f = new File(System.getProperty("user.dir") + "\\DungeonBoard\\Paint\\" + selectedItem);
		setFile(f);
	}

	private static ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = FileChooser.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
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
						}
					} catch (Exception error) {
						error.printStackTrace();
					}
				}
			};
			fileLoadingThread.start();
		}
	}
}