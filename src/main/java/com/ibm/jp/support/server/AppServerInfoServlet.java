/*
 * AppServerInfoServlet.java
 */
package com.ibm.jp.support.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ibm.jp.util.Html;

/**
 * アプリケーションサーバー情報表示サーブレット。
 * デプロイ先のアプリケーションサーバー種別（Liberty / WAS / Tomcat /
 * GlassFish / WildFly など）を判別し、関連情報を出力する。
 */
public class AppServerInfoServlet extends HttpServlet {

	private static final long serialVersionUID = 3921748056431970L;

	@Override
	public void service(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		resp.setContentType("text/html; charset=UTF-8");
		PrintWriter out = resp.getWriter();

		out.println("<!DOCTYPE html>");
		out.println("<html><head><meta charset=\"UTF-8\">");
		out.println("<title>Application Server Information</title></head>");
		out.println("<body>");
		out.println("<h1>Application Server Information</h1>");

		// --- サーバー種別の判別 ---
		ServerInfo info = detectServer(getServletContext());

		out.println("<h2>Detected Server</h2>");
		out.println("<table border=\"1\" cellpadding=\"4\" cellspacing=\"0\">");
		out.println("<tr><th align=\"left\">Item</th><th align=\"left\">Value</th></tr>");
		printRow(out, "Server Type",    info.serverType);
		printRow(out, "Server Version", info.serverVersion);
		printRow(out, "Detection Basis",info.detectionBasis);
		out.println("</table>");

		// --- ServletContext 情報 ---
		out.println("<h2>Servlet Container (ServletContext)</h2>");
		out.println("<table border=\"1\" cellpadding=\"4\" cellspacing=\"0\">");
		out.println("<tr><th align=\"left\">Item</th><th align=\"left\">Value</th></tr>");
		ServletContext ctx = getServletContext();
		printRow(out, "Server Info",           ctx.getServerInfo());
		printRow(out, "Servlet API Version",
				ctx.getMajorVersion() + "." + ctx.getMinorVersion());
		printRow(out, "Context Path",          ctx.getContextPath());
		out.println("</table>");

		// --- JVM Runtime 情報 ---
		out.println("<h2>JVM Runtime</h2>");
		out.println("<table border=\"1\" cellpadding=\"4\" cellspacing=\"0\">");
		out.println("<tr><th align=\"left\">Item</th><th align=\"left\">Value</th></tr>");
		RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
		printRow(out, "JVM Name",           runtime.getVmName());
		printRow(out, "JVM Vendor",         runtime.getVmVendor());
		printRow(out, "JVM Version",        runtime.getVmVersion());
		printRow(out, "Spec Version",       runtime.getSpecVersion());
		printRow(out, "Uptime (ms)",        String.valueOf(runtime.getUptime()));
		out.println("</table>");

		// --- サーバー固有の追加情報（JMX MBean 経由）---
		Map<String, String> extraProps = collectExtraProperties(info.serverType);
		if (!extraProps.isEmpty()) {
			out.println("<h2>Server-specific Properties</h2>");
			out.println("<table border=\"1\" cellpadding=\"4\" cellspacing=\"0\">");
			out.println("<tr><th align=\"left\">Key</th><th align=\"left\">Value</th></tr>");
			for (Map.Entry<String, String> e : extraProps.entrySet()) {
				printRow(out, e.getKey(), e.getValue());
			}
			out.println("</table>");
		}

		// --- System Properties（サーバー関連のみ）---
		out.println("<h2>Relevant System Properties</h2>");
		out.println("<table border=\"1\" cellpadding=\"4\" cellspacing=\"0\">");
		out.println("<tr><th align=\"left\">Property</th><th align=\"left\">Value</th></tr>");
		String[] relevantProps = {
			"was.install.root",
			"server.root",
			"wlp.install.dir",
			"wlp.server.name",
			"wlp.user.dir",
			"catalina.home",
			"catalina.base",
			"com.sun.aas.instanceRoot",
			"com.sun.aas.installRoot",
			"com.sun.aas.domainRoot",
			"jboss.home.dir",
			"jboss.server.name",
			"jboss.server.base.dir",
			"weblogic.home",
			"weblogic.Name",
			"jetty.home",
			"jetty.base",
		};
		boolean anyPrinted = false;
		for (String key : relevantProps) {
			String val = System.getProperty(key);
			if (val != null) {
				printRow(out, key, val);
				anyPrinted = true;
			}
		}
		if (!anyPrinted) {
			out.println("<tr><td colspan=\"2\">(none found)</td></tr>");
		}
		out.println("</table>");

		out.println("</body></html>");
	}

