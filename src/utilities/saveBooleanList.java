package utilities;

import java.util.ArrayList;

public class saveBooleanList {
	public ArrayList<String> names;
	public ArrayList<Boolean> bools;
	
	public saveBooleanList() {
		names = new ArrayList<String>();
		bools = new ArrayList<Boolean>();
	}
	
	public void add(String name, Boolean b) {
		names.add(name);
		bools.add(b);
	}
	
	public void set(String newName, boolean newBool) {
		if(!names.contains(newName)) {
			add(newName,newBool);
		}
		else {
			for(int i = 0; i < names.size(); i++) {
				if(names.get(i).equals(newName)) {
					bools.set(i,newBool);
				}
			}
		}
	}
	
	public boolean getBool(int i) {
		return bools.get(i);
	}
	
	public String getName(int i) {
		return names.get(i);
	}
	
	public boolean get(String name) {
		for(int i = 0; i < names.size(); i++) {
			if(names.get(i).equals(name)) return bools.get(i);
		}
		return false;
	}
	
	public void remove(String name) {
		for(int i = 0; i < names.size(); i++) {
			if(names.get(i).equals(name)) {
				bools.remove(i);
				names.remove(i);
				break;
			}
		}
	}
	
	public int size() {
		return bools.size();
	}
}