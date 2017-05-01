package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import paint.DrawPanel;

public class Settings {
	
	public static final String NAME = "Dungeon Board";
	
	public static final File USER_DIR = new File(System.getProperty("user.dir"));
	public static final File FOLDER = new File(USER_DIR.getAbsolutePath() + "/DungeonBoard");
	public static final File[] FOLDERS = {
			new File(FOLDER + "/Layer"),
			new File(FOLDER + "/Image"),
			new File(FOLDER + "/Paint"),
			new File(FOLDER + "/Loading")
	};
	
	public static final ImageIcon ICON = load("icon.gif");
	public static final ImageIcon ICON_REFRESH = load("refresh.gif");
	public static final ImageIcon ICON_FLIP = load("flip.gif");
	public static final ImageIcon ICON_SETTINGS = load("settings.gif");
	public static final ImageIcon ICON_DVD = load("dvdlogo.gif");
	public static final ImageIcon ICON_DVD2 = load("dvdlogo2.gif");
	public static final ImageIcon DRAW_STYLE[] = {
			load("squigle.gif"),
			load("vertical.gif"),
			load("horizontal.gif")
	};
	public static final ImageIcon DRAW_MODE[] = {
			load("mouse.gif"),
			load("visible.gif"),
			load("invisible.gif"),
			load("move.gif")
	};
	public static final ImageIcon PEN_TYPE[] = {
			load("circle.gif"),
			load("square.gif")
	};
	
	public static final BufferedImage BLANK_CURSOR = new BufferedImage(3, 3, BufferedImage.TYPE_INT_ARGB);
	
	public static final Dimension CONTROL_SIZE = new Dimension(800, 700);
	
	public static final Color ACTIVE = new Color(153, 255, 187);
	public static final Color INACTIVE = new Color(255, 128, 128);
	
	public static final Color ENABLE_COLOR = Color.GREEN;
	public static final Color DISABLE_COLOR = Color.GRAY;
	
	public static final Color CLEAR = new Color(100, 255, 100);
	public static final Color OPAQUE = new Color(255, 100, 100);
	public static final Color PINK = new Color(255, 0, 255);
	
	public static final Color BACKGROUND = Color.GRAY;
	public static final Color CONTROL_BACKGROUND = Color.LIGHT_GRAY;
	
	public static final boolean IS_WINDOWS = System.getProperty("os.name").startsWith("Windows");
	
	public static BufferedImage PAINT_IMAGE;

	public static Dimension DISPLAY_SIZE;
	
	public static int PIXELS_PER_MASK = 3;
	
	public static void load() {
		try {
			for (File f: Settings.FOLDERS) {
				if (!f.exists()) {
					f.mkdirs();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static JButton createButton(String label) {
		JButton button = new JButton(label);
		button.setFocusPainted(false);
		button.setRolloverEnabled(false);
		button.setOpaque(true);
		button.setBackground(CONTROL_BACKGROUND);
		return button;
	}
	
	public static JButton createButton(ImageIcon imageIcon) {
		JButton button = new JButton(imageIcon);
		button.setFocusPainted(false);
		button.setRolloverEnabled(false);
		button.setOpaque(true);
		button.setBackground(CONTROL_BACKGROUND);
		return button;
	}
	
	public static ImageIcon load(String res) {
		return new ImageIcon(Settings.class.getResource("/resources/" + res));
	}

	public static void showSettings(DrawPanel drawPanel) {
		JDialog settings = new JDialog(Main.CONTROL_WINDOW, "Settings", true);
		settings.setLocationRelativeTo(Main.CONTROL_WINDOW);
		settings.setSize(new Dimension(400, 400));
		
		settings.setLayout(new BoxLayout(settings.getContentPane(), BoxLayout.Y_AXIS));
		
		JPanel paintMaskPanel = new JPanel();
		paintMaskPanel.setLayout(new BoxLayout(paintMaskPanel, BoxLayout.X_AXIS));
		paintMaskPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		
		paintMaskPanel.add(new JLabel("Paint Mask Quality: "));
		
		JLabel maskQualityLabel = new JLabel(PIXELS_PER_MASK + "");
		paintMaskPanel.add(maskQualityLabel);
		
		JSlider maskQualitySlider = new JSlider(JSlider.HORIZONTAL, 1, 20, PIXELS_PER_MASK);
		maskQualitySlider.setMajorTickSpacing(5);
		maskQualitySlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				maskQualityLabel.setText(maskQualitySlider.getValue() + "");
			}
		});
		
		paintMaskPanel.add(maskQualitySlider);
		settings.add(paintMaskPanel);
		
		JButton saveButton = createButton("Save");
		saveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				PIXELS_PER_MASK = maskQualitySlider.getValue();
				drawPanel.setImage();
				settings.dispose();
			}
		});
		settings.add(saveButton);
		
		settings.setVisible(true);
	}
	
	public static BufferedImage addAlpha(BufferedImage b) {
		BufferedImage b2 = new BufferedImage(b.getWidth(), b.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
		int alphaColor = b.getRGB(0, 0);
		for (int i = 0; i < b.getWidth(); i++) {
			for (int j = 0; j < b.getHeight(); j++) {
				int rgb = b.getRGB(i, j);
				if (rgb != alphaColor) {
					b2.setRGB(i, j, rgb);
				}
				else {
					b2.setRGB(i, j, 0);
				}
			}
		}
		return b2;
	}
}