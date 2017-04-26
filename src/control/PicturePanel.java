package control;

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

public abstract class PicturePanel extends JPanel implements ActionListener {
	
	private static final long serialVersionUID = 2972394170217781329L;
	
	public PicturePanel() {
		setLayout(new GridLayout(0, 6));
		setBorder(BorderFactory.createEmptyBorder());
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		press((JButton) (e.getSource()));
	}
	
	protected abstract void press(JButton button);

	public void addImage(File f) {
		try {
		    ImageIcon icon = new ImageIcon(ImageIO.read(f).getScaledInstance(100, 100, BufferedImage.SCALE_SMOOTH));
		    JButton jcb = new JButton(icon);
		    jcb.setBackground(Settings.DISABLE_COLOR);
		    jcb.setMargin(new Insets(0, 0, 0, 0));
		    jcb.setFocusPainted(false);
		    jcb.addActionListener(this);
		    jcb.setText(f.getName());
		    jcb.setVerticalTextPosition(SwingConstants.BOTTOM);
		    jcb.setHorizontalTextPosition(SwingConstants.CENTER);
		    add(jcb);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void clearImages() {
		for (Component c: getComponents()) {
			if (c.getClass().equals(JButton.class)) {
				remove(c);
			}
		}
	}
}