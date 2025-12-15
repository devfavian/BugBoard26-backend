package it.unina.bugboard.utils;

public class StringManager {

	public static String[] getFields(String string) {
	    String[] p = string.split(",");
	    return p;
	}
	
	public static String getElement(String string, int element) {
	    return getFields(string)[element];
	}
}
