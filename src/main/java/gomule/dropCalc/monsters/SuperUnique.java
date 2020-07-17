/*******************************************************************************
 *
 * Copyright 2008 Silospen
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
package gomule.dropCalc.monsters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import randall.d2files.D2TblFile;
import randall.d2files.D2TxtFile;
import randall.d2files.D2TxtFileItemProperties;

public class SuperUnique extends Monster {
    
    private String SUID;
    
    public SuperUnique(D2TxtFileItemProperties monRow, String monDiff, int monClass, int flag) {
        super(monRow, monDiff, monClass, flag);
        this.monID = monRow.get("Name");
        this.SUID = monRow.get("Class");
        this.monName = D2TblFile.getString(monID);
        mTuples = new ArrayList<>();
        Map<String, Integer> areas = new HashMap<>();
        findLocsSU(0, areas, monID);
        enterMonLevel(areas);
        List<String> initTCs = getInitTC(areas);
        mTuples = createTuples(areas, initTCs);
    }
    
    protected List<String> getInitTC(Map<String, Integer> monLvlAreas) {
        String header = "TC";
        List<String> result = new ArrayList<>();
        
        for (Map.Entry<String, Integer> entry : monLvlAreas.entrySet()) {
            String initTC;
            if (this.getMonDiff().equals("N")) {
                initTC = monRow.get(header);
            } else {
                int lvl = entry.getValue();
                if (this.getMonDiff().equals("NM")) {
                    initTC = bumpTC(D2TxtFile.SUPUNIQ.searchColumns("Name", monID).get(header + "(N)"), lvl);
                } else {
                    initTC = bumpTC(D2TxtFile.SUPUNIQ.searchColumns("Name", monID).get(header + "(H)"), lvl);
                }
            }
            result.add(initTC);
        }
        
        return result;
    }
    
    public void enterMonLevel(Map<String, Integer> monLvlAreas) {
        
        for (String area : monLvlAreas.keySet()) {
            if (monDiff.equals("N")) {
                monLvlAreas.put(area, Integer.parseInt(D2TxtFile.MONSTATS.searchColumns("Id", SUID).get("Level")) + 3);
            } else if (monDiff.equals("NM")) {
                monLvlAreas.put(area, Integer.parseInt(D2TxtFile.LEVELS.searchColumns("Name", area).get("MonLvl2Ex")) + 3);
            } else {
                monLvlAreas.put(area, Integer.parseInt(D2TxtFile.LEVELS.searchColumns("Name", area).get("MonLvl3Ex")) + 3);
            }
        }
    }
    
    public String getSUID() {
        return SUID;
    }
    
}
