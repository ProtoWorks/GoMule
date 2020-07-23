/*
 * Created on 01.09.2004
 */
package randall.flavie.filters;

import randall.flavie.D2ItemInterface;

/**
 * @author mbr
 */
public interface FlavieItemFilter {
    
    int getDupeCount();
    
    void initialize();
    
    // TODO closeable?
    void finish();
    
    boolean check(D2ItemInterface itemFound);
}
