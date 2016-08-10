package org.midnightas.mau;

public interface VariableContainer {
	
	public void setVar(String name, MauInstance value, boolean isPrivate);
	public Object getVar(String name, int row, int col) throws MauException;
	
}
