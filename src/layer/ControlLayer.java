package layer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import main.ControlPanel;
import main.FileChooser;

public class ControlLayer extends ControlPanel {
	private static final long serialVersionUID = 1L;
	
	private DisplayLayerPanel layerDisplay;
	private PicturePanel pp;
	
	public ControlLayer(DisplayLayerPanel layerDisplay) {
		this.layerDisplay = layerDisplay;
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createLineBorder(Color.GRAY, 10));
		JPanel northPanel = new JPanel();
		northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.X_AXIS));
		
		FileChooser fc = new FileChooser();
		fc.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setDirectory(fc.getFolder());
			}
		});
		northPanel.add(fc);
		
		pp = new PicturePanel(layerDisplay);
		
		add(northPanel, BorderLayout.NORTH);
		
		JScrollPane jsp = new JScrollPane(pp);
		jsp.setBorder(BorderFactory.createEmptyBorder());
		add(jsp, BorderLayout.CENTER);
		
		setVisible(true);
	}
	
	public void setDirectory(File folder) {
		if (folder != null) {
			layerDisplay.setFolder(folder);
			pp.clearImages();
			for (File f: folder.listFiles()) {
				String name = f.getName();
				String suffix = name.substring(name.lastIndexOf('.') + 1);
				if (suffix.equalsIgnoreCase("PNG")) {
					pp.addImage(f);
				}
			}
			repaint();
			revalidate();
		}
	}

	

}
