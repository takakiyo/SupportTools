package com.ibm.jp.support.jmx;

import java.io.Serializable;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class NamingServiceClient implements NamingServiceClientMBean, Serializable {
	private static final long serialVersionUID = -3471186907869296587L;

	public NamingServiceClient() {
	}

	public String lookup(String url, String jndiName) {
		try {
			Properties prop = new Properties();
			prop.put(Context.PROVIDER_URL, url);
			prop.put(Context.INITIAL_CONTEXT_FACTORY, "com.ibm.websphere.naming.WsnInitialContextFactory");
			Context context = new InitialContext(prop);
			Object ret = context.lookup(jndiName);
			return "Success: " + ret.getClass().getCanonicalName();
		} catch (NamingException ex) {
			ex.printStackTrace();
			return "Exception: " + ex.getClass().getName() + ": " + ex.getMessage();
		}
	}
}
