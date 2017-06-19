package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;

/**
 * contains static final variables that are used through Dungeon Board
 * @author McAJBen <McAJBen@gmail.com>
 * @since 1.6
 */
public class Settings {
	
	/**
	 * full name of the program
	 */
	public static final String NAME = "Dungeon Board";
	
	/**
	 * The Dungeon Board directory that contains all images
	 */
	public static final File FOLDER = new File(new File(System.getProperty("user.dir")).getAbsolutePath() + "/" + NAME);
	
	/**
	 * The folder currently in use on the paint layer
	 */
	public static File PAINT_FOLDER;
	
	/**
	 * an array of all the sub folders in the Dungeon Board folder by their {@code Mode}
	 */
	public static final File[] FOLDERS = {
			new File(FOLDER + "/Layer"),
			new File(FOLDER + "/Image"),
			new File(FOLDER + "/Paint"),
			new File(FOLDER + "/Loading")
	};
	
	/**
	 * the main {@code ImageIcon} for Dungeon Board
	 */
	public static final ImageIcon ICON = load("icon.gif");
	
	/**
	 * the {@code ImageIcon} for a refresh {@code JButton}
	 */
	public static final ImageIcon ICON_REFRESH = load("refresh.gif");
	
	/**
	 * the {@code ImageIcon} for a flip {@code JButton}
	 */
	public static final ImageIcon ICON_FLIP = load("flip.gif");
	
	/**
	 * the {@code ImageIcon} for a settings {@code JButton}
	 */
	public static final ImageIcon ICON_SETTINGS = load("settings.gif");
	
	/**
	 * the {@code ImageIcon} that floats around the {@code DisplayLoading}
	 */
	public static final ImageIcon ICON_DVD = load("dvdlogo.gif");
	
	/**
	 * the {@code ImageIcon} that sticks to the corner of {@code DisplayLoading}
	 */
	public static final ImageIcon ICON_DVD2 = load("dvdlogo2.gif");
	
	/**
	 * an array of {@code ImageIcons} visualizing the drawing {@code Direction} state on a {@code JButton}
	 */
	public static final ImageIcon DRAW_STYLE[] = {
			load("squigle.gif"),
			load("vertical.gif"),
			load("horizontal.gif")
	};
	
	/**
	 * an array of {@code ImageIcons} visualizing the {@code DrawMode} state on a {@code JButton}
	 */
	public static final ImageIcon DRAW_MODE[] = {
			load("mouse.gif"),
			load("visible.gif"),
			load("invisible.gif"),
			load("move.gif")
	};
	
	/**
	 * an array of {@code ImageIcons} visualizing the {@code Pen} state on a {@code JButton}
	 */
	public static final ImageIcon PEN_TYPE[] = {
			load("circle.gif"),
			load("square.gif")
	};
	
	/**
	 * a blank 3x3 {@code BufferedImage} for displaying an invisible cursor, or as a placeholder for an image;
	 */
	public static final BufferedImage BLANK_CURSOR = new BufferedImage(3, 3, BufferedImage.TYPE_INT_ARGB);
	
	/**
	 * the default size of the {@code ControlWindow}
	 */
	public static final Dimension CONTROL_SIZE = new Dimension(900, 700);
	
	/**
	 * the color of an active button - (153, 255, 187)
	 */
	public static final Color ACTIVE = new Color(153, 255, 187);
	
	/**
	 * the color of an inactive button - (255, 128, 128)
	 */
	public static final Color INACTIVE = new Color(255, 128, 128);
	
	/**
	 * the color of an enabled button - Color.GREEN
	 */
	public static final Color ENABLE_COLOR = Color.GREEN;
	
	/**
	 * the color of an disabled button - Color.GRAY
	 */
	public static final Color DISABLE_COLOR = Color.GRAY;
	
	/**
	 * the green color used on {@code DrawPanel} to show a visible part of the map
	 */
	public static final Color CLEAR = new Color(100, 255, 100);
	
	/**
	 * the red color used on {@code DrawPanel} to show a blocked part of the map
	 */
	public static final Color OPAQUE = new Color(255, 100, 100);
	
	/**
	 * the pink color used on {@DrawPanel} to show the cursor and outline of player's view
	 */
	public static final Color PINK = new Color(255, 0, 255);
	
	/**
	 * the background color used through Dungeon Board - (153, 153, 153)
	 */
	public static final Color BACKGROUND = new Color(153, 153, 153);
	
	/**
	 * the background for user controls like {@code JButtons} and {@code JComboBoxs} - (200, 200, 200)
	 */
	public static final Color CONTROL_BACKGROUND = new Color(200, 200, 200);
	
	/**
	 * true if the environment's Operating System is Window, false if not (currently unused)
	 */
	public static final boolean IS_WINDOWS = System.getProperty("os.name").startsWith("Windows");
	
	/**
	 * the image used by {@code DisplayPaint}
	 */
	public static BufferedImage PAINT_IMAGE;
	
	/**
	 * the image used by {@code ControlPaint}
	 */
	public static BufferedImage PAINT_CONTROL_IMAGE;
	
	/**
	 * The active layers in {@code DisplayPaint}
	 */
	public static boolean[] PAINT_IMAGES;

	/**
	 * the size of the display that players see
	 */
	public static Dimension DISPLAY_SIZE;
	
	/**
	 * number of pixels on the Paint image that are being covered by a single pixel on the mask.<br>
	 * - higher number means the shadows will be more blocky<br>
	 * - lower number means the shadows will be more fine, but will use more memory and CPU time
	 */
	public static int PIXELS_PER_MASK = 3;
	
	/**
	 * number of files in the current PAINT_FOLDER
	 */
	public static int PAINT_FOLDER_SIZE;
	
	/**
	 * initializes the {@code FOLDERS} and creates them if they are not already there
	 * @throws SecurityException when the program is unable to create the folders
	 */
	public static void load() throws SecurityException {
		for (File f: Settings.FOLDERS) {
			if (!f.exists()) {
				f.mkdirs();
			}
		}
	}
	
	/**
	 * method used to load resources by file name
	 * @param res name of the file to be loaded
	 * @return an {@code ImageIcon} from the resources folder
	 */
	public static ImageIcon load(String res) {
		return new ImageIcon(Settings.class.getResource("/resources/" + res));
	}
	
	/**
	 * creates and returns a {@code JButton} with the proper look
	 * @param label text to be shown on the {@code JButton}
	 * @return a {@code JButton} that looks like the standard for Dungeon Board
	 */
	public static JButton createButton(String label) {
		JButton button = new JButton(label);
		button.setFocusPainted(false);
		button.setRolloverEnabled(false);
		return button;
	}
	
	/**
	 * creates and returns a {@code JButton} with the proper look
	 * @param imageIcon to be shown on the {@code JButton}
	 * @return a {@code JButton} that looks like the standard for Dungeon Board
	 */
	public static JButton createButton(ImageIcon imageIcon) {
		JButton button = new JButton(imageIcon);
		button.setFocusPainted(false);
		button.setRolloverEnabled(false);
		return button;
	}
	
	/**
	 * shows a {@code JOptionPane} to display an error
	 * @param message custom text to be displayed
	 */
	public static void showError(String message) {
		JOptionPane.showMessageDialog(Main.getControl(), message, "Error", JOptionPane.ERROR_MESSAGE);
	}
	
	/**
	 * shows a {@code JOptionPane} to display an error
	 * @param message custom text to be displayed
	 * @param error the error that was thrown
	 */
	public static void showError(String message, Throwable error) {
		JOptionPane.showMessageDialog(Main.getControl(), message + "\n" + error.getMessage());
	}
}