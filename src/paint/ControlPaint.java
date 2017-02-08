package paint;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import main.ControlPanel;
import main.FileChooser;

public class ControlPaint extends ControlPanel {
	
	private static final long serialVersionUID = 1L;
	
	private ImageIcon[] drawStyle;
	private ImageIcon[] drawMode;
	private ImageIcon[] penType;
	private DrawPanel drawPanel;
	private DisplayPaintPanel paintDisplay;
	JButton updateScreen;
	
	public ControlPaint(Dimension displaySize, DisplayPaintPanel disp) {
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createLineBorder(Color.GRAY, 10));
		drawStyle = new ImageIcon[3];
		drawStyle[0] = createImageIcon("/resources/squigle.gif");
		drawStyle[1] = createImageIcon("/resources/vertical.gif");
		drawStyle[2] = createImageIcon("/resources/horizontal.gif");
		drawMode = new ImageIcon[4];
		drawMode[0] = createImageIcon("/resources/mouse.gif");
		drawMode[1] = createImageIcon("/resources/visible.gif");
		drawMode[2] = createImageIcon("/resources/invisible.gif");
		drawMode[3] = createImageIcon("/resources/move.gif");
		penType = new ImageIcon[2];
		penType[0] = createImageIcon("/resources/square.gif");
		penType[1] = createImageIcon("/resources/circle.gif");
		
		paintDisplay = disp;
		
		drawPanel = new DrawPanel(displaySize, disp);
		
		JPanel northPanel = new JPanel();
		northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.X_AXIS));
		
		setFocusable(true);
		
		FileChooser fc = new FileChooser();
		fc.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setImage(fc.getImage());
			}
		});
		northPanel.add(fc);
		
		JButton drawStyleButton = new JButton(drawStyle[0]);
		drawStyleButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				drawPanel.toggleStyle();
				drawStyleButton.setIcon(drawStyle[drawPanel.getStyle()]);
			}
		});
		northPanel.add(drawStyleButton);
		
		JButton shape = new JButton(penType[0]);
		shape.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				drawPanel.togglePen();
				shape.setIcon(penType[drawPanel.getPen()]);
			}
		});
		northPanel.add(shape);
		
		JButton drawModeButton = new JButton(drawMode[0]);
		drawModeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				drawPanel.toggleDrawMode();
				drawModeButton.setIcon(drawMode[drawPanel.getDrawMode()]);
			}
		});
		northPanel.add(drawModeButton);
		
		JSlider slider = new JSlider(SwingConstants.HORIZONTAL, 10, 100, 25);
		slider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				drawPanel.setRadius(slider.getValue());
			}
		});
		northPanel.add(slider);
		
		updateScreen = new JButton("Update Screen");
		updateScreen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				paintDisplay.setMask(drawPanel.getMask());
				paintDisplay.changeWindowPos(drawPanel.getWindowPos());
				updateScreen.setEnabled(false);
			}
		});
		northPanel.add(updateScreen);
		drawPanel.setUpdateButton(updateScreen);
		
		add(northPanel, BorderLayout.NORTH);
		add(drawPanel, BorderLayout.CENTER);
		
		setVisible(true);
	}
	
	private static ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = FileChooser.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }

	public void setImage(BufferedImage image) {
		if (image != null) {
			drawPanel.setImage(image);
			paintDisplay.setMask(drawPanel.getMask());
			paintDisplay.setImage(image);
		}
	}
}