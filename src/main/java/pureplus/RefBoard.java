package pureplus;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.Iterator;

class BoardView extends JComponent
{
	RefBoardModel	model;
	Point			viewLocation; /* View Location unit is board */
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

		if (model!=null) {
			Iterator<ImagePanel>	imgp_itr = model.getDrawIterator();
			while (imgp_itr.hasNext()) {
				ImagePanel imgp = imgp_itr.next();
				if (imgp != null) {
					Rectangle imgbounds = imgp.getBounds();

					int   draw_w = (int)(imgbounds.width * scale);
					int   draw_h = (int)(imgbounds.height * scale);
					int   draw_x = (int)((imgbounds.x - viewLocation.x) * scale);
					int   draw_y = (int)((imgbounds.y - viewLocation.y) * scale);
		
					g2d.drawImage(imgp.getImage(), draw_x, draw_y, draw_w, draw_h, this);
				}
			}

			Rectangle selbounds = model.getSelectionBounds();
			if (selbounds != null) {
				/* Draw Handle */
				int   draw_w = (int)(selbounds.width * scale);
				int   draw_h = (int)(selbounds.height * scale);
				int   draw_x = (int)((selbounds.x - viewLocation.x) * scale);
				int   draw_y = (int)((selbounds.y - viewLocation.y) * scale);

				g2d.setColor(select_color);

				Stroke  bk_stroke = g2d.getStroke();
				g2d.setStroke(new BasicStroke(2));
				g2d.drawRect(draw_x, draw_y, draw_w, draw_h);
				g2d.setStroke(bk_stroke);

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

	public void update(Graphics g) {
		paint(g);
	}

	public void setModel(RefBoardModel refmodel) {
		this.model = refmodel;
		repaint();
	}

	public Point getViewLocation() {
		return this.viewLocation;
	}

	public void setViewLocation(Point pt) {
		this.viewLocation = pt;
	}

	public void moveLocationAsDisp(int x, int y) {
		this.viewLocation.translate((int)(x / scale), (int)(y / scale));
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(1200,768);
	}

	public double getScale() {
		return scale;
	}

	public void setScale(double scale) {
		this.scale = scale;
	}

	public void scaleUp(int num) {
		this.scale = this.scale - (num/50.0);
	}

	public Point getWorldPosition(Point pt) {
		return new Point((int)(pt.x / this.scale) + viewLocation.x,
						 (int)(pt.y / this.scale) + viewLocation.y);
	}

	public BoardView() {
		this.viewLocation = new Point();
		this.scale = 1.0;
	}
}



public class RefBoard
{
	JFrame       	frame;
	BoardView    	view;
	RefBoardModel	model;
	RefBoardDB	 	db;

	JFileChooser   fChooser;

	/* control */
    Point  		drag_start;
	ImagePanel  drag_pane;
	
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
			model.setImagePanelList(db.loadDB());
			view.repaint();
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
		model = new RefBoardModel();
		view.setModel(model);
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

		view.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent ev) {
				if (ev.getButton()==MouseEvent.BUTTON3) {
					// TODO Popupmenu
					drag_start = null;
					drag_pane = null;
				} else if (ev.getButton()==MouseEvent.BUTTON2) {
					drag_start = ev.getPoint();
					drag_pane = null;
				} else {
					Point  		wpt = view.getWorldPosition(ev.getPoint());
					ImagePanel 	tgt = model.getPanel(wpt);
					ImagePanel  bkimgp = model.getSelectedPanel();
					model.setSelectedPanel(tgt);
					if (tgt != bkimgp) view.repaint();

					if (tgt!=null) {
						drag_start = wpt;
						drag_pane = tgt;
					}
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				drag_start = null;
				drag_pane = null;	
			}

			@Override
			public void mouseClicked(MouseEvent ev) {
				Point  pt = view.getWorldPosition(ev.getPoint());
				ImagePanel  imgp = model.getPanel(pt);
				if (imgp != null) {
					if (!model.isTopPanel(imgp)) {
						model.pullupPanel(imgp);
					}
					model.setSelectedPanel(imgp);
					view.repaint();
				} else {
					if (model.getSelectedPanel()!=null) {
						model.setSelectedPanel(null);
						view.repaint();
					}
				}
			}
		});

		view.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent ev) {
				if (drag_start != null) {
					
					if (drag_pane != null) {
						Point  pt = view.getWorldPosition(ev.getPoint());
						drag_pane.translate(pt.x - drag_start.x, pt.y - drag_start.y);
						drag_start = pt;
						view.repaint();
					} else {
						Point  pt = ev.getPoint();
						view.moveLocationAsDisp(drag_start.x - pt.x, drag_start.y - pt.y);
						drag_start = pt;
						view.repaint();
					}
				}
			}
		});

		view.addMouseWheelListener(new MouseWheelListener() {
			@Override
			public void mouseWheelMoved(MouseWheelEvent ev) {
				if (drag_start == null) {
					view.scaleUp(ev.getWheelRotation());
					view.repaint();
				}
			}
		});

		fChooser.setFileFilter(new ImageFileFilter());

		frame.setSize(1200,1024);
	}

	public void openDB(String path) {
		db = new RefBoardDB(path);
		db.init();
		model.setImagePanelList(db.loadDB());
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
