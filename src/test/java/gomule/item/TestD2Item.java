package gomule.item;

import java.util.Comparator;

import com.google.common.collect.ComparisonChain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TestD2Item {
    
    @Test
    void test() {
        Assertions.assertEquals(-1, compare(null, ""));
        Assertions.assertEquals(1, compare("", null));
        Assertions.assertEquals(0, compare(null, null));
        Assertions.assertEquals(0, compare("", ""));
    }
    
    int compare(String s1, String s2) {
        return ComparisonChain.start()
                              .compare(s1, s2, Comparator.nullsFirst(Comparator.naturalOrder()))
                              .result();
    }
    
}