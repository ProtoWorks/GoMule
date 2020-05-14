/*
 * Created on 01.09.2004
 */
package randall.flavie.filters;

import randall.flavie.D2ItemInterface;

/**
 * @author mbr
 */
public interface FlavieItemFilter {
    
    void initialize() throws Exception;
    
    // TODO closeable?
    void finish() throws Exception;
    
    boolean check(D2ItemInterface itemFound);
}
