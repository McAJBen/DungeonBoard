package image;

import java.awt.Component;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import main.Settings;

public class PictureImagePanel extends JPanel implements ActionListener {
	
	private static final long serialVersionUID = 2972394170217781329L;
	
	private DisplayImagePanel displayImagePanel;
	
	public PictureImagePanel(DisplayImagePanel displayImagePanel) {
		setLayout(new GridLayout(0, 3));
		setBorder(BorderFactory.createEmptyBorder());
		this.displayImagePanel = displayImagePanel;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		JButton button = (JButton) (e.getSource());
		
		if (button.getBackground() == Settings.DISABLE_COLOR) {
			for (Component c: getComponents()) {
				c.setBackground(Settings.DISABLE_COLOR);
			}
			button.setBackground(Settings.ENABLE_COLOR);
			displayImagePanel.setImage(button.getText());
		}
		else {
			button.setBackground(Settings.DISABLE_COLOR);
			displayImagePanel.setImage(null);
		}
	}

	public void addImage(File f) {
		try {
		    BufferedImage img = ImageIO.read(f);
		    ImageIcon icon = new ImageIcon(img.getScaledInstance(192, 108, BufferedImage.SCALE_FAST));
		    JButton jcb = new JButton(icon);
		    jcb.setBackground(Settings.DISABLE_COLOR);
		    jcb.setMargin(new Insets(0, 0, 0, 0));
		    
		    jcb.addActionListener(this);
		    jcb.setText(f.getName());
		    jcb.setVerticalTextPosition(SwingConstants.BOTTOM);
		    jcb.setHorizontalTextPosition(SwingConstants.CENTER);
		    add(jcb);
		    
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}