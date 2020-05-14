package gomule.gui.desktop.generic;

import java.awt.Cursor;

/**
 * handler for actual gui element handling the display on the desktop
 *
 * @author mbr
 */
public interface GoMuleViewDisplayHandler {
    
    void setTitle(String title);
    
    void setCursor(Cursor pCursor);
    
    void addDesktopListener(GoMuleDesktopListener pListener);
}
