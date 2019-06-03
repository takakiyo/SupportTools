// Decompiled by Jad v1.5.8d. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   CreateJavaDumpServlet.java

package com.ibm.jp.support.ibmjdk;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import javax.servlet.ServletException;
import javax.servlet.http.*;

public class CreateJavaDumpServlet extends HttpServlet {

	public CreateJavaDumpServlet() {
	}

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException
	{
		outputHtml(
			resp,
			"<form action=\"" + req.getRequestURI() + "\" method=\"POST\">"
				+ "<input id=\"java\" type=\"radio\" name=\"dump\" value=\"java\" checked>"
				+ "<label for=\"java\">Java Dump</label>"
				+ "<input id=\"heap\" type=\"radio\" name=\"dump\" value=\"heap\">"
				+ "<label for=\"java\">Heap Dump</label><br>"
				+ "<input type=\"submit\" value=\"Do Dump\">"
				+ "</form>");
	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException 
	{
		String s = req.getParameter("dump");
		try {
			Class<?> clazz = Class.forName("com.ibm.jvm.Dump");
			if (s != null && s.equals("heap")) {
				Method dump = clazz.getMethod("HeapDump", (Class<?>[])null);
				dump.invoke((Object)null, (Object[])null);
				outputHtml(resp, "HeapDump output Done.");
			} else {
				Method dump = clazz.getMethod("JavaDump", (Class<?>[])null);
				dump.invoke((Object)null, (Object[])null);
				outputHtml(resp, "JavaDump output Done.");
			}
		} catch (Exception e) {
			outputHtml(resp, "Exception: " + e.toString() + "<br />"
					+ "It does not seem IBM JDK.<br />");
		}
	}

	private void outputHtml(HttpServletResponse resp, String s)
			throws IOException
	{
		resp.setContentType("text/html; charset=UTF-8");
		PrintWriter out = resp.getWriter();
		out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
		out.println("<html>");
		out.println("<head>");
		out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">");
		out.println("<title>Create JavaDump</title>");
		out.println("</head>");
		out.println("<body>");
		out.println("<h1>Create JavaDump/HeapDump</h1>");
		out.print(s);
		out.println("<h2>JDK/JRE Information</h2>");
		out.print("java.vendor = ");
		out.print(System.getProperty("java.vendor"));
		out.println("<br />");
		out.print("java.version = ");
		out.print(System.getProperty("java.version"));
		out.println("<br />");
		out.print("java.vm.name = ");
		out.print(System.getProperty("java.vm.name"));
		out.println("<br />");
		out.println("</body></html>");
	}

	private static final long serialVersionUID = 0xf056ba26cecf85d2L;
}
