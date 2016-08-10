package org.midnightas.mau;

import java.util.HashMap;

public class MauUserFunction extends MauFunction {

	private Mau mau;
	private String toExec;
	
	public MauUserFunction(Mau mau, String toExec) {
		this.mau = mau;
		this.toExec = toExec;
	}
	
	@Override
	public void call(HashMap<String, MauInstance> args) throws MauException {
		this.mau.function(toExec, args);
	}
	
	public Mau getMau() {
		return this.mau;
	}
	
	public String getFunctionContent() {
		return this.toExec;
	}

}
