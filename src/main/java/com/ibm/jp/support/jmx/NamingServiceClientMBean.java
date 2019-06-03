package com.ibm.jp.support.jmx;

public interface NamingServiceClientMBean {
	public String lookup(String url, String jndiName);
}
