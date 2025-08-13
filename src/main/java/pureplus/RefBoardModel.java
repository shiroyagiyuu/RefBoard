package pureplus;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.LinkedList;
import java.util.Iterator;

public class RefBoardModel {
    LinkedList<ImagePanel>  img_panels;

    int    mode = 0;
    final static int MODE_VIEW=0;
    final static int MODE_EDIT=1;

    public Iterator<ImagePanel> getDrawIterator() {
        return img_panels.descendingIterator();
    }

    public Rectangle getSelectionBounds() {
        if (mode==MODE_EDIT) {
            return img_panels.getFirst().getBounds();
        }
        return null;
    }

    public ImagePanel getPanel(Point pt) {
        for (ImagePanel imgp : img_panels) {
            if (imgp.getBounds().contains(pt)) {
                return imgp;
            }
        }
        return null;
    }

    public ImagePanel getImagePanelFromID(int id) {
        for (ImagePanel imgp : img_panels) {
            if (imgp.getId() == id) {
                return imgp;
            }
        }
        return null;
    }

    public boolean isTopPanel(ImagePanel imgp) {
        ImagePanel   topp = img_panels.getFirst();
        if (topp!=null) {
            return topp.getId() == imgp.getId();
        }
        return false;
    }

    public ImagePanel getTopPanel() {
        return img_panels.getFirst();
    }

    public void pullupPanel(ImagePanel imgp) {
        img_panels.remove(imgp);
        img_panels.addFirst(imgp);
    }

    public void setImagePanelList(LinkedList<ImagePanel> list) {
        img_panels = list;
    }

    public void setEditMode(boolean b) {
        if (b) {
            if (mode!=MODE_EDIT) System.out.println("mode to edit");
            mode = MODE_EDIT; }
        else {
            if (mode!=MODE_VIEW) System.out.println("mode to view");
            mode = MODE_VIEW;
        }
    }

    public boolean isEditMode() {
        return (mode==MODE_EDIT);
    }

    public RefBoardModel() {
        mode = MODE_VIEW;
        img_panels = new LinkedList<>();
    }
}
