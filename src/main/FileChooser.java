package main;

import java.io.File;
import javax.swing.JButton;
import javax.swing.JFileChooser;

public class FileChooser extends JButton {
	
	private static final long serialVersionUID = -6068205167217638272L;

	public FileChooser() {
		setIcon(Settings.ICON_FOLDER);
	}
	
	public File getFile() {
		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(Settings.USER_DIR);
		chooser.setDialogTitle("Select Picture");
		if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			File f = chooser.getSelectedFile();
			String name = f.getName();
			String suffix = name.substring(name.lastIndexOf('.') + 1);
			if (suffix.equalsIgnoreCase("PNG") || suffix.equalsIgnoreCase("JPG")) {
				return f;
			}
		}
		return null;
	}

	public File getFolder() {
		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(Settings.USER_DIR);
		chooser.setDialogTitle("Select Folder containing pictures");
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setAcceptAllFileFilterUsed(false);
		
		if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			return chooser.getSelectedFile();
		}
		return null;
	}
}