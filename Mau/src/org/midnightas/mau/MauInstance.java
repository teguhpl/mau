package org.midnightas.mau;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MauInstance implements VariableContainer {

	public MauModule module;
	public HashMap<String, Object> vars = new HashMap<String, Object>();
	public List<String> privateVars = new ArrayList<String>();

	public MauInstance(MauModule module) {
		this.module = module;
	}
	
	public String toString() {
		return String.format("%s:%s", getClass().getSimpleName(), vars);
	}

	@Override
	public void setVar(String name, MauInstance value, boolean isPrivate) {
		vars.remove(name);
		vars.put(name, value);
		if(isPrivate)
			privateVars.add(name);
	}

	@Override
	public Object getVar(String name, int row, int col) throws MauException {
		if(privateVars.contains(name))
			throw new MauException("Offlimits variable " + name, row, col);
		return vars.get(name);
	}

}