	// -------------------------------------------------------------------------
	// サーバー判別ロジック
	// -------------------------------------------------------------------------

	private static final class ServerInfo {
		String serverType;
		String serverVersion;
		String detectionBasis;

		ServerInfo(String type, String version, String basis) {
			this.serverType    = type;
			this.serverVersion = version;
			this.detectionBasis = basis;
		}
	}

	/**
	 * 複数の手がかりを組み合わせてサーバーを判別する。
	 * 判別優先順位：
	 *   1. System property（サーバー固有キー）
	 *   2. ServletContext#getServerInfo()
	 *   3. JMX MBean の存在確認
	 *   4. クラスパス上の固有クラス
	 */
	private ServerInfo detectServer(ServletContext ctx) {
		// -- 1. System property による判別 --
		if (System.getProperty("wlp.install.dir") != null
				|| System.getProperty("wlp.server.name") != null) {
			String ver = System.getProperty("wlp.server.name", "(unknown)");
			return new ServerInfo("IBM Open Liberty / WebSphere Liberty", ver,
					"System property: wlp.install.dir / wlp.server.name");
		}
		if (System.getProperty("was.install.root") != null) {
			return new ServerInfo("IBM WebSphere Application Server (Traditional)",
					System.getProperty("was.install.root"),
					"System property: was.install.root");
		}
		if (System.getProperty("catalina.home") != null) {
			return new ServerInfo("Apache Tomcat",
					System.getProperty("catalina.home"),
					"System property: catalina.home");
		}
		if (System.getProperty("com.sun.aas.instanceRoot") != null) {
			return new ServerInfo("Eclipse GlassFish / Oracle GlassFish",
					System.getProperty("com.sun.aas.instanceRoot"),
					"System property: com.sun.aas.instanceRoot");
		}
		if (System.getProperty("jboss.home.dir") != null) {
			return new ServerInfo("Red Hat JBoss EAP / WildFly",
					System.getProperty("jboss.home.dir"),
					"System property: jboss.home.dir");
		}
		if (System.getProperty("weblogic.home") != null
				|| System.getProperty("weblogic.Name") != null) {
			String ver = System.getProperty("weblogic.Name",
					System.getProperty("weblogic.home", "(unknown)"));
			return new ServerInfo("Oracle WebLogic Server", ver,
					"System property: weblogic.home / weblogic.Name");
		}
		if (System.getProperty("jetty.home") != null) {
			return new ServerInfo("Eclipse Jetty",
					System.getProperty("jetty.home"),
					"System property: jetty.home");
		}

		// -- 2. ServletContext#getServerInfo() による判別 --
		String serverInfo = ctx.getServerInfo();
		if (serverInfo != null) {
			String si = serverInfo.toLowerCase();
			if (si.contains("liberty")) {
				return new ServerInfo("IBM Open Liberty / WebSphere Liberty",
						serverInfo, "ServletContext.getServerInfo()");
			}
			if (si.contains("websphere")) {
				return new ServerInfo("IBM WebSphere Application Server",
						serverInfo, "ServletContext.getServerInfo()");
			}
			if (si.contains("apache tomcat") || si.contains("tomcat")) {
				return new ServerInfo("Apache Tomcat",
						serverInfo, "ServletContext.getServerInfo()");
			}
			if (si.contains("glassfish") || si.contains("payara")) {
				return new ServerInfo("GlassFish / Payara",
						serverInfo, "ServletContext.getServerInfo()");
			}
			if (si.contains("wildfly") || si.contains("jboss")) {
				return new ServerInfo("Red Hat JBoss EAP / WildFly",
						serverInfo, "ServletContext.getServerInfo()");
			}
			if (si.contains("weblogic")) {
				return new ServerInfo("Oracle WebLogic Server",
						serverInfo, "ServletContext.getServerInfo()");
			}
			if (si.contains("jetty")) {
				return new ServerInfo("Eclipse Jetty",
						serverInfo, "ServletContext.getServerInfo()");
			}
			if (si.contains("undertow")) {
				return new ServerInfo("Undertow (JBoss/WildFly embedded)",
						serverInfo, "ServletContext.getServerInfo()");
			}
		}

		// -- 3. JMX MBean による判別 --
		try {
			MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
			Set<ObjectName> objs;

			objs = mbs.queryNames(new ObjectName("WebSphere:*"), null);
			if (!objs.isEmpty()) {
				return new ServerInfo("IBM WebSphere Application Server",
						"(detected via JMX)", "JMX MBean domain: WebSphere");
			}
			objs = mbs.queryNames(new ObjectName("Catalina:*"), null);
			if (!objs.isEmpty()) {
				return new ServerInfo("Apache Tomcat",
						"(detected via JMX)", "JMX MBean domain: Catalina");
			}
			objs = mbs.queryNames(new ObjectName("amx:*"), null);
			if (!objs.isEmpty()) {
				return new ServerInfo("Eclipse GlassFish / Oracle GlassFish",
						"(detected via JMX)", "JMX MBean domain: amx");
			}
			objs = mbs.queryNames(new ObjectName("jboss.as:*"), null);
			if (!objs.isEmpty()) {
				return new ServerInfo("Red Hat JBoss EAP / WildFly",
						"(detected via JMX)", "JMX MBean domain: jboss.as");
			}
			objs = mbs.queryNames(new ObjectName("com.bea:*"), null);
			if (!objs.isEmpty()) {
				return new ServerInfo("Oracle WebLogic Server",
						"(detected via JMX)", "JMX MBean domain: com.bea");
			}
		} catch (Exception e) {
			// JMX が利用できない環境では無視
		}

		// -- 4. 固有クラスの存在確認 --
		if (isClassPresent("com.ibm.ws.kernel.boot.Launcher")) {
			return new ServerInfo("IBM Open Liberty / WebSphere Liberty",
					"(class detected)", "Class: com.ibm.ws.kernel.boot.Launcher");
		}
		if (isClassPresent("com.ibm.websphere.runtime.ServerInformation")) {
			return new ServerInfo("IBM WebSphere Application Server (Traditional)",
					"(class detected)", "Class: com.ibm.websphere.runtime.ServerInformation");
		}
		if (isClassPresent("org.apache.catalina.startup.Catalina")) {
			return new ServerInfo("Apache Tomcat",
					"(class detected)", "Class: org.apache.catalina.startup.Catalina");
		}
		if (isClassPresent("com.sun.enterprise.glassfish.bootstrap.ASMain")) {
			return new ServerInfo("Eclipse GlassFish / Oracle GlassFish",
					"(class detected)", "Class: com.sun.enterprise.glassfish.bootstrap.ASMain");
		}
		if (isClassPresent("org.jboss.as.server.Main")) {
			return new ServerInfo("Red Hat JBoss EAP / WildFly",
					"(class detected)", "Class: org.jboss.as.server.Main");
		}
		if (isClassPresent("weblogic.server.ServerLifecycleException")) {
			return new ServerInfo("Oracle WebLogic Server",
					"(class detected)", "Class: weblogic.server.ServerLifecycleException");
		}
		if (isClassPresent("org.eclipse.jetty.server.Server")) {
			return new ServerInfo("Eclipse Jetty",
					"(class detected)", "Class: org.eclipse.jetty.server.Server");
		}

		return new ServerInfo("Unknown",
				serverInfo != null ? serverInfo : "(none)",
				"ServletContext.getServerInfo() (no match)");
	}

