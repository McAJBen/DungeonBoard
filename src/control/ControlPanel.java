package control;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import main.FileChooser;
import main.Settings;

public abstract class ControlPanel extends JPanel {

	private static final long serialVersionUID = 7062093943678033069L;
	
	public ControlPanel() {
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createLineBorder(Settings.BACKGROUND, 5));
	}
	
	protected abstract void setDirectory(File file);
	
	protected JPanel getNorthPanel() {
		JPanel northPanel = new JPanel();
		northPanel.setBackground(Settings.CONTROL_BACKGROUND);
		northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.X_AXIS));
		
		FileChooser fc = Settings.createFileChooser();
		fc.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setDirectory(fc.getFolder());
			}
		});
		northPanel.add(fc);
		
		return northPanel;
	}
}