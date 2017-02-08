package layer;
import java.awt.Color;
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

public class PicturePanel extends JPanel implements ActionListener {
	private static final long serialVersionUID = 1L;
	
	private DisplayLayerPanel window;
	private static final Color ENABLE_COLOR = Color.GREEN;
	private static final Color DISABLE_COLOR = Color.GRAY;
	
	public PicturePanel(DisplayLayerPanel window) {
		setLayout(new GridLayout(0, 3));
		setBorder(BorderFactory.createEmptyBorder());
		this.window = window;
	}

	public void addImage(File f) {
		try {
		    BufferedImage img = ImageIO.read(f);
		    ImageIcon icon = new ImageIcon(img.getScaledInstance(192, 108, BufferedImage.SCALE_FAST));
		    JButton jcb = new JButton(icon);
		    jcb.setBackground(DISABLE_COLOR);
		    jcb.setMargin(new Insets(0, 0, 0, 0));
		    
		    jcb.addActionListener(this);
		    jcb.setText(f.getName());
		    jcb.setVerticalTextPosition(SwingConstants.BOTTOM);
		    jcb.setHorizontalTextPosition(SwingConstants.CENTER);
		    add(jcb);
		    
		} catch (IOException e) {
			
		}
	}
	
	public void clearImages() {
		for (Component c: getComponents()) {
			if (c.getClass().equals(JButton.class)) {
				remove(c);
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JButton button = (JButton) (e.getSource());
		if (button.getBackground() == DISABLE_COLOR) {
			button.setBackground(ENABLE_COLOR);
			window.addImage(button.getText());
		}
		else if (button.getBackground() == ENABLE_COLOR) {
			button.setBackground(DISABLE_COLOR);
			window.removeImage(button.getText());
		}
	}
	
	
	
	
}