package main;

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

public abstract class PicturePanel extends JPanel {
	
	private static final long serialVersionUID = 2972394170217781329L;
	
	public PicturePanel() {
		setLayout(new GridLayout(0, 6));
		setBorder(BorderFactory.createEmptyBorder());
	}
	
	public void addButton(File file) {
		try {
			JButton button = new JButton(
					file.getName(),
					new ImageIcon(ImageIO.read(file).getScaledInstance(100, 100, BufferedImage.SCALE_SMOOTH)));
			button.setMargin(new Insets(0, 0, 0, 0));
			button.setFocusPainted(false);
			button.setVerticalTextPosition(SwingConstants.BOTTOM);
			button.setHorizontalTextPosition(SwingConstants.CENTER);
			button.setBackground(Settings.DISABLE_COLOR);
			button.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					JButton button = (JButton) arg0.getSource();
					String name = button.getText();
					if (button.getBackground() == Settings.DISABLE_COLOR) {
						select(name);
						button.setBackground(Settings.ENABLE_COLOR);
					}
					else {
						deselect(name);
						button.setBackground(Settings.DISABLE_COLOR);
					}
				}
			});
			add(button);
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
	
	protected abstract void select(String name);
	
	protected abstract void deselect(String name);
}