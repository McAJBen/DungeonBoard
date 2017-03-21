package layer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import control.ControlPanel;
import main.FileChooser;

public class ControlLayer extends ControlPanel {
	private static final long serialVersionUID = 1L;
	private static final Color ACTIVE = new Color(153, 255, 187);
	private static final Color NOT_ACTIVE = new Color(255, 128, 128);
	
	private DisplayLayerPanel layerDisplay;
	private PicturePanel pp;
	private JLabel folder;
	
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
		
		JButton scaleButton = new JButton("AutoScale");
		scaleButton.setBackground(ACTIVE);
		scaleButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				layerDisplay.toggleAutoScale();
				if (layerDisplay.getAutoScale()) {
					scaleButton.setBackground(ACTIVE);
				}
				else {
					scaleButton.setBackground(NOT_ACTIVE);
				}
				
			}
		});
		northPanel.add(scaleButton);
		
		folder = new JLabel();
		northPanel.add(folder);
		
		pp = new PicturePanel(layerDisplay);
		
		add(northPanel, BorderLayout.NORTH);
		
		JScrollPane jsp = new JScrollPane(pp);
		jsp.setBorder(BorderFactory.createEmptyBorder());
		add(jsp, BorderLayout.CENTER);
		
		setDirectory(new File(System.getProperty("user.dir") + "\\DungeonBoard\\Layer"));
		
		setVisible(true);
	}
	
	public void setDirectory(File folder) {
		if (folder != null && folder.exists()) {
			this.folder.setText(folder.getPath());
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
