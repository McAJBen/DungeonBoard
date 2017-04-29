package layer;

import javax.swing.JButton;

import main.Main;
import main.PicturePanel;
import main.Settings;

public class PictureLayerPanel extends PicturePanel {
	
	private static final long serialVersionUID = 2972394170217781329L;
	
	@Override
	protected void press(JButton button) {
		if (button.getBackground() == Settings.DISABLE_COLOR) {
			button.setBackground(Settings.ENABLE_COLOR);
			Main.DISPLAY_LAYER.addImage(button.getText());
		}
		else if (button.getBackground() == Settings.ENABLE_COLOR) {
			button.setBackground(Settings.DISABLE_COLOR);
			Main.DISPLAY_LAYER.removeImage(button.getText());
		}
	}
}