package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class Settings {
	
	public static final String NAME = "Dungeon Board";
	
	public static final File USER_DIR = new File(System.getProperty("user.dir"));
	public static final File FOLDER = new File(USER_DIR.getAbsolutePath() + "\\DungeonBoard");
	public static final File[] FOLDERS = {
			new File(FOLDER + "\\Layer"),
			new File(FOLDER + "\\Image"),
			new File(FOLDER + "\\Paint"),
			new File(FOLDER + "\\Loading")
	};
	
	public static final ImageIcon ICON_FOLDER = load("open.gif");
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
	
	public static final BufferedImage BLANK_CURSOR = new BufferedImage(3, 3, BufferedImage.TYPE_INT_ARGB);
	public static final BufferedImage HANDS[] = new BufferedImage[4];
	public static final int[] HANDS_OFFSET = {-5, -100, -45, 0};
	
	public static final int PIXELS_PER_MASK = 2;
	
	public static final Point NULL_POS = new Point(-100, -100);
	
	private static ImageIcon load(String res) {
		return new ImageIcon(Settings.class.getResource("/resources/" + res));
	}
	
	public static void load() {
		for (int i = 0; i < HANDS.length; i++) {
        	try {
				HANDS[i] = ImageIO.read(Settings.class.getResource("/resources/hand" + i + ".png"));
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
	}
}