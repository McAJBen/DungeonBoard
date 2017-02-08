package loading;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import main.ControlPanel;
import main.FileChooser;

public class ControlLoading extends ControlPanel {
	private static final long serialVersionUID = 1L;
	
	public ControlLoading(DisplayLoadingPanel loadingDisplay) {
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createLineBorder(Color.GRAY, 10));
		JPanel northPanel = new JPanel();
		northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.X_AXIS));
		FileChooser fc = new FileChooser();
		fc.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loadingDisplay.setDirectory(fc.getFolder());
			}
		});
		northPanel.add(fc);
		add(northPanel, BorderLayout.NORTH);
		setVisible(true);
	}
	
	
}