package display;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.LinkedList;
import java.util.Random;

import javax.imageio.ImageIO;

import main.Mode;
import main.Settings;

/**
 * {@code JPanel} for displaying Loading Utility
 * @author McAJBen <McAJBen@gmail.com>
 * @since 1.0
 */
public class DisplayLoading extends Display {
	
	private static final long serialVersionUID = -4364176757863161776L;
	
	/**
	 * the number of ticks while the images are changing
	 * 50ms tick time makes 20 ticks per second
	 */
	private static final int FADE_IN = 20;
	
	/**
	 * the total number of ticks to display the image
	 */
	private int totalWait = 400;
	
	/**
	 * a list of the cubes in the {@code DisplayLoading}
	 */
	private LinkedList<Cube> cubePositions;
	
	/**
	 * a list of the file names that haven't been shown this loop
	 */
	private LinkedList<String> fileNames;
	
	/**
	 * the previous image that is fading out
	 */
	private BufferedImage oldImage;
	
	/**
	 * the current image being displayed
	 */
	private BufferedImage currentImage;
	
	/**
	 * the thread that is repainting the {@code DisplayLoading}
	 * and calculating motions and keeping track of time
	 */
	private Thread paintThread;
	
	/**
	 * tells if this is being shown and if we should be keeping track of time
	 */
	private boolean mainDisplay;
	
	/**
	 * tells if the images should be up scaled
	 */
	private boolean upScale;
	
	/**
	 * the count of how many ticks since the image has been changed
	 */
	private short timer;
	
	/**
	 * the alpha part of how faded the images are
	 */
	private float fade;
	
	/**
	 * creates a instance of {@code DisplayLoading}
	 */
	public DisplayLoading() {
		cubePositions = new LinkedList<>();
		paintThread = new Thread();
		fileNames = new LinkedList<>();
		upScale = false;
		timer = 20;
		fade = 1;
		getImage();
		setVisible(true);
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2d = (Graphics2D) g;
		if (currentImage != null) {
			if (upScale) {
				if (timer <= FADE_IN) {
					g2d.drawImage(oldImage, 0, 0, Settings.DISPLAY_SIZE.width, Settings.DISPLAY_SIZE.height, null);
				}
				g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, fade));
				g2d.drawImage(currentImage, 0, 0, Settings.DISPLAY_SIZE.width, Settings.DISPLAY_SIZE.height, null);
			}
			else {
				g2d.setColor(new Color(currentImage.getRGB(0, 0)));
				g2d.fillRect(0, 0, Settings.DISPLAY_SIZE.width, Settings.DISPLAY_SIZE.height);
				
				if (timer <= FADE_IN && oldImage != null) {
					g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1 - fade));
					g2d.drawImage(oldImage, (Settings.DISPLAY_SIZE.width - oldImage.getWidth()) / 2,
							(Settings.DISPLAY_SIZE.height - oldImage.getHeight()) / 2, null);
				}
				g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, fade));
				g2d.drawImage(currentImage, (Settings.DISPLAY_SIZE.width - currentImage.getWidth()) / 2,
						(Settings.DISPLAY_SIZE.height - currentImage.getHeight()) / 2, null);
			}
		}
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
		for (Cube c: cubePositions) {
			c.paint(g2d);
		}
		paintMouse(g2d);
		g2d.dispose();
	}
	
	@Override
	public void setMainDisplay(boolean b) {
		if (b) {
			restart(false);
		}
		mainDisplay = b;
	}
	
	/**
	 * changes the amount of seconds between loading images
	 * @param seconds the number of seconds between loading images
	 */
	public void setTotalWait(int seconds) {
		totalWait = seconds * 20;
	}

	/**
	 * changes if the images are up scaled or not
	 * @param b <br>
	 * - true if the image is scaled up<br>
	 * - false if the image is real size
	 */
	public void setUpScale(boolean b) {
		upScale = b;
		repaint();
	}
	
	/**
	 * creates a cube and adds it to the list of displaying cubes
	 */
	public void addCube() {
		cubePositions.add(new Cube());
		repaint();
	}

	/**
	 * removes all cubes
	 */
	public void clearCubes() {
		synchronized (cubePositions) {
			cubePositions.clear();
		}
	}

	/**
	 * progresses one tick forward
	 */
	private void motion() {
		timer++;
		repaint();
		if (timer <= FADE_IN) {
			fade = (float)timer / FADE_IN;
		}
		else if (timer > totalWait) {
			timer = 0;
			getImage();
		}
		for (Cube c: cubePositions) {
			c.move();
		}
	}
	
	/**
	 * changes images and loads a new one
	 */
	private void getImage() {
		if (fileNames.isEmpty()) {
			rePop();
		}
		if (!fileNames.isEmpty()) {
			oldImage = currentImage;
			String file = Settings.FOLDERS[Mode.LOADING.ordinal()] + "/" + fileNames.removeFirst();
			try {
				currentImage = ImageIO.read(new File(file));
			} catch (Exception e) {
				currentImage = null;
				e.printStackTrace();
			}
		}
	}

	/**
	 * re loads the list of images in the loading folder
	 */
	private void rePop() {
		File folder = Settings.FOLDERS[Mode.LOADING.ordinal()];
		if (folder.exists()) {
			Random rand = new Random();
			for (File f: folder.listFiles()) {
				String name = f.getName();
				String suffix = name.substring(name.lastIndexOf('.') + 1);
				if (suffix.equalsIgnoreCase("PNG") || suffix.equalsIgnoreCase("JPG") || suffix.equalsIgnoreCase("JPEG")) {
					int index = rand.nextInt(fileNames.size() + 1);
					if (index == fileNames.size()) {
						fileNames.add(name);
					}
					else {
						fileNames.add(index, name);
					}
				}
			}
		}
	}

	/**
	 * restarts the screen when it starts displaying again or is disabled
	 * @param changeImage whether the image should be changed first or not
	 */
	private void restart(boolean changeImage) {
		paintThread.interrupt();
		try {
			paintThread.join();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		if (changeImage) {
			getImage();
		}
		paintThread = new Thread("paintThread") {
			@Override
			public void run() {
				while (mainDisplay) {
					try {
						motion();
						sleep(50);
					} catch (InterruptedException e) {
						break;
					}
				}
			}
		};
		paintThread.start();
	}
}