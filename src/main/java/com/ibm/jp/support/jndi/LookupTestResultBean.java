package com.ibm.jp.support.jndi;

/**
 * @version $Id: LookupTestResultBean.java,v 1.1 2004/10/25 06:37:55 takakiyo Exp $
 * @author T.Takakiyo
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class LookupTestResultBean {
	private String providerUrl = "";
	private String initialContextFactory = "";
	private String altKey1 = "";
	private String altValue1 = "";
	private String altKey2 = "";
	private String altValue2 = "";
	private String jndiName = "";
	private String result = null;
	private String root = "";
	
	static private boolean WAS_TRADITIONAL;
	static {
		try {
			Class.forName("com.ibm.websphere.naming.WsnInitialContextFactory");
			WAS_TRADITIONAL = true;
		} catch (ClassNotFoundException e) {
			WAS_TRADITIONAL = false;
		}
	}
		
	public LookupTestResultBean() {
		if (WAS_TRADITIONAL) {
			providerUrl ="iiop:///";
			initialContextFactory = "com.ibm.websphere.naming.WsnInitialContextFactory";
		}
	}

	/**
	 * Returns the altKey1.
	 * @return String
	 */
	public String getAltKey1() {
		return altKey1;
	}

	/**
	 * Returns the altKey2.
	 * @return String
	 */
	public String getAltKey2() {
		return altKey2;
	}

	/**
	 * Returns the altValue1.
	 * @return String
	 */
	public String getAltValue1() {
		return altValue1;
	}

	/**
	 * Returns the altValue2.
	 * @return String
	 */
	public String getAltValue2() {
		return altValue2;
	}

	/**
	 * Returns the initialContextFactory.
	 * @return String
	 */
	public String getInitialContextFactory() {
		return initialContextFactory;
	}

	/**
	 * Returns the providerUrl.
	 * @return String
	 */
	public String getProviderUrl() {
		return providerUrl;
	}
	
	public String getJndiName() {
		return jndiName;
	}
	
	public String getResult() {
		return result;
	}

	/**
	 * Sets the altKey1.
	 * @param altKey1 The altKey1 to set
	 */
	void setAltKey1(String altKey1) {
		this.altKey1 = altKey1;
	}

	/**
	 * Sets the altKey2.
	 * @param altKey2 The altKey2 to set
	 */
	void setAltKey2(String altKey2) {
		this.altKey2 = altKey2;
	}

	/**
	 * Sets the altValue1.
	 * @param altValue1 The altValue1 to set
	 */
	void setAltValue1(String altValue1) {
		this.altValue1 = altValue1;
	}

	/**
	 * Sets the altValue2.
	 * @param altValue2 The altValue2 to set
	 */
	void setAltValue2(String altValue2) {
		this.altValue2 = altValue2;
	}

	/**
	 * Sets the initialContextFactory.
	 * @param initialContextFactory The initialContextFactory to set
	 */
	void setInitialContextFactory(String initialContextFactory) {
		this.initialContextFactory = initialContextFactory;
	}

	/**
	 * Sets the providerUrl.
	 * @param providerUrl The providerUrl to set
	 */
	void setProviderUrl(String providerUrl) {
		this.providerUrl = providerUrl;
	}
	
	void setJndiName(String name) {
		jndiName = name;
	}
	
	void setResult(String res) {
		result = res;
	}

	public String getRoot() {
		return root;
	}

	public void setRoot(String root) {
		this.root = root;
	}

}
