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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import randall.util.RandallUtil;

/**
 * @author Marco
 * <p>
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DataFileBuilder {
    
    private Flavie iFlavie;
    //	private FlavieSettingsPanel	iFlaviePanel;
    
    public DataFileBuilder(Flavie pFlavie) {
        iFlavie = pFlavie;
        //		iFlaviePanel = iFlavie.getSettingsPanel();
    }
    
    public String getFileName() {
        return "Unknown";
    }
    
    public List<Object> readDataFileObjects(String pFileName, List<Object> pDatFile) throws Exception {
        if (pFileName == null || "".equals(pFileName.trim())) {
            throw new Exception("No data file set, please set the data file in the Flavie tab");
        }
        
        List<Object> lList = new ArrayList<>();
        
        File lData = new File(pFileName);
        
        if (!lData.exists()) {
            throw new Exception("Data File " + pFileName + " does not exist");
        } else if (!lData.isFile()) {
            throw new Exception("Data File " + pFileName + " is not a file");
        } else if (!lData.canRead()) {
            throw new Exception("Data File " + pFileName + " can not be read");
        }
        
        BufferedReader lIn = new BufferedReader(new FileReader(lData));
        
        CatObject lCat = null;
        SubCatObject lSubCat = null;
        
        List<Object> lTotalObjectList = new ArrayList<>();
        
        String lLine = lIn.readLine();
        while (lLine != null) {
            // Now parse the FARA file
            if (lLine.startsWith("***")) {
                String lWork = RandallUtil.merge(RandallUtil.split(lLine, "]", true), "");
                lWork = lWork.substring(3);
                
                List<String> lItem = RandallUtil.split(lWork, "[", false);
                
                if (lItem.size() != 3 && lItem.size() != 4) {
                    throw new Exception("Incorrect line format " + lLine);
                }
                TotalObject lTotal = new TotalObject(lItem.get(0), lItem.get(1), lItem.get(2));
                if (lItem.size() == 4) {
                    for (int i = 0; i < lTotalObjectList.size(); i++) {
                        TotalObject lParent = (TotalObject) lTotalObjectList.get(i);
                        if (lItem.get(3).equals(lParent.getShortString())) {
                            lParent.addChild(lTotal);
                        }
                    }
                }
                lTotalObjectList.add(lTotal);
                pDatFile.add(lTotal);
            } else if (lLine.startsWith("**")) {
                //				String lWork = lLine.replaceAll("]","");
                String lWork = RandallUtil.merge(RandallUtil.split(lLine, "]", true), "");
                
                lWork = lWork.substring(2);
                List<String> lItem = RandallUtil.split(lWork, "[", false);
                
                if (lItem.size() != 3 && lItem.size() != 4) {
                    throw new Exception("Incorrect line format " + lLine);
                }
                lCat = CatObject.builder().cat(lItem.get(0))
                                .style(lItem.get(1))
                                .newRow(lItem.get(2).equals("newrow"))
                                .group(lItem.size() == 4 ? lItem.get(3) : null)
                                .build();
                if (lItem.size() == 4) {
                    for (int i = 0; i < lTotalObjectList.size(); i++) {
                        TotalObject lParent = (TotalObject) lTotalObjectList.get(i);
                        if (lItem.get(3).equals(lParent.getShortString())) {
                            lParent.addChild(lCat);
                        }
                    }
                }
                //				System.err.println("Style: " + lCat.getStyle() );
                //				iCatObjectList.add(lCat);
                pDatFile.add(lCat);
            } else if (lLine.startsWith("*")) {
                lSubCat = new SubCatObject(lLine.substring(1), lCat);
                lCat.addSubCat(lSubCat);
                pDatFile.add(lSubCat);
            } else {
                // TODO: Start item
                List<String> lItem = RandallUtil.split(lLine, ",", false);
                if (lItem.size() == 1) {
                    // Ok, found a item definition
                    ItemObject lItemObject = new ItemObject(lItem.get(0), "", lSubCat);
                    lList.add(lItemObject);
                    lSubCat.addItemObject(lItemObject);
                    pDatFile.add(lItemObject);
                } else if (lItem.size() == 2 || lItem.size() == 3) {
                    if ("Fire Skills".equals(lItem.get(0))) {
                        System.err.println("Test");
                    }
                    // Ok, found a item definition
                    ItemObject lItemObject = new ItemObject(lItem.get(0), lItem.get(1), lSubCat);
                    if (iFlavie.checkForRuneWord(lItem.get(0), lItem.get(1))) {
                        lItemObject.setRuneWord(true);
                    } else {
                        checkForTC(lItemObject, lItem.get(1));
                    }
                    if (lItem.size() == 3) {
                        // xTODO: complete
                        //					    System.err.println("Read jewelry");
                        
                        String lExtra = lItem.get(2);
                        int lDisplayStart = lExtra.indexOf("(");
                        int lDisplayEnd = lExtra.indexOf(")");
                        int lDetectStart = lExtra.indexOf("[");
                        int lDetectEnd = lExtra.indexOf("]");
                        
                        if (lDisplayStart != -1 && lDisplayEnd != -1 &&
                                lDisplayStart < lDisplayEnd
                                && lDetectStart != -1 && lDetectEnd != -1 &&
                                lDetectStart < lDetectEnd
                        ) {
                            String lDisplay = lExtra.substring(lDisplayStart + 1, lDisplayEnd);
                            List<String> lDetect = RandallUtil.split(lExtra.substring(lDetectStart + 1, lDetectEnd), "/", true);
                            
                            lItemObject.setExtraDisplay(lDisplay);
                            lItemObject.setExtraDetect(lDetect);
                        }
                    }
                    lList.add(lItemObject);
                    lSubCat.addItemObject(lItemObject);
                    pDatFile.add(lItemObject);
                }
            }
            lLine = lIn.readLine();
        }
        
        //		for ( int i = 0 ; i < lList.size() ; i++ )
        //		{
        //			System.err.println("Item " + i + ": " + lList.get(i));
        //		}
        
        return lList;
    }
    
    public void checkForTC(ItemObject pItemObject, String lLine) {
        List<String> lList = RandallUtil.split(lLine, "(", false);
        if (lList.size() == 2 && lList.get(1).toUpperCase().startsWith("TC")) {
            pItemObject.setItemType(lList.get(0).trim());
        } else {
            pItemObject.setItemType(lLine);
        }
    }
    
}
