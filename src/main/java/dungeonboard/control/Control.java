package dungeonboard.control;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

import dungeonboard.Settings;

public abstract class Control extends JPanel {

	private static final long serialVersionUID = 7062093943678033069L;
	
	public Control() {
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createLineBorder(Settings.BACKGROUND, 5));
	}
	
	protected JPanel getNorthPanel() {
		JPanel northPanel = new JPanel();
		northPanel.setBackground(Settings.CONTROL_BACKGROUND);
		northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.X_AXIS));
		
		JButton refreshButton = Settings.createButton(Settings.ICON_REFRESH);
		refreshButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				load();
			}
		});
		northPanel.add(refreshButton);
		
		northPanel.repaint();
		
		return northPanel;
	}
	
	protected abstract void load();
}