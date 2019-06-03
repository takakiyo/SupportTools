/*
 * Env.java
 * 
 * $Id: $
 */
package com.ibm.jp.util;

public class Env {
	private static boolean isTiger;
	static {
		isTiger = (Double.parseDouble(System.getProperty("java.specification.version")) >= 1.5);
	}
	
	public static boolean isTiger() {
		return isTiger;
	}

}
