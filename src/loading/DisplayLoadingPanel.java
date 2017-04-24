package loading;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.LinkedList;
import java.util.Random;
import javax.imageio.ImageIO;
import display.DisplayPanel;
import display.DisplayWindow;

public class DisplayLoadingPanel extends DisplayPanel {
	
	private static final long serialVersionUID = -4364176757863161776L;
	
	// 50ms tick time makes 20 ticks per second
	private static final int FADE_IN = 20;
	private int totalWait = 400;
	
	private LinkedList<Cube> cubePositions;
	
	private File folder;
	private LinkedList<String> fileNames;
	private BufferedImage oldImage;
	private BufferedImage currentImage;
	
	private Thread paintThread;
	private boolean mainDisplay;
	private boolean upScale;
	private short timer;
	private float fade;
	
	public DisplayLoadingPanel(DisplayWindow window) {
		super(window);
		cubePositions = new LinkedList<>();
		paintThread = new Thread();
		fileNames = new LinkedList<>();
		upScale = false;
		timer = 20;
		fade = 1;
		setVisible(true);
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2d = (Graphics2D) g;
		Dimension s = getSize();
		if (currentImage != null) {
			if (upScale) {
				if (timer <= FADE_IN) {
					g2d.drawImage(oldImage, 0, 0, s.width, s.height, null);
				}
				g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, fade));
				g2d.drawImage(currentImage, 0, 0, s.width, s.height, null);
			}
			else {
				g2d.setColor(new Color(currentImage.getRGB(0, 0)));
				g2d.fillRect(0, 0, s.width, s.height);
				
				if (timer <= FADE_IN && oldImage != null) {
					g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1 - fade));
					g2d.drawImage(oldImage, (s.width - oldImage.getWidth()) / 2,
							(s.height - oldImage.getHeight()) / 2, null);
				}
				g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, fade));
				g2d.drawImage(currentImage, (s.width - currentImage.getWidth()) / 2,
						(s.height - currentImage.getHeight()) / 2, null);
			}
		}
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
		for (Cube c: cubePositions) {
			c.paint(g2d, s);
		}
		window.paintMouse(g);
		g.dispose();
	}
	
	@Override
	public void setMainDisplay(boolean b) {
		if (b) {
			restart(false);
		}
		mainDisplay = b;
	}

	public void setDirectory(File folder) {
		this.folder = folder;
		fileNames.clear();
		timer = 20;
		fade = 1;
		rePop();
		restart(true);
	}
	
	public void setTotalWait(int seconds) {
		totalWait = seconds * 20;
	}

	public void setUpScale(boolean b) {
		upScale = b;
		repaint();
	}
	
	public void addCube() {
		cubePositions.add(new Cube(getSize()));
		repaint();
	}

	public void clearCubes() {
		synchronized (cubePositions) {
			cubePositions.clear();
		}
	}

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
			c.move(getSize());
		}
	}
	
	private void getImage() {
		if (fileNames.isEmpty()) {
			rePop();
		}
		if (!fileNames.isEmpty()) {
			oldImage = currentImage;
			String file = folder.getAbsolutePath() + "\\" + fileNames.removeFirst();
			try {
				currentImage = ImageIO.read(new File(file));
			} catch (Exception e) {
				currentImage = null;
			}
		}
	}

	private void rePop() {
		if (folder != null && folder.exists()) {
			Random rand = new Random();
			for (File f: folder.listFiles()) {
				String name = f.getName();
				String suffix = name.substring(name.lastIndexOf('.') + 1);
				if (suffix.equalsIgnoreCase("PNG")) {
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