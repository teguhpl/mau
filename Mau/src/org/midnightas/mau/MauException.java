package org.midnightas.mau;

public class MauException extends Exception {
	
	private static final long serialVersionUID = 1L;
	
	public int row, col;
	
	public MauException(String s, int row, int col) {
		super(s);
		this.row = row;
		this.col = col;
	}
	
}
