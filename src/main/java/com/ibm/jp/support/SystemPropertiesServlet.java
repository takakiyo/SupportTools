/*
 * SystemPropertiesServlet.java
 * 
 * $Id:$
 */
package com.ibm.jp.support;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;

import javax.servlet.ServletException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @version 	1.0
 * @author
 */
public class SystemPropertiesServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = -735419902769667434L;
	
	private static final String[] commonKeys = {
		"java.version",
		"java.vendor",
		"java.vendor.url",
		"java.home",
		"java.vm.specification.version",
		"java.vm.specification.vendor",
		"java.vm.specification.name",
		"java.vm.version",
		"java.vm.vendor",
		"java.vm.name",
		"java.specification.version",
		"java.specification.vendor",
		"java.specification.name",
		"java.class.version",
		"java.class.path",
		"java.library.path",
		"java.io.tmpdir",
		"java.compiler",
		"java.ext.dirs",
		"os.name",
		"os.arch",
		"os.version",
		"file.separator",
		"path.separator",
		"line.separator",
		"user.name",
		"user.home",
		"user.dir",
	};
	private static final String[] commonKeyNames = {
		"Java Runtime Environment version",
		"Java Runtime Environment vendor",
		"Java vendor URL",
		"Java installation directory",
		"Java Virtual Machine specification version",
		"Java Virtual Machine specification vendor",
		"Java Virtual Machine specification name",
		"Java Virtual Machine implementation version",
		"Java Virtual Machine implementation vendor",
		"Java Virtual Machine implementation name",
		"Java Runtime Environment specification  version",
		"Java Runtime Environment specification  vendor",
		"Java Runtime Environment specification  name",
		"Java class format version number",
		"Java class path",
		"List of paths to search when loading libraries",
		"Default temp file path",
		"Name of JIT compiler to use",
		"Path of extension directory or directories",
		"Operating system name",
		"Operating system architecture",
		"Operating system version",
		"File separator",
		"Path separator",
		"Line separator",
		"User's account name",
		"User's home directory",
		"User's current working directory",
    };

	/**
	 * @see javax.servlet.GenericServlet#void ()
	 */
	public void init() throws ServletException {

		super.init();

	}

	/**
	* @see javax.servlet.http.HttpServlet#void (javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	*/
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
		throws ServletException, IOException {
		
		ArrayList<String> keys = new ArrayList<String>(System.getProperties().stringPropertyNames());
		
		resp.setContentType("text/html; charset=UTF-8");
		PrintWriter out = resp.getWriter();
		
		out.println("<heml><head><title>JVM System Properties</title></head>");
		out.println("<body>");
		out.println("<h1>JVM System Properties</h1>");
		
		out.println("<h2>Common Properties</h2>");
		out.println("<table border>");
		for (int i = 0; i < commonKeys.length; i++) {
			print(out, commonKeyNames[i], commonKeys[i]);
			keys.remove(commonKeys[i]);
		}
		out.println("</table>");

		out.println("<h2>Other Properties</h2>");
		out.println("<table border>");
		Collections.sort(keys);
		for (String key : keys) {
			print(out, key);
		}
		out.println("</table>");
		
		out.println("</body></html>");

	}
	
	private void print(PrintWriter out, String name, String key) {
		out.print("<tr><td nowrap>");
		out.print(name);
		out.print("</td><td nowrap>");
		out.print(key);
		out.print("</td><td>");
		out.print(System.getProperty(key));
		out.println("</td></tr>");
	}

	private void print(PrintWriter out, String key) {
		out.print("<tr><td nowrap>");
		out.print(key);
		out.print("</td><td>");
		out.print(System.getProperty(key));
		out.println("</td></tr>");
	}
}
