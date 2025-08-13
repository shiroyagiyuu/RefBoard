package pureplus;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public class ImagePanel
{
	Rectangle		bounds;
	BufferedImage	image;

    public Rectangle getBounds() {
		return this.bounds;
	}

	public void setBounds(Rectangle rect) {
		this.bounds = rect;
	}

	public BufferedImage getImage() {
		return this.image;
	}

	public void setImage(BufferedImage img) {
		this.image = img;
	}

	int  id;

    public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void translate(int x, int y) {
		this.bounds.translate(x, y);
	}

    public ImagePanel(BufferedImage img, int id, int x, int y, int width, int height) {
		this.id = id;
		this.bounds = new Rectangle(x,y,width,height);
        this.image = img;
    }
}
