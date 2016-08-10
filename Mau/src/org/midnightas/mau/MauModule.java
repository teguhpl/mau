package org.midnightas.mau;

import java.util.HashMap;

public class MauModule {
	public HashMap<String, MauInstance> classVars = new HashMap<String, MauInstance>();
	public HashMap<String, MauModule> vars = new HashMap<String, MauModule>();
	
	public MauInstance newInstance(Object... args) {
		MauInstance mauInstance = new MauInstance(this);
		constructor(mauInstance, args);
		return mauInstance;
	}
	
	public void constructor(MauInstance mau, Object... args) {};
	
}
