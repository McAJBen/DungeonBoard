package main;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;

public class FileChooser extends JButton {
	private static final long serialVersionUID = 1L;
	
	public FileChooser() {
		setIcon(createImageIcon("/resources/open.gif"));
	}
	
	protected static ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = FileChooser.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }
	
	public BufferedImage getImage() {
		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(new java.io.File("."));
		chooser.setDialogTitle("Select Picture");
		if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			File f = chooser.getSelectedFile();
			String name = f.getName();
			String suffix = name.substring(name.lastIndexOf('.') + 1);
			if (suffix.equalsIgnoreCase("PNG") || suffix.equalsIgnoreCase("JPG")) {
				try {
					return ImageIO.read(f);
				} catch (Exception error) {
					
				}
			}
		}
		return null;
	}

	public File getFolder() {
		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
		chooser.setDialogTitle("Select Folder containing pictures");
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setAcceptAllFileFilterUsed(false);
		
		if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			return chooser.getSelectedFile();
		}
		return null;
	}
}