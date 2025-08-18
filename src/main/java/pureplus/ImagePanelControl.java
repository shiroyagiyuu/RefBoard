package pureplus;

import java.awt.Rectangle;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Point;

public class ImagePanelControl {
    ImagePanel  imagePanel;

    public ImagePanel getImagePanel() {
        return imagePanel;
    }

    public void setImagePanel(ImagePanel imagePanel) {
        this.imagePanel = imagePanel;
        if (imagePanel != null) {
            setHandleBounds(imagePanel.getBounds());
        }
    }

    Rectangle[]    handle = new Rectangle[9];
    final static int  TOPLEFT    = 0;
    final static int  TOP        = 1;
    final static int  TOPRIGHT   = 2;
    final static int  LEFT       = 3;
    final static int  CENTER     = 4;
    final static int  RIGHT      = 5;
    final static int  BOTTOMLEFT = 6;
    final static int  BOTTOM     = 7;
    final static int  BOTTOMRIGHT= 8;

    final static int  HANDLE_SIZE = 10;

    void setHandleBounds(Rectangle baseBounds) {
        int  left_x   = baseBounds.x                  - (HANDLE_SIZE/2);
        int  right_x  = baseBounds.x+baseBounds.width - (HANDLE_SIZE/2);
        int  top_y    = baseBounds.y                  - (HANDLE_SIZE/2);
        int  bottom_y = baseBounds.y+baseBounds.height- (HANDLE_SIZE/2);

        handle[TOPLEFT]      = new Rectangle(left_x,  top_y,    HANDLE_SIZE, HANDLE_SIZE);
        handle[TOPRIGHT]     = new Rectangle(right_x, top_y,    HANDLE_SIZE, HANDLE_SIZE);
        handle[BOTTOMLEFT]   = new Rectangle(left_x,  bottom_y, HANDLE_SIZE, HANDLE_SIZE);
        handle[BOTTOMRIGHT]  = new Rectangle(right_x, bottom_y, HANDLE_SIZE, HANDLE_SIZE);

        int  ct_x      = left_x + HANDLE_SIZE;
        int  ct_y      = top_y  + HANDLE_SIZE;
        int  ct_width  = baseBounds.width-HANDLE_SIZE;
        int  ct_height = baseBounds.height-HANDLE_SIZE;

        handle[TOP]     = new Rectangle(ct_x,    top_y,     ct_width,    HANDLE_SIZE);
        handle[BOTTOM]  = new Rectangle(ct_x,    bottom_y,  ct_width,    HANDLE_SIZE);
        handle[LEFT]    = new Rectangle(left_x,  ct_y,      HANDLE_SIZE, ct_height);
        handle[RIGHT]   = new Rectangle(right_x, ct_y,      HANDLE_SIZE, ct_height);

        handle[CENTER]  = new Rectangle(ct_x, ct_y, ct_width, ct_height);
    }

    int checkHandleHit(Point pt) {
        if (imagePanel == null) return -1;

        for (int i=0; i<handle.length; i++) {
            if (handle[i].contains(pt)) {
                return i;
            }
        }

        return -1;  /* Not Hit */
    }

    void setCursor(Component comp, Point pt) {
        int   hit = checkHandleHit(pt);

        Cursor   c = Cursor.getDefaultCursor();
        switch(hit) {
            case TOPLEFT:
                c = Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR);
                break;
            case TOP:
                c = Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR);
                break;
            case TOPRIGHT:
                c = Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR);
                break;
            case LEFT:
                c = Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR);
                break;
            case CENTER:
                c = Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR);
                break;
            case RIGHT:
                c = Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR);
                break;
            case BOTTOMLEFT:
                c = Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR);
                break;
            case BOTTOM:
                c = Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR);
                break;
            case BOTTOMRIGHT:
                c = Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR);
                break;
        }
        comp.setCursor(c);
    }

    private Point       dragStart;
    private int         dragType;
    private Rectangle   baseBounds;

    public void startDrag(Point pt) {
        dragStart = pt;
        dragType = checkHandleHit(pt);
        baseBounds = imagePanel.getBounds();
    }

    private Rectangle getFixRect(Rectangle bounds, int dx, int dy) {
        double  dstx = bounds.getWidth()  + dx;
        double  dsty = bounds.getHeight() + dy;
        double  sc_x = dstx / bounds.getWidth();
        double  sc_y = dsty / bounds.getHeight();
        if (sc_x > sc_y) sc_x = sc_y;

        return new Rectangle(bounds.x, bounds.y, (int)(bounds.width * sc_x), (int)(bounds.height * sc_x));
    }

    public void drag(Point pt) {
        Rectangle  bounds = new Rectangle(baseBounds);
        int   dx = pt.x - dragStart.x;
        int   dy = pt.y - dragStart.y;
        Rectangle  fixBounds;

        switch (dragType) {
            case CENTER:
                bounds.x += dx;
                bounds.y += dy;
                break;
            case TOP:
                bounds.y      += dy;
                bounds.height -= dy;
                break;
            case LEFT:
                bounds.x      += dx;
                bounds.width  -= dx;
                break;
            case RIGHT:
                bounds.width  += dx;
                break;
            case BOTTOM:
                bounds.height += dy;
                break;
            case TOPLEFT:
                fixBounds = getFixRect(bounds, -dx, -dy);
                bounds.x -= fixBounds.width  - bounds.width;
                bounds.y -= fixBounds.height - bounds.height;
                bounds.width = fixBounds.width;
                bounds.height = fixBounds.height;
                break;
            case TOPRIGHT:
                fixBounds = getFixRect(bounds, dx, -dy);
                bounds.y -= fixBounds.height - bounds.height;
                bounds.width = fixBounds.width;
                bounds.height = fixBounds.height;
                break;
            case BOTTOMLEFT:
                fixBounds = getFixRect(bounds, -dx, dy);
                bounds.x -= fixBounds.width  - bounds.width;
                bounds.width = fixBounds.width;
                bounds.height = fixBounds.height;
                break;
            case BOTTOMRIGHT:
                fixBounds = getFixRect(bounds, dx, dy);
                bounds.width = fixBounds.width;
                bounds.height = fixBounds.height;
                break;
        }

        if (bounds.width > 0 && bounds.height > 0) {
            imagePanel.setBounds(bounds);
        }
    }

    public void endDrag() {
        setHandleBounds(imagePanel.getBounds());
        dragType = -1;
    }

    public boolean isDragging() {
        return (dragType>=0);
    }
}
