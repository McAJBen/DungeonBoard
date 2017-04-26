package image;

import java.awt.Component;

import javax.swing.JButton;

import control.PicturePanel;
import main.Main;
import main.Settings;

public class PictureImagePanel extends PicturePanel {
	
	private static final long serialVersionUID = 2972394170217781329L;
	
	@Override
	protected void press(JButton button) {
		if (button.getBackground() == Settings.DISABLE_COLOR) {
			for (Component c: getComponents()) {
				c.setBackground(Settings.DISABLE_COLOR);
			}
			button.setBackground(Settings.ENABLE_COLOR);
			Main.DISPLAY_IMAGE.setImage(button.getText());
		}
		else {
			button.setBackground(Settings.DISABLE_COLOR);
			Main.DISPLAY_IMAGE.setImage(null);
		}
	}
}