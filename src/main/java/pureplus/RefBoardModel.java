package pureplus;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.LinkedList;
import java.util.Iterator;

public class RefBoardModel {
    LinkedList<ImagePanel>  img_panels;

    boolean  selected;

    public Iterator<ImagePanel> getDrawIterator() {
        return img_panels.descendingIterator();
    }

    public Rectangle getSelectionBounds() {
        if (selected) {
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

    public void setSelected(boolean sel) {
        this.selected = sel;
    }

    public boolean isSelected() {
        return selected;
    }

    public RefBoardModel() {
        selected = false;
        img_panels = new LinkedList<>();
    }
}
