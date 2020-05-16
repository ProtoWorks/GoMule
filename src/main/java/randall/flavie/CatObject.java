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

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@EqualsAndHashCode(of = "cat")
@ToString(of = "cat")
public class CatObject implements HasCounter, Comparable<CatObject> {
    
    private final String cat;
    private final List<SubCatObject> subCats = new ArrayList<>();
    private final String style;
    private final String group;
    
    private final boolean newRow;
    
    @Setter private PercentageCounter counter;
    
    public void addSubCat(SubCatObject subCatObject) {
        subCats.add(subCatObject);
    }
    
    public boolean isUnique() {
        return "unique".equals(group);
    }
    
    public boolean isSet() {
        return "set".equals(group);
    }
    
    public boolean isRune() {
        return "rune".equals(group);
    }
    
    public boolean isRuneWord() {
        return "runeword".equals(group);
    }
    
    public boolean isMisc() {
        return "misc".equals(group);
    }
    
    public boolean isGem() {
        return "gem".equals(group);
    }
    
    public boolean isSkiller() {
        return "skiller".equals(group);
    }
    
    @Override
    public int compareTo(CatObject object) {
        return getCat().compareTo(object.getCat());
    }
}
