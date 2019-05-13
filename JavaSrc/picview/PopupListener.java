package picview;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

class PopupListener extends MouseAdapter {

    private JPopupMenu menu;

    public PopupListener(JPopupMenu menu) {
        this.menu = menu;
    }

    public void mousePressed(MouseEvent e) {
        maybeShowPopup(e);
    }

    public void mouseReleased(MouseEvent e) {
        maybeShowPopup(e);
    }

    private void maybeShowPopup(MouseEvent e) {
        if (SwingUtilities.isMiddleMouseButton(e)) {
            try {
                menu.show(e.getComponent(),
                        e.getX(), e.getY());
            } catch (IllegalComponentStateException exception) {
                exception.printStackTrace();
            }
        }
    }
}
