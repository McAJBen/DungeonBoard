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

public class DisplayLoadingPanel extends DisplayPanel {
	private static final long serialVersionUID = 1L;
	
	private static final int FADE_IN = 20;
	private static final int TOTAL_WAIT = 400;
	
	private File folder;
	private LinkedList<String> fileNames;
	private BufferedImage oldImage;
	private BufferedImage currentImage;
	
	private Thread paintThread;
	private boolean mainDisplay;
	private short timer;
	private float fade;
	
	public DisplayLoadingPanel() {
		paintThread = new Thread();
		fileNames = new LinkedList<>();
		fade = 0;
		setVisible(true);
	}

	private void motion() {
		timer++;
		if (timer <= FADE_IN) {
			repaint();
			fade = (float)timer / FADE_IN;
		}
		else if (timer > TOTAL_WAIT) {
			repaint();
			timer = 0;
			getImage();
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

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2d = (Graphics2D) g;
		Dimension s = getSize();
		g2d.setColor(Color.BLACK);
		g2d.fillRect(0, 0, s.width, s.height);
		if (timer <= FADE_IN) {
			g2d.drawImage(oldImage, 0, 0, s.width, s.height, null);
		}
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, fade));
		g2d.drawImage(currentImage, 0, 0, s.width, s.height, null);
		g.dispose();
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
	
	@Override
	public void setMainDisplay(boolean b) {
		if (b) {
			mainDisplay = true;
			restart(false);
		}
		else {
			mainDisplay = false;
		}
	}
	
	public void setDirectory(File folder) {
		this.folder = folder;
		fileNames.clear();
		timer = 0;
		rePop();
		restart(true);
	}
}
