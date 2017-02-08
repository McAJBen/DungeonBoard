package loading;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Random;

import javax.imageio.ImageIO;

import display.DisplayPanel;
import main.FileChooser;

public class DisplayLoadingPanel extends DisplayPanel {
	private static final long serialVersionUID = 1L;
	
	private File folder;
	private LinkedList<String> fileNames;
	private BufferedImage image;
	
	private Thread paintThread;
	private boolean mainDisplay;
	private short timer;
	private int fade;
	
	public DisplayLoadingPanel() {
		paintThread = new Thread();
		fileNames = new LinkedList<>();
		fade = 0;
		setVisible(true);
	}

	private void motion() {
		timer++;
		if (timer <= 17) {
			repaint();
			fade = 255 - timer * 15;
		}
		else if (timer > 400) {
			repaint();
			timer = 0;
			getImage();
		}	
		else if (timer > 383) {
			repaint();
			fade = (timer - 383) * 15;
		}
	}
	
	private void getImage() {
		if (!fileNames.isEmpty()) {
			Random rand = new Random();
			if (rand.nextInt(100) == 0) {
				image = getFakeImage();
			}
			else {
				String file = folder.getAbsolutePath() + "\\" + fileNames.removeFirst();
				try {
					image = ImageIO.read(new File(file));
				} catch (Exception e) {
					image = null;
				}
			}
		}
	}

	private BufferedImage getFakeImage() {
		try {
        	java.net.URL imgURL = FileChooser.class.getResource("/resources/fakeLoadingTip.png");
        	if (imgURL != null) {
        		return ImageIO.read(imgURL);
        	}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Dimension s = getSize();
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, s.width, s.height);
		g.drawImage(image, 0, 0, s.width, s.height, null);
		g.setColor(new Color(0, 0, 0, fade));
		g.fillRect(0, 0, s.width, s.height);
		g.dispose();
	}
	
	public void rePop() {
		if (folder != null && fileNames.isEmpty() && folder.exists()) {
			Random rand = new Random();
			for (File f: folder.listFiles()) {
				String name = f.getName();
				String suffix = name.substring(name.lastIndexOf('.') + 1);
				if (suffix.equalsIgnoreCase("PNG")) {
					if (fileNames.isEmpty()) {
						fileNames.add(name);
					}
					else {
						fileNames.add(rand.nextInt(fileNames.size()), name);
					}
				}
			}
		}
	}
	
	private void restart() {
		paintThread.interrupt();
		try {
			paintThread.join();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		getImage();
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
		timer = 0;
		paintThread.start();
	}
	
	@Override
	public void setMainDisplay(boolean b) {
		if (b) {
			mainDisplay = true;
			restart();
		}
		else {
			mainDisplay = false;
		}
	}
	
	public void setDirectory(File folder) {
		this.folder = folder;
		fileNames.clear();
		rePop();
		restart();
	}
}
