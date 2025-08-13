package pureplus;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.LinkedList;
import java.util.Iterator;

public class RefBoardModel {
    LinkedList<ImagePanel>  img_panels;

    ImagePanel  selected_panel;

    public Iterator<ImagePanel> getDrawIterator() {
        return img_panels.descendingIterator();
    }

    public Rectangle getSelectionBounds() {
        if (selected_panel != null) {
            return selected_panel.getBounds();
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

    public void setSelectedPanel(ImagePanel panel) {
        this.selected_panel = panel;
    }

    public ImagePanel getSelectedPanel() {
        return this.selected_panel;
    }

    public RefBoardModel() {
        selected_panel = null;
        img_panels = new LinkedList<>();
    }
}
