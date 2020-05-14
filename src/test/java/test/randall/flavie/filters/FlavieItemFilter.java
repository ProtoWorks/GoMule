/*
 * Created on 01.09.2004
 */
package test.randall.flavie.filters;

import test.randall.flavie.*;

/**
 * @author mbr
 */
public interface FlavieItemFilter
{
	public void initialize() throws Exception;
	public void finish() throws Exception;
	public boolean check(D2ItemInterface pItemFound);
	public boolean check(String pFingerprint, String pItemname);
}