	/**
	 * JMX MBean からサーバー固有の追加情報を収集する。
	 */
	private Map<String, String> collectExtraProperties(String serverType) {
		Map<String, String> props = new LinkedHashMap<String, String>();
		try {
			MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
			if (serverType.contains("Liberty") || serverType.contains("WebSphere")) {
				// Liberty: com.ibm.websphere.runtime:type=Server
				Set<ObjectName> objs = mbs.queryNames(
						new ObjectName("WebSphere:type=Server,*"), null);
				for (ObjectName on : objs) {
					for (String attr : new String[]{"name", "platformVersion", "serverVersion"}) {
						try {
							Object val = mbs.getAttribute(on, attr);
							if (val != null) {
								props.put(on.getKeyProperty("type") + "/" + attr,
										val.toString());
							}
						} catch (Exception ignore) { /* 属性が存在しない場合は無視 */ }
					}
					break; // 最初の1件のみ
				}
			}
		} catch (Exception ignore) {
			// JMX 非対応環境では空のままにする
		}
		return props;
	}

	private boolean isClassPresent(String className) {
		try {
			Class.forName(className);
			return true;
		} catch (ClassNotFoundException e) {
			return false;
		}
	}

	private void printRow(PrintWriter out, String key, String value) {
		out.print("<tr><td nowrap><b>");
		out.print(Html.escapeChar(key));
		out.print("</b></td><td>");
		out.print(value != null ? Html.escapeChar(value) : "<i>(null)</i>");
		out.println("</td></tr>");
	}
}
