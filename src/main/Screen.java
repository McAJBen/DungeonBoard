package main;

import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.Rectangle;

/**
 * a container for a name and rectangle of a display
 * @author McAJBen <McAJBen@gmail.com>
 * @since 2.0
 */
public class Screen {
	
	/**
	 * the dimensions and position of the {@code Screen}
	 */
	private final Rectangle rectangle;
	
	/**
	 * the {@code IDString} obtained from the {@code GraphicsDevice}
	 */
	private final String name;
	
	/**
	 * creates a {@code Screen} instance by using a {@code GraphicsDevice}
	 * @param graphicsDevice the {@code GraphicsDevice} related to the display
	 */
	public Screen(GraphicsDevice graphicsDevice) {
		rectangle = graphicsDevice.getDefaultConfiguration().getBounds();
		name = graphicsDevice.getIDstring();
	}
	
	@Override
	public String toString() {
		return name + "  " + rectangle.width + "x" + rectangle.height;
	}

	/**
	 * gets the size of the {@code Screen} just like calling {@code getRectangle().getSize()}
	 * @return {@code Dimension} of the {@code Screen}
	 */
	public Dimension getSize() {
		return rectangle.getSize();
	}

	/**
	 * gets the {@code Rectangle} describing the {@code Screen's} position and size
	 * @return {@code Rectangle} of the {@code Screen}
	 */
	public Rectangle getRectangle() {
		return rectangle;
	}
}