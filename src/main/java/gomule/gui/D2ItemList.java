/*
 * Created on 5-jun-2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gomule.gui;

import java.io.PrintWriter;
import java.util.List;

import gomule.item.D2Item;
import gomule.util.D2Project;

/**
 * @author Marco
 * <p>
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface D2ItemList {
    
    void ignoreItemListEvents();
    
    void listenItemListEvents();
    
    boolean containsItem(D2Item pItem);
    
    void removeItem(D2Item pItem);
    
    void addItem(D2Item pItem);
    
    List<Object> getItemList();
    
    int getNrItems();
    
    String getFilename();
    
    boolean isModified();
    
    void addD2ItemListListener(D2ItemListListener pListener);
    
    void removeD2ItemListListener(D2ItemListListener pListener);
    
    boolean hasD2ItemListListener();
    
    void save(D2Project pProject);
    
    boolean isSC();
    
    boolean isHC();
    
    void fullDump(PrintWriter pWriter);
    
    void initTimestamp();
    
    boolean checkTimestamp();
    
    void fireD2ItemListEvent();
}
