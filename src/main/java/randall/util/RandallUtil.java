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

import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

@UtilityClass
public class RandallUtil {
    
    public String merge(@NonNull List<String> list, @NonNull String join) {
        return String.join(join, list);
    }
    
    public List<String> split(@NonNull String input, @NonNull String separator, boolean ignoreCase) {
        
        if (input.equals("")) {
            return Lists.newArrayList();
        }
        
        if (!ignoreCase) {
            return Arrays.stream(input.split(Pattern.quote(separator)))
                         .map(String::trim)
                         .collect(Collectors.toList());
        }
        String lowerInput = input.toLowerCase();
        String lowerSeparator = separator.toLowerCase();
        
        // fast quit
        if (!lowerInput.contains(lowerSeparator)) {
            return Lists.newArrayList(input.trim());
        }
        
        int index = 0;
        List<String> result = new ArrayList<>();
        while (lowerInput.indexOf(lowerSeparator, index) > -1) {
            result.add(input.substring(index, lowerInput.indexOf(lowerSeparator, index)));
            index = lowerInput.indexOf(lowerSeparator, index) + lowerSeparator.length();
        }
        result.add(input.substring(index));
        return result.stream()
                     .map(String::trim)
                     .collect(Collectors.toList());
    }
    
    public String fill(int value, int digits) {
        return String.format("%" + digits + "s", value % Math.pow(10, digits));
    }
    
    public void checkDir(@NonNull String dir) throws Exception {
        File lDir = new File(dir);
        
        if (!lDir.exists()) {
            if (!lDir.mkdirs()) {
                throw new Exception("Can not create backup dir: " + dir);
            }
        }
        if (!lDir.isDirectory()) {
            throw new Exception("File exists with name of backup dir: " + dir);
        }
        if (!lDir.canRead()) {
            throw new Exception("Can not read backup dir: " + dir);
        }
        if (!lDir.canWrite()) {
            throw new Exception("Can not write backup dir: " + dir);
        }
    }
}
