/*******************************************************************************
 *
 * Copyright 2007 Randall
 *
 * This file is part of gomule.
 *
 * gomule is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * gomule is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * gomlue; if not, write to the Free Software Foundation, Inc., 51 Franklin St,
 * Fifth Floor, Boston, MA 02110-1301 USA
 *
 ******************************************************************************/
package randall.util;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

public class RandallPanel extends JPanel {
    
    private static final long serialVersionUID = -6556940562813366360L;
    public static final Integer NONE = 100;
    public static final Integer HORIZONTAL = 101;
    public static final Integer VERTICAL = 102;
    public static final Integer BOTH = 103;
    
    public static final int ANCHOR_NORTHWEST = GridBagConstraints.NORTHWEST;
    public static final int ANCHOR_NORTHEAST = GridBagConstraints.NORTHEAST;
    
    private int marginXSize = 2;
    private int marginYSize = -1;
    private int yPos = 0;
    
    private int margin = 1;
    //	private boolean iMarginAllSides = false;
    
    // sub panel -> all elements on X or Y of 0 does not get a top or left margin
    private boolean subPanel = false;
    
    private TitledBorder titledBorder = null;
    
    /**
     * Creates a new RandallPanel with a double buffer and a flow layout.
     */
    public RandallPanel() {
        super();
        init();
    }
    
    public RandallPanel(boolean subPanel) {
        super();
        init();
        if (subPanel) {
            setSubPanel();
        }
    }
    
    public void setBorder(String title) {
        titledBorder = new TitledBorder(new EtchedBorder(), title);
        setBorder(titledBorder);
    }
    
    private void init() {
        setLayout(new GridBagLayout());
    }
    
    public void finishDefaultPanel() {
        yPos = yPos + 100;
        addToPanel(new JPanel(), 0, 250, 1, VERTICAL);
    }
    
    /**
     * Set the right column nr for the panel
     */
    public void setMarginRightColumnNr(int columnNr) {
        marginXSize = columnNr;
    }
    
    public void setMarginBottomRowNrColumnNr(int rowNr) {
        marginYSize = rowNr;
    }
    
    public void setMarginRightBottom(int columnNr, int rowNr) {
        marginXSize = columnNr;
        marginYSize = rowNr;
    }
    
    public void setMargin(int margin) {
        this.margin = margin;
    }
    
    //	public void setMarginAllSides(boolean pMarginAllSides)
    //	{
    //		iMarginAllSides = pMarginAllSides;
    //	}
    
    /**
     * Set the right column nr for the sub panel
     * (does not add left/upper margin when left/up coordinate is 0)
     */
    public void setSubPanel() {
        subPanel = true;
    }
    
    public int getPanelColumnNr() {
        return marginXSize;
    }
    
    public void addToPanel(JComponent component, int x, int y, int sizeX, Object constraint) {
        addToPanel(component, x, y, sizeX, 1, constraint, -1.0, -1.0, -1);
    }
    
    public void addToPanel(JComponent component, int x, int y, int sizeX, double weightX, Object constraint) {
        addToPanel(component, x, y, sizeX, 1, constraint, weightX, -1.0, -1);
    }
    
    public void addToPanel(JComponent component, int x, int y, int sizeX, int sizeY, Object constraint) {
        addToPanel(component, x, y, sizeX, sizeY, constraint, -1.0, -1.0, -1);
    }
    
    public void addToPanel(JComponent component, int x, int y, int sizeX, int sizeY, Object constraint, int constraintAnchor) {
        addToPanel(component, x, y, sizeX, sizeY, constraint, -1.0, -1.0, constraintAnchor);
    }
    
    public void addToPanel(JComponent component, int x, int y, int sizeX, int sizeY, Object constraint, double weightX, double weightY, int constraintAnchor) {
        double lWeightX = 0.0;
        double lWeightY = 0.0;
        
        int gridbagConstraint = GridBagConstraints.NONE;
        int gridbagAnchor = ANCHOR_NORTHWEST;
        
        int marginTop = margin;
        int marginLeft = margin;
        int marginBottom = 0;
        //        if ( marginAllSides )
        //        {
        //			lMarginBottom = margin;
        //        }
        int marginRight = margin;
        
        if (constraint == HORIZONTAL) {
            lWeightX = 1.0;
            gridbagConstraint = GridBagConstraints.HORIZONTAL;
        }
        if (constraint == VERTICAL) {
            lWeightY = 1.0;
            gridbagConstraint = GridBagConstraints.VERTICAL;
        }
        if (constraint == BOTH) {
            lWeightX = 1.0;
            lWeightY = 1.0;
            gridbagConstraint = GridBagConstraints.BOTH;
        }
        
        if (weightX >= 0.0) {
            lWeightX = weightX;
        }
        if (weightY >= 0.0) {
            lWeightY = weightY;
        }
        if (constraintAnchor >= 0) {
            gridbagAnchor = constraintAnchor;
        }
        
        if (x + sizeX < marginXSize) {
            marginRight = 0;
        }
        if (marginYSize != -1 && y + sizeY == marginYSize) {
            marginBottom = margin;
        }
        if (subPanel) {
            marginRight = 0;
            if (x == 0) {
                marginLeft = 0;
            }
            if (y == 0) {
                marginTop = 0;
            }
        }
        
        //        if ( component instanceof JCheckBox )
        //        {
        //        	lMarginLeft -= 4;
        //        }
        
        this.add(component, new GridBagConstraints(x, y, sizeX, sizeY, lWeightX, lWeightY, gridbagAnchor, gridbagConstraint, new Insets(marginTop, marginLeft, marginBottom, marginRight), 0, 0));
        
        this.yPos = y + 1;
        
    }
    
    /**
     * Removes the given Component from the Panel if not null
     *
     * @param component The Component to be removed
     */
    public void removeFromPanel(JComponent component) {
        if (component != null) {
            this.remove(component);
        }
    }
    
    public String getTitle() {
        return titledBorder.getTitle();
    }
    
    public void setTitle(String title) {
        titledBorder.setTitle(title);
    }
    
}