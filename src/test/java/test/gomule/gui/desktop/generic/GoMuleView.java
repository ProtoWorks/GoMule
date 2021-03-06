package test.gomule.gui.desktop.generic;

import test.gomule.gui.D2ItemContainer;
import test.gomule.gui.D2ItemList;

import javax.swing.JComponent;

/**
 * view for content of internal frame/tab
 * @author mbr
 *
 */
public interface GoMuleView 
{
	public JComponent				getDisplay();
	public D2ItemList				getItemLists();
	public D2ItemContainer			getItemContainer();
	public void						setDisplayHandler(GoMuleViewDisplayHandler pDisplayHandler);
	public GoMuleViewDisplayHandler	getDisplayHandler();
}
