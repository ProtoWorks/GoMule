package gomule.gui.desktop.generic;

import javax.swing.JComponent;

import gomule.gui.D2ItemContainer;
import gomule.gui.D2ItemList;

/**
 * view for content of internal frame/tab
 *
 * @author mbr
 */
public interface GoMuleView {
    
    JComponent getDisplay();
    
    D2ItemList getItemLists();
    
    D2ItemContainer getItemContainer();
    
    void setDisplayHandler(GoMuleViewDisplayHandler pDisplayHandler);
    
    GoMuleViewDisplayHandler getDisplayHandler();
}
