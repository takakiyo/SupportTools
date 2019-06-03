package com.ibm.jp.support.jmx;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.management.MBeanInfo;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class for Servlet: MBeanServerInfoServlet
 *
 */
 public class MBeanServerInfoServlet extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {
    /**
	 * 
	 */
	private static final long serialVersionUID = -7260080352331186207L;
	
	/* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#HttpServlet()
	 */
	public MBeanServerInfoServlet() {
		super();
	}   	
	
	/* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		perform(request, response);
	}  	
	
	/* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		perform(request, response);
	}   	 
	
	private void perform(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html; charset=UTF-8");
		PrintWriter out = response.getWriter();
		out.print(
				"<HTML>\n" +
				"<HEAD>\n" +
				"<TITLE>List MBean Server</TITLE>\n" +
				"</HEAD>\n" +
				"<BODY>\n" +
				"<H1>List MBean Server</H1>\n"
			);
		
		try {
			out.print("<H2>MBean Server List</H2>\n" + "<UL>\n");
			List<MBeanServer> servers = MBeanServerFactory.findMBeanServer(null);
			Iterator<MBeanServer> it = servers.iterator();
			while (it.hasNext()) {
				MBeanServer server = it.next();
				out.print("<LI><B>Default Domain: " + server.getDefaultDomain() + "</B>");
				out.print("<DL>\n");
				out.print("<DT>Implement Class<DD>" + server.getClass().getCanonicalName());
				out.print("<DT>Domains<DD>");
				String[] domains = server.getDomains();
				for (int i = 0; i < domains.length; i++) {
					if (i > 0) out.print(", ");
					out.print(domains[i]);
				}
				out.print("<DT>MBean Count<DD>" + server.getMBeanCount());
				out.print("\n</DL></LI>\n");
			}
			out.print("</UL>\n");
			
			it = servers.iterator();
			while (it.hasNext()) {
				MBeanServer server = it.next();
				out.print("<H2>MBeanServer (Default Domain:" + server.getDefaultDomain() + ")</H2>\n");
				Set<ObjectName> names = server.queryNames(null, null);
				Iterator<ObjectName> it2 = names.iterator();
				out.print("<OL>\n");
				while (it2.hasNext()) {
					ObjectName name = it2.next();
					MBeanInfo info = server.getMBeanInfo(name);
					out.println("<LI>name: <B>" + name.getKeyProperty("name") + "</B>");
					out.println("<DL>");
					out.print("<DT>CanonicalName</DT><DD>" + name.getCanonicalName() + "</DD>");
					out.print("<DT>Implement Class</DT><DD>" + info.getClassName() + "</DD>");
					if (info.getDescription() != null) {
						out.print("<DT>Description</DT><DD>" + info.getDescription() + "</DD>");
					}
//					MBeanAttributeInfo[] attrs = info.getAttributes();
//					if (attrs.length > 0) {
//						out.print("<DT>Attributes</DT>");
//						for (int i = 0; i < attrs.length; i++) {
//							out.print("<DD>" + attrs[i].getName() + " (" + attrs[i].getDescription() + ")</DD>");
//						}
//					}
					out.print("\n</DL></LI>\n");
				}
				out.print("</OL>\n");
			}
		} catch (Exception e) {
			out.print("<PRE>");
			e.printStackTrace(out);
			out.print("</PRE>\n");
		}
		out.print("</BODY></HTML>\n");
	}
	
//	private static final String name = "WebSphere:name=namingserviceclient";
//	public void init() throws ServletException {
//		try {
//			List servers = MBeanServerFactory.findMBeanServer(null);
//			MBeanServer server = (MBeanServer)servers.get(0);
//			ObjectName oname = new ObjectName(name);
//			NamingServiceClientMBean test = new NamingServiceClient();
//			server.registerMBean(test, oname);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//	public void destroy() {
//		try {
//			List servers = MBeanServerFactory.findMBeanServer(null);
//			MBeanServer server = (MBeanServer)servers.get(0);
//			ObjectName oname = new ObjectName(name);
//			server.unregisterMBean(oname);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
}