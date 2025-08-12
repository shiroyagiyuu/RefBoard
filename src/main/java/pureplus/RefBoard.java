package pureplus;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

class BoardView extends JComponent
{
	ImagePanel[]	img_pane;
	Point			position;
	double			scale;
	
	Color			select_color = new Color(128,128,255);

	public Rectangle getHandleBounds(int x, int y) {
		int handle_size = 10;

		return new Rectangle(x-(handle_size/2), y-(handle_size/2), handle_size, handle_size);
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
				ImagePanel  imgp = img_pane[i];
				if (imgp != null) {
					Rectangle imgbounds = imgp.getBounds();

					int   draw_w = (int)(imgbounds.width * scale);
					int   draw_h = (int)(imgbounds.height * scale);
					int   draw_x = (int)((imgbounds.x - position.x) * scale);
					int   draw_y = (int)((imgbounds.y - position.y) * scale);
		
					g2d.drawImage(imgp.getImage(), draw_x, draw_y, draw_w, draw_h, this);

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

	public void setImages(ImagePanel[] pane) {
		this.img_pane = pane;
		repaint();
	}

	public Point getPosition() {
		return this.position;
	}

	public void setPosition(Point pt) {
		this.position = pt;
	}

	public Dimension getPrefferedSize() {
		return new Dimension(1200,768);
	}

	public BoardView() {
		this.position = new Point();
		this.scale = 1.0;
	}
}

public class RefBoard
{
	JFrame       frame;
	BoardView    view;
	RefBoardDB	 db;

	JFileChooser   fChooser;

	void addImage(File f, int x, int y) {
		BufferedImage  img;
		ImagePanel     imgp;
		int            width,height;

		try {
			img = ImageIO.read(f);
			
			width = img.getWidth();
			height = img.getHeight();
			imgp = new ImagePanel(img, -1, x, y, width, height);

			db.insertFile(f, imgp);
			view.setImages(db.loadDB());
		} catch (IOException ex) {
			ex.printStackTrace(System.err);
		}

	}

	public void openImage() {
		int  ret = fChooser.showOpenDialog(frame);
		
		if (ret == JFileChooser.APPROVE_OPTION) {
			File  f = fChooser.getSelectedFile();
			addImage(f, 10, 10);
		}
	}

	public RefBoard() {
		frame = new JFrame("RefBoard");
		view = new BoardView();
		fChooser = new JFileChooser();
		
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent ev) {
				System.exit(0);
			}
		});
		frame.setContentPane(view);

		JMenuBar  mbar = new JMenuBar();
		JMenu     filemenu = new JMenu("File");

		AbstractAction  openact = new AbstractAction("Open") {
			public void actionPerformed(ActionEvent e) {
				openImage();
			}
		};

		JMenuItem  openitem = new JMenuItem(openact);
		filemenu.add(openitem);
	
		mbar.add(filemenu);

		frame.setJMenuBar(mbar);

		/*
		view.addMouseListener(new MouseAdapter() {
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

		view.setFocusable(true);
		view.addKeyListener(new KeyAdapter() {
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
		*/

		fChooser.setFileFilter(new ImageFileFilter());

		frame.setSize(1200,1024);
	}

	public void openDB(String path) {
		db = new RefBoardDB(path);
		db.init();
		ImagePanel[]  pane = db.loadDB();
		view.setImages(pane);
	}

	public void setVisible(boolean v) {
		frame.setVisible(v);
	}

	public boolean isVisible() {
		return frame.isVisible();
	}

	public static void main(String[] args) {
		RefBoard refboard = new RefBoard();
		String   dbname = "refboarddata.db";

		if (args.length>0) {
			dbname = args[0];
		}

		refboard.openDB(dbname);
		refboard.setVisible(true);
	}
}
