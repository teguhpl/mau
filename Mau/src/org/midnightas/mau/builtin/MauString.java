package org.midnightas.mau.builtin;

import org.midnightas.mau.MauInstance;
import org.midnightas.mau.MauModule;

public class MauString extends MauModule {
	
	public MauString() {
	}
	
	public void constructor(MauInstance mau, Object... args) {
		mau.vars.put("value", (String) args[0]);
	}
	
}
