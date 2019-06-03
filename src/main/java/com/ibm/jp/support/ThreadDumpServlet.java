package com.ibm.jp.support;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Map;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class for Servlet: ThreadDumpServlet
 * 
 * @web.servlet name="ThreadDumpServlet" display-name="ThreadDumpServlet"
 * 
 * @web.servlet-mapping url-pattern="/threadDump"
 * 
 */
public class ThreadDumpServlet extends javax.servlet.http.HttpServlet implements
		javax.servlet.Servlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1728169936973829245L;

	/*
	 * (non-Java-doc)
	 * 
	 * @see javax.servlet.http.HttpServlet#HttpServlet()
	 */
	public ThreadDumpServlet() {
		super();
	}

	/**
	 * @see javax.servlet.http.HttpServlet#void
	 *      (javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	public void service(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.setContentType("text/html; charset=UTF-8");
		PrintWriter out = resp.getWriter();
		out.println("<HTML>");
		out.println("<HEAD><TITLE>ThreadDumpServlet</TITLE></HEAD>");
		out.println("<BODY>");
		out.println("<H1>ThreadDumpServlet</H1>");

		long time = System.currentTimeMillis();
		Map<Thread, StackTraceElement[]> traces = Thread.getAllStackTraces();
		Vector<Thread> threads = new Vector<Thread>(traces.keySet());
		Collections.sort(threads, new Comparator<Thread>() {
			public int compare(Thread t1, Thread t2) {
				int n = t1.getThreadGroup().getName().compareTo(t2.getThreadGroup().getName());
				return (n != 0)? n : t1.getName().compareTo(t2.getName());
			}
		});

		out.println("<P>Current Time: " + (new Date(time)) + "</P>");
		String groupName = "";
		for (Thread t : threads) {
			if (!t.getThreadGroup().getName().equals(groupName)) {
				groupName = t.getThreadGroup().getName();
				out.println("<H2>Thread Group: " + groupName + "</H2>");
			}
			out.print("<P><B>\"" + t.getName() + 
					"\"</B> ID:0x" + Long.toHexString(t.getId()) +
					" Priority:" + t.getPriority() +
					" Daemon:" + t.isDaemon() +
					" Status:" + t.getState());
			StackTraceElement[] s = traces.get(t);
			out.print("</P><PRE>");
			for (int j = 0; j < s.length; j++) {
				out.print("    " + s[j].getClassName() + "#" + s[j].getMethodName());
				if (s[j].getFileName() == null) {
					out.println("(Unknown Source)");
				} else {
					out.print("(" + s[j].getFileName());
					int ln = s[j].getLineNumber();
					if (ln >= 0) {
						out.print(":" + ln);
					} else if (ln == -2) {
						out.print("(Native code)");
					}
					out.println(")");
				}
			}
			out.println("</PRE>");
		}
		out.println("</BODY></HTML>");

	}
}