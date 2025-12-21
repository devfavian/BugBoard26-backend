package it.unina.bugboard.utils;

public class StringManager {
	
	private StringManager() {}

	public static String[] getFields(String string) {
	    return string.split(",");
	}
	
	public static String getElement(String string, int element) {
	    return getFields(string)[element];
	}
}
