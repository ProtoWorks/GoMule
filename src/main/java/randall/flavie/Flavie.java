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
import lombok.SneakyThrows;

import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import gomule.item.D2Item;
import randall.d2files.D2TxtFile;
import randall.d2files.D2TxtFileItemProperties;
import randall.flavie.filters.FlavieDupeFilter;
import randall.flavie.filters.FlavieItemFilter;
import randall.util.RandallUtil;

/**
 * @author Marco
 */
public class Flavie {
    
    public static final String MATCHED_DIR = "." + File.separator;
    
    @Getter private final String reportName;
    private String dataFile;
    
    private final List<Object> datFile = new ArrayList<>();
    @Getter private final Map<String, D2Item> allItemsFP = new HashMap<>();
    @Getter private final Map<String, Long> runeCount = new HashMap<>();
    
    protected List<FlavieDupeFilter> filters = new ArrayList<>();
    
    private DataFileBuilder dataFileBuilder;
    private DirectD2Files directD2;
    private ReportBuilder reportBuilder;
    
    @Getter
    @Setter
    private int notMatched = 0;
    @Getter
    @Setter
    private int notMatchedType = 0;
    @Getter
    @Setter
    private int normalMatched = 0;
    @Getter
    @Setter
    private int multipleMatched = 0;
    @Getter
    @Setter
    private int dualFPMatched = 0;
    @Getter
    @Setter
    private int dupeMatched = 0;
    
    @SneakyThrows
    public Flavie(String reportName,
                  String reportTitle,
                  String dataFile,
                  String styleFile,
                  List<Object> fileNames,
                  boolean countAll,
                  boolean countEthereal,
                  boolean countStash,
                  boolean countChar) {
        this.reportName = reportName;
        
        ReportBuilder reportBuilder = new ReportBuilder(this);
        DirectD2Files directD2 = new DirectD2Files(this);
        DataFileBuilder dataFileBuilder = new DataFileBuilder(this);
        
        List<Object> datFile = new ArrayList<>();
        List<Object> dataFileObjects = dataFileBuilder.readDataFileObjects(dataFile, datFile);
        directD2.readDirectD2Files(dataFileObjects, fileNames);
        
        File dupeDirList = new File("dupelists");
        File[] dupeFiles = dupeDirList.listFiles();
        
        if (dupeFiles != null) {
            for (File dupeFile : dupeFiles) {
                if (dupeFile.getCanonicalPath().endsWith(".txt")) {
                    filters.add(new FlavieDupeFilter(dupeFile.getCanonicalPath(), new FileReader(dupeFile.getCanonicalPath())));
                }
                if (dupeFile.getCanonicalPath().endsWith(".zip")) {
                    ZipFile zip = new ZipFile(dupeFile.getCanonicalPath());
                    Enumeration<? extends ZipEntry> entries = zip.entries();
                    while (entries.hasMoreElements()) {
                        ZipEntry entry = entries.nextElement();
                        if (entry.getName().endsWith(".txt")) {
                            filters.add(new FlavieDupeFilter(dupeFile.getCanonicalPath() + ":" + entry.getName(), new InputStreamReader(zip.getInputStream(entry))));
                        }
                    }
                }
            }
        }
        
        reportBuilder.buildReport(reportTitle, reportName, dataFile, styleFile, datFile, countAll, countEthereal, countStash, countChar);
    }
    
    @Deprecated
    public boolean checkForRuneWord(String runes) {
        List<String> list = RandallUtil.split(runes, "-", false);
        return checkForRuneWord(list);
    }
    
    public boolean checkForRuneWord(List<String> runes) {
        if (runes.size() <= 1) {
            return false;
        }
        List<String> runeList = new ArrayList<>();
        for (String rune : runes) {
            D2TxtFileItemProperties props = D2TxtFile.MISC.searchColumns("name", rune + " Rune");
            if (props == null) {
                return false;
            }
            runeList.add(props.get("code"));
        }
        return D2TxtFile.RUNES.searchRuneWord((List) runeList) != null;
    }
    
    public void initializeFilters() {
        filters.forEach(FlavieItemFilter::initialize);
    }
    
    public boolean checkFilters(D2ItemInterface itemFound) {
        return filters.stream().allMatch(filter -> filter.check(itemFound));
    }
    
    public void finishFilters() {
        dupeMatched += filters.stream()
                              .mapToInt(filter -> {
                                  int dupeCount = filter.getDupeCount();
                                  filter.finish();
                                  return dupeCount;
                              })
                              .sum();
    }
}
