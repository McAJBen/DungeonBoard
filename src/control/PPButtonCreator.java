package control;

import java.io.File;
import java.util.LinkedList;

import javax.swing.JButton;

import main.Settings;

/**
 * a thread handler that loads all of the {@code PicturePanel} button thumbnails in rapid succession
 * @author McAJBen <McAJBen@gmail.com>
 * @since 2.3
 */
public class PPButtonCreator {
	
	/**
	 * the {@code PicturePanel} this {@code PPButtonCreator} is loading for
	 */
	private final PicturePanel pp;
	
	/**
	 * the queue of files to be loaded
	 */
	private final LinkedList<File> queue;
	
	/**
	 * the returned buttons that should be placed in the {@code ControlPicture}
	 */
	private final JButton[] buttons;
	
	/**
	 * the number of the next button
	 */
	private int queueNumber;
	
	/**
	 * creates an instance of the {@code PPButtonCreator}
	 * @param pp the {@code PicturePanel} the buttons need to be added to
	 * @param folder the folder the images are in
	 */
	public PPButtonCreator(PicturePanel pp, File folder) {
		this.pp = pp;
		queue = new LinkedList<>();
		queueNumber = 0;
		
		for (File f: Settings.listFilesInOrder(folder)) {
			String name = f.getName();
			String suffix = name.substring(name.lastIndexOf('.') + 1);
			if (suffix.equalsIgnoreCase("PNG") || suffix.equalsIgnoreCase("JPG") || suffix.equalsIgnoreCase("JPEG")) {
				queue.add(f);
			}
		}
		buttons = new JButton[queue.size()];
	}
	
	/**
	 * starts the loading process
	 */
	public synchronized void run() {
		if (!queue.isEmpty()) {
			ButtonMakerThread[] bmt = new ButtonMakerThread[Settings.SYS_THREADS];
			
			for (int i = 0; i < bmt.length; i++) {
				bmt[i] = new ButtonMakerThread(ButtonMakerThread.class.getName() + "-" + i);
				bmt[i].start();
			}
			for (int i = 0; i < bmt.length; i++) {
				try {
					bmt[i].join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			for (JButton b: buttons) {
				System.out.println(b.getText());
				pp.add(b);
			}
		}
	}
	
	/**
	 * A Thread that loads a thumbnail for a button, and adds it to the picture panel
	 * @author McAJBen <McAJBen@gmail.com>
	 */
	private class ButtonMakerThread extends Thread {
		
		/**
		 * creates an instance of the {@code ButtonMakerThread}
		 * @param string the name of the thread
		 */
		public ButtonMakerThread(String string) {
			super(string);
		}

		@Override
		public void run() {
			while (true) {
				File f;
				int w;
				synchronized (queue) {
					if (queue.isEmpty()) {
						break;
					}
					f = queue.removeFirst();
					w = queueNumber++;
				}
				buttons[w] = pp.createPPButton(f);
			}
		}
	}
}