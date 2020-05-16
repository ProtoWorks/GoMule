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

import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;

@Value
@ToString(of = "subCat")
@EqualsAndHashCode(of = "subCat")
public class SubCatObject implements Comparable<SubCatObject> {
    
    String subCat;
    List<ItemObject> itemObjects = new ArrayList<>();
    CatObject catObject;
    
    public void addItemObject(ItemObject itemObject) {
        itemObjects.add(itemObject);
    }
    
    public ImmutableList<ItemObject> getItemObjects() {
        return ImmutableList.copyOf(itemObjects);
    }
    
    @Override
    public int compareTo(SubCatObject object) {
        return subCat.compareTo(object.subCat);
    }
}