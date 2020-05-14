/*
 * Created on 01.09.2004
 */
package randall.flavie.filters;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import randall.flavie.D2ItemInterface;
import randall.flavie.Flavie;
import randall.util.RandallUtil;

/**
 * @author mbr
 */
@Slf4j
public class FlavieDupeFilter implements FlavieItemFilter {
    
    private PrintStream dupeOut = null;
    @Getter private final Map<String, String> dupeList = new HashMap<>();
    @Getter private int dupeCount = -1;
    
    public FlavieDupeFilter(String dupeName, Reader dupeFile) throws Exception {
        try (BufferedReader in = new BufferedReader(dupeFile)) {
            in.lines()
              .map(Strings::nullToEmpty)
              .map(String::trim)
              // skip empty lines
              .filter(line -> !Strings.isNullOrEmpty(line))
              .forEach(line -> {
                  // Find fingerprint in this line
                  RandallUtil.split(line, " ", false)
                             .stream()
                             .filter(s -> s.startsWith("0x"))
                             .forEach(s -> dupeList.put(s, line));
              });
        }
        log.debug("Dupelist {} contains {} items.", dupeName, dupeList.size());
    }
    
    @Override
    public void initialize() throws Exception {
        Preconditions.checkState(dupeOut == null, "Dupe file already initialised!");
        dupeOut = new PrintStream(new FileOutputStream(Flavie.sMatchedDir + "matched.dupe.txt"));
        dupeCount = 0;
    }
    
    @Override
    public void finish() throws Exception {
        Preconditions.checkState(dupeOut != null, "Dupe file not initialised!");
        dupeOut.close();
        dupeCount = -1;
    }
    
    @Override
    public boolean check(D2ItemInterface itemFound) {
        if (dupeList.containsKey(itemFound.getFingerprint())) {
            dupeOut.println("Item " + itemFound.getFingerprint() + "/" + itemFound.getName() + " from file " + itemFound.getFileName() + " is listed as a dupe");
            dupeCount++;
            return false;
        }
        return true;
    }
}
