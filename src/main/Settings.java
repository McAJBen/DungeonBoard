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
	public static final File FOLDER = new File(new File(System.getProperty("user.dir")).getAbsolutePath() + File.separator + NAME);
	
	/**
	 * The folder currently in use on the paint layer
	 */
	public static File PAINT_FOLDER;
	
	/**
	 * an array of all the sub folders in the Dungeon Board folder by their {@code Mode}
	 */
	public static final File[] FOLDERS = {
			new File(FOLDER + File.separator + "Layer"),
			new File(FOLDER + File.separator + "Image"),
			new File(FOLDER + File.separator + "Paint"),
			new File(FOLDER + File.separator + "Loading")
	};
	
	/**
	 * a sub folder in Dungeon Board to store data separate from sessions
	 */
	public static final File DATA_FOLDER = new File(FOLDER + File.separator + "Data");
	
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
			load("square.gif"),
			load("rect.gif")
	};
	
	/**
	 * a blank 3x3 {@code BufferedImage} for displaying an invisible cursor, or as a placeholder for an image;
	 */
	public static final BufferedImage BLANK_CURSOR = new BufferedImage(3, 3, BufferedImage.TYPE_INT_ARGB);

	/**
	 * the image used by {@code DisplayPaint}
	 */
	public static BufferedImage PAINT_IMAGE;
	
	/**
	 * the image used by {@code ControlPaint}
	 */
	public static BufferedImage PAINT_CONTROL_IMAGE;
	
	/**
	 * the default size of the {@code ControlWindow}
	 */
	public static final Dimension CONTROL_SIZE = new Dimension(900, 700);
	
	/**
	 * the size of the display that players see
	 */
	public static Dimension DISPLAY_SIZE;
	
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
	 * the pink color used on {@code DrawPanel} to show the cursor and outline of player's view
	 */
	public static final Color PINK = new Color(255, 0, 255);
	
	/**
	 * the semi-transparent pink color used on {@code DrawPanel}
	 */
	public static final Color PINK_CLEAR = new Color(255, 0, 255, 25);
	
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
	 * The active layers in {@code DisplayPaint}
	 */
	public static boolean[] PAINT_IMAGES;
	
	/**
	 * the number of threads this computer has
	 */
	public static final int SYS_THREADS = Math.max(1, Runtime.getRuntime().availableProcessors() - 1);
	
	/**
	 * how much to scale down the image for {@code ControlPaint}
	 */
	public static final int PAINT_GUIDE_SCALE = 3;
	
	/**
	 * number of pixels on the Paint image that are being covered by a single pixel on the mask.<br>
	 * - higher number means the shadows will be more blocky<br>
	 * - lower number means the shadows will be more fine, but will use more memory and CPU time
	 */
	public static final int PIXELS_PER_MASK = 5;
	
	/**
	 * number of files in the current PAINT_FOLDER
	 */
	public static int PAINT_FOLDER_SIZE;
	
	/**
	 * initializes the {@code FOLDERS} and creates them if they are not already there
	 * @throws SecurityException when the program is unable to create the folders
	 */
	public static void load() throws SecurityException {
		for (File f: FOLDERS) {
			if (!f.exists()) {
				f.mkdirs();
			}
		}
		
		if (!DATA_FOLDER.exists()) {
			DATA_FOLDER.mkdirs();
		}
		
		File ImageThumbs = new File(DATA_FOLDER + File.separator + "Layer");
		ImageThumbs.mkdirs();
		
		File LayerThumbs = new File(DATA_FOLDER + File.separator + "Image");
		LayerThumbs.mkdirs();
		
		File paintMasks = new File(DATA_FOLDER + File.separator + "Paint");
		paintMasks.mkdirs();
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
	 * turns a file into its thumbnail equal
	 * @param f the file referring to an image
	 * @return a file in the thumbnail folder
	 */
	public static File fileToThumb(File f) {
		return new File(DATA_FOLDER.getAbsolutePath() + File.separator + f.getParentFile().getName() + File.separator + f.getName());
	}
	
	/**
	 * turns a thumbnail file into its normal file equal
	 * @param f the file referring to the thumbnail
	 * @return a file in the normal folders
	 */
	public static File thumbToFile(File f) {
		return new File(FOLDER.getAbsolutePath() + File.separator + f.getParentFile().getName() + File.separator + f.getName());
	}
	
	/**
	 * turns a folder into its data folder equal
	 * @param f the folder referring to one in Settings.FOLDERS
	 * @return a folder in the data folder
	 */
	public static File folderToDataFolder(File f) {
		return new File(DATA_FOLDER.getAbsolutePath() + File.separator + f.getName());
	}
	
	/**
	 * turns a file/folder into its data folder equal to store a mask for Paint
	 * @param f the file referring to one in Settings.PAINT_FOLDER
	 * @return a file in the data folder
	 */
	public static File fileToMaskFile(File f) {
		if (f != null) {
			if (f.isDirectory()) {
				return new File(DATA_FOLDER.getAbsolutePath() + File.separator + "Paint" + File.separator + f.getName() + ".f");
			}
			else {
				return new File(DATA_FOLDER.getAbsolutePath() + File.separator + "Paint" + File.separator + f.getName());
			}
		}
		return null;
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