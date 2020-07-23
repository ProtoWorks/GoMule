package gomule.gui.desktop.generic;

import java.util.Iterator;

import javax.swing.JComponent;

/**
 * desktop handler interface (generic for internal frames or tabs)
 *
 * @author mbr
 */
public interface GoMuleDesktop {
    
    JComponent getDisplay();
    
    GoMuleView getSelectedView();
    
    void addView(GoMuleView pView);
    
    void closeView(String pFileName);
    
    void removeView(GoMuleView pView);
    
    void closeViewAll();
    
    void showView(GoMuleView pView);
    
    Iterator getIteratorView();
    //	public Iterator		getIteratorContainer();
}
