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
package randall.flavie;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@ToString(of = "name")
public class ItemObject {
    
    private final String name;
    private final String info;
    private final List<D2ItemInterface> itemInstances = new ArrayList<>();
    private final SubCatObject subCatObject;
    @Setter private boolean runeWord = false;
    @Setter private String itemType;
    
    @Setter private String extraDisplay;
    @Setter private List<String> extraDetect;
    
    public ItemObject(String name, String info, SubCatObject subCatObject) {
        this.name = name;
        this.info = info;
        this.subCatObject = subCatObject;
    }
    
    public void addItemInstance(D2ItemInterface instance) {
        itemInstances.add(instance);
    }
}