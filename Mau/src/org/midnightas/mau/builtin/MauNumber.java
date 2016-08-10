package org.midnightas.mau.builtin;

import org.midnightas.mau.MauInstance;
import org.midnightas.mau.MauModule;

public class MauNumber extends MauModule {
	
	public MauNumber() {
	}
	
	public void constructor(MauInstance mau, Object... args) {
		mau.vars.put("value", (Double) args[0]);
	}
	
}
