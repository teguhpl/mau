package org.midnightas.mau;

import java.util.HashMap;

public abstract class MauFunction extends MauInstance {
	
	public HashMap<String, MauModule> args = new HashMap<String, MauModule>();
	public abstract void call(HashMap<String, MauInstance> args) throws MauException;
	
	public MauFunction() {
		super(Mau.modules.get("function"));
	}

}
