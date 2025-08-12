import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.imageio.*;

class ImagePanel
{
	Rectangle		bounds;
	Image			image;
	boolean			select;

	public Rectangle getBounds() {
		return this.bounds;
	}

	public void setBounds(Rectangle rect) {
		this.bounds = rect;
	}

	public Image getImage() {
		return this.image;
	}

	public void setImage(Image img) {
		this.image = img;
	}

	public boolean isSelected() {
		return this.select;
	}

	public void setSelected(boolean s) {
		this.select = s;
	}
}

class BoardView extends JComponent
{
	ImagePanel[]	img_pane;
	Position		view_pos;
	double			scale;
	
	Color			select_color = new Color(128,128,255);

	public Rectangle getHandleBounds(int x, int y) {
		int handle_size = 10;

		Rectangle dest = new Rectangle(x-(size/2), y-(size/2), handle_size, handle_size);
	}

	public void paint(Graphics g) {
		Graphics2D  g2d = (Graphics2D)g;

		//g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		Rectangle  bounds = g2d.getClipBounds();

		g2d.setColor(Color.lightGray);
		g2d.fillRect(0, 0, bounds.width, bounds.height);

		if (img_pane!=null) {
			for (int i=0; i<img_pane.length; i++) {
				ImagePane  imgp = img_pane[i];
				if (imgp != null) {
					imgbounds = imgp.getBounds()

					int   draw_w = (int)(imgbounds.w * scale);
					int   draw_h = (int)(imgbounds.h * scale);
					int   draw_x = (int)((imgbounds.x - view_pos.x) * scale);
					int   draw_y = (int)((imgbounds.y - view_pos.y) * scale);
		
					g2d.drawImage(image, draw_x, draw_y, draw_w, draw_h, this);

					if (imgp.isSelected()) {
						/* Draw Handle */
						g.setColor(select_color);
						g2d.fill( getHandleBounds(draw_x,          draw_y         ) );
						g2d.fill( getHandleBounds(draw_x+draw_w/2, draw_y         ) );
						g2d.fill( getHandleBounds(draw_x+draw_w  , draw_y         ) );
						g2d.fill( getHandleBounds(draw_x,          draw_y+draw_h/2) );
						g2d.fill( getHandleBounds(draw_x+draw_w  , draw_y+draw_h/2) );
						g2d.fill( getHandleBounds(draw_x         , draw_y+draw_h  ) );
						g2d.fill( getHandleBounds(draw_x+draw_w/2, draw_y+draw_h  ) );
						g2d.fill( getHandleBounds(draw_x+draw_w  , draw_y+draw_h  ) );
					}
				}
			}
		}
	}

	public void update(Graphics g) {
		paint(g);
	}

	public void setImage(Image img) {
		this.image = img;
	}

	public Dimension getPrefferedSize() {
		return new Dimension(1200,768);
	}
}

public class BoardRef
{
	JFrame       frame;
	JDispView    cont;
	JDispLoader  ldr;

	public JDisp() {
		frame = new JFrame("JDisp");
		cont = new JDispView();
		
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent ev) {
				ldr.shutdown();
				//System.exit(0);
			}
		});
		frame.setContentPane(cont);

		cont.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent ev) {
				if (ldr!=null) { 
					if (ev.isPopupTrigger()) {
						ldr.previous();
						//System.out.println("previous");
					} else {
						ldr.next();
					}
				}
			}
		});

		cont.setFocusable(true);
		cont.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent ev) {
				//System.out.println("KeyEvent code="+ev.getKeyCode());
				if (ldr!=null) {
					switch(ev.getKeyCode()) {
					case KeyEvent.VK_DOWN:
					case KeyEvent.VK_RIGHT:
					case KeyEvent.VK_SPACE:
						ldr.next();
						break;
					case KeyEvent.VK_UP:
					case KeyEvent.VK_LEFT:
						ldr.previous();
					}
				}
			}
		});

		frame.setSize(1200,1024);

		ldr = null;
	}

	public void setPath(String path) {
		if (ldr != null) {
			ldr = null;
		}

		ldr = new JDispLoader(path);
		ldr.addJDispLoadListener(img -> {
			cont.setImage(img);
			cont.repaint();
		});
		ldr.start();
	}

	public void setVisible(boolean v) {
		frame.setVisible(v);
	}

	public boolean isVisible() {
		return frame.isVisible();
	}

	public static void main(String[] args) {
		JDisp jdisp = new JDisp();
		if (args.length>0) {
			jdisp.setPath(args[0]);
		}
		jdisp.setVisible(true);
	}
}
