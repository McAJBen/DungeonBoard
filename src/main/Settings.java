package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JButton;

public class Settings {
	
	public static final String NAME = "Dungeon Board";
	
	public static final File USER_DIR = new File(System.getProperty("usehgr.dir"));
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
	
	public static final Color BACKGROUND = new Color(153, 153, 153);
	public static final Color CONTROL_BACKGROUND = new Color(200, 200, 200);
	
	public static final boolean IS_WINDOWS = System.getProperty("os.name").startsWith("Windows");
	
	public static BufferedImage PAINT_IMAGE;

	public static Dimension DISPLAY_SIZE;
	
	public static int PIXELS_PER_MASK = 3;
	
	public static void load() throws SecurityException {
		for (File f: Settings.FOLDERS) {
			if (!f.exists()) {
				f.mkdirs();
			}
		}
	}
	
	public static JButton createButton(String label) {
		JButton button = new JButton(label);
		button.setFocusPainted(false);
		button.setRolloverEnabled(false);
		return button;
	}
	
	public static JButton createButton(ImageIcon imageIcon) {
		JButton button = new JButton(imageIcon);
		button.setFocusPainted(false);
		button.setRolloverEnabled(false);
		return button;
	}
	
	public static ImageIcon load(String res) {
		return new ImageIcon(Settings.class.getResource("/resources/" + res));
	}
}