package com.ibm.jp.support.http;

/*
 * @(#)SnoopServlet.java        1.00 99/03/15
 * 
 * Copyright (c) 1999, 2005 International Business Machines. All Rights Reserved.
 * 
 * This software is the confidential and proprietary information of IBM.
 * You shall not disclose such Confidential Information and shall use it
 * only in accordance with the terms of the license agreement you entered
 * into with IBM.
 * 
 * IBM MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. IBM SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 * 
 * CopyrightVersion 1.0
 *
 *
 */

import java.io.IOException;
import java.io.PrintWriter;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Enumeration;
import java.util.Locale;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpUtils;

import com.ibm.jp.util.Html;

/**
 * Snoop Servlet returns information about the request. This servlet is useful
 * for checking the request parameters from a particular client. SnoopServlet
 * also returns information of existing sessions, application attributes, and
 * request attributes.
 * 
 * @version 1.0
 */
public class SnoopServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4821886583871053600L;

	public void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		PrintWriter out;
		HttpSession session = req.getSession(true);

		res.setContentType("text/html");
		out = res.getWriter();

		out.println("<HTML><HEAD><TITLE>Snoop Servlet</TITLE></HEAD><BODY BGCOLOR=\"#FFFFEE\">");
		out.println("<h1>Snoop Servlet - Request/Client Information</h1>");
		out.println("<h2>Requested URL:</h2>");
		out.println("<TABLE Border=\"2\" WIDTH=\"65%\" BGCOLOR=\"#DDDDFF\">");
		out.println("<tr><td>"
				+ escapeChar(HttpUtils.getRequestURL(req).toString())
				+ "</td></tr></table><BR><BR>");

		out.println("<h2>Servlet Name:</h2>");
		out.println("<table border=\"2\" WIDTH=\"65%\" BGCOLOR=\"#DDDDFF\">");
		out.println("<tr><td>"
				+ escapeChar(getServletConfig().getServletName())
				+ "</td></tr></table><BR><BR>");

		Enumeration<String> vEnum = getServletConfig().getInitParameterNames();
		if (vEnum != null && vEnum.hasMoreElements()) {
			boolean first = true;
			while (vEnum.hasMoreElements()) {
				if (first) {
					out.println("<h2>Servlet Initialization Parameters</h2>");
					out.println("<TABLE Border=\"2\" WIDTH=\"65%\" BGCOLOR=\"#DDDDFF\">");
					first = false;
				}
				String param = vEnum.nextElement();
				out.println("<tr><td>" + escapeChar(param) + "</td><td>"
						+ escapeChar(getInitParameter(param)) + "</td></tr>");
			}
			out.println("</table><BR><BR>");
		}
		
		ServletContext context = getServletConfig().getServletContext();
		vEnum = context.getInitParameterNames();
		if (vEnum != null && vEnum.hasMoreElements()) {
			boolean first = true;
			while (vEnum.hasMoreElements()) {
				if (first) {
					out.println("<h2>Servlet Context Initialization Parameters</h2>");
					out.println("<TABLE Border=\"2\" WIDTH=\"65%\" BGCOLOR=\"#DDDDFF\">");
					first = false;
				}
				String param = vEnum.nextElement();
				out.println("<tr><td>"
						+ escapeChar(param)
						+ "</td><td>"
						+ escapeChar(getServletConfig().getServletContext()
								.getInitParameter(param)) + "</td></tr>");
			}
			out.println("</table><BR><BR>");
		}

		out.println("<h2>Request Information:</h2>");
		out.println("<table border=\"2\" WIDTH=\"65%\" BGCOLOR=\"#DDDDFF\">");
		print(out, "Request method", req.getMethod());
		print(out, "Request URI", req.getRequestURI());
		print(out, "Request protocol", req.getProtocol());
		print(out, "Servlet path", req.getServletPath());
		print(out, "Path info", req.getPathInfo());
		print(out, "Path translated", req.getPathTranslated());
		print(out, "Character encoding", req.getCharacterEncoding());
		print(out, "Query string", req.getQueryString());
		print(out, "Content length", req.getContentLength());
		print(out, "Content type", req.getContentType());
		print(out, "Server name", req.getServerName());
		print(out, "Server port", req.getServerPort());
		print(out, "Remote user", req.getRemoteUser());
		print(out, "Remote address", req.getRemoteAddr());
		print(out, "Remote host", req.getRemoteHost());
		try {
			print(out, "Remote port", req.getRemotePort());
			print(out, "Local address", req.getLocalAddr());
			print(out, "Local host", req.getLocalName());
			print(out, "Local port", req.getLocalPort());
		} catch (NoSuchMethodError error) {
		}
		print(out, "Authorization scheme", req.getAuthType());
		if (req.getLocale() != null) {
			print(out, "Preferred Client Locale", req.getLocale().toString());
		} else {
			print(out, "Preferred Client Locale", "none");
		}
		Enumeration<Locale> ee = req.getLocales();
		while (ee.hasMoreElements()) {
			Locale cLocale = ee.nextElement();
			if (cLocale != null) {
				print(out, "All Client Locales", cLocale.toString());
			} else {
				print(out, "All Client Locales", "none");
			}
		}
		print(out, "Context Path", escapeChar(req.getContextPath()));
		if (req.getUserPrincipal() != null) {
			print(out, "User Principal", escapeChar(req.getUserPrincipal().getName()));
		} else {
			print(out, "User Principal", "none");
		}
		out.println("</table><BR><BR>");

		Enumeration<String> e = req.getHeaderNames();
		if (e.hasMoreElements()) {
			out.println("<h2>Request headers:</h2>");
			out.println("<TABLE Border=\"2\" WIDTH=\"65%\" BGCOLOR=\"#DDDDFF\">");
			while (e.hasMoreElements()) {
				String name = e.nextElement();
				out.println("<tr><td>" + escapeChar(name) + "</td><td>"
						+ escapeChar(req.getHeader(name)) + "</td></tr>");
			}
			out.println("</table><BR><BR>");
		}

		e = req.getParameterNames();
		if (e.hasMoreElements()) {
			out.println("<h2>Servlet parameters (Single Value style):</h2>");
			out.println("<TABLE Border=\"2\" WIDTH=\"65%\" BGCOLOR=\"#DDDDFF\">");
			while (e.hasMoreElements()) {
				String name = e.nextElement();
				out.println("<tr><td>" + escapeChar(name) + "</td><td>"
						+ escapeChar(req.getParameter(name)) + "</td></tr>");
			}
			out.println("</table><BR><BR>");
		}

		e = req.getParameterNames();
		if (e.hasMoreElements()) {
			out.println("<h2>Servlet parameters (Multiple Value style):</h2>");
			out.println("<TABLE Border=\"2\" WIDTH=\"65%\" BGCOLOR=\"#DDDDFF\">");
			while (e.hasMoreElements()) {
				String name = e.nextElement();
				String vals[] = (String[]) req.getParameterValues(name);
				if (vals != null) {

					out.print("<tr><td>" + escapeChar(name) + "</td><td>");
					out.print(escapeChar(vals[0]));
					for (int i = 1; i < vals.length; i++)
						out.print(", " + escapeChar(vals[i]));
					out.println("</td></tr>");
				}
			}
			out.println("</table><BR><BR>");
		}

		String cipherSuite = (String) req
				.getAttribute("javax.net.ssl.cipher_suite");
		if (cipherSuite != null) {
			X509Certificate certChain[] = (X509Certificate[]) req
					.getAttribute("javax.net.ssl.peer_certificates");

			out.println("<h2>HTTPS Information:</h2>");
			out.println("<TABLE Border=\"2\" WIDTH=\"65%\" BGCOLOR=\"#DDDDFF\">");
			out.println("<tr><td>Cipher Suite</td><td>" + escapeChar(cipherSuite) + "</td></tr>");

			if (certChain != null) {
				for (int i = 0; i < certChain.length; i++) {
					out.println("client cert chain [" + i + "] = "
							+ escapeChar(certChain[i].toString()));
				}
			}
			out.println("</table><BR><BR>");
		}

		Cookie[] cookies = req.getCookies();
		if (cookies != null && cookies.length > 0) {
			out.println("<H2>Client cookies</H2>");
			out.println("<TABLE Border=\"2\" WIDTH=\"65%\" BGCOLOR=\"#DDDDFF\">");
			out.println("<tr><td>Key</td><td>Value</td><td>Cookie Version</td></tr>");
			for (int i = 0; i < cookies.length; i++) {
				out.println("<tr><td>" + escapeChar(cookies[i].getName())
						+ "</td><td>" + escapeChar(cookies[i].getValue())
						+ "</td><td>" + cookies[i].getVersion()
						+ "</td></tr>");
			}
			out.println("</table><BR><BR>");
		}

		e = req.getAttributeNames();
		if (e.hasMoreElements()) {
			out.println("<h2>Request attributes:</h2>");
			out.println("<TABLE Border=\"2\" WIDTH=\"65%\" BGCOLOR=\"#DDDDFF\">");
			while (e.hasMoreElements()) {
				String name = e.nextElement();
				out.println("<tr><td>" + escapeChar(name) + "</td><td>"
						+ escapeChar(req.getAttribute(name).toString())
						+ "</td></tr>");
			}
			out.println("</table><BR><BR>");
		}

		out.println("<h2>Servlet Context Information:</h2>");
		out.println("<table border=\"2\" WIDTH=\"65%\" BGCOLOR=\"#DDDDFF\">");
		print(out, "Servlet Container", context.getServerInfo());
		print(out, "Servlet Context Name", context.getServletContextName());
		print(out, "Servlet API Major version", context.getMajorVersion());
		print(out, "Servlet API Minor version", context.getMinorVersion());
		out.println("</table><BR><BR>");

		e = getServletContext().getAttributeNames();
		if (e.hasMoreElements()) {
			out.println("<h2>ServletContext attributes:</h2>");
			out.println("<TABLE Border=\"2\" WIDTH=\"65%\" BGCOLOR=\"#DDDDFF\">");
			while (e.hasMoreElements()) {
				String name = (String) e.nextElement();
				out.println("<tr><td>"
						+ escapeChar(name)
						+ "</td><td>"
						+ escapeChar(getServletContext().getAttribute(name).toString())
						+ "</td></tr>");
			}
			out.println("</table><BR><BR>");
		}

		out.println("<h2>Session information:</h2>");
		out.println("<TABLE Border=\"2\" WIDTH=\"65%\" BGCOLOR=\"#DDDDFF\">");
		print(out, "Is New", Boolean.toString(session.isNew()));
		print(out, "Session ID", session.getId());
		print(out, "Current time", dateToString(System.currentTimeMillis()));
		print(out, "Last accessed time", dateToString(session.getLastAccessedTime()));
		print(out, "Creation time", dateToString(session.getCreationTime()));
		String mechanism = "unknown";
		if (req.isRequestedSessionIdFromCookie()) {
			mechanism = "cookie";
		} else if (req.isRequestedSessionIdFromURL()) {
			mechanism = "url-encoding";
		}
		print(out, "Session-tracking mechanism", mechanism);
		out.println("</table><BR><BR>");

		Enumeration<String> vals = session.getAttributeNames();
		if (vals.hasMoreElements()) {
			out.println("<h2>Session values</h2>");
			out.println("<TABLE Border=\"2\" WIDTH=\"65%\" BGCOLOR=\"#DDDDFF\">");

			while (vals.hasMoreElements()) {
				String name = (String) vals.nextElement();
				out.println("<tr><td>" + escapeChar(name) + "</td><td>"
						+ escapeChar(session.getAttribute(name).toString())
						+ "</td></tr>");
			}
			out.println("</table><BR><BR>");
		}

		out.println("</body></html>");
	}

	private static String dateToString(long date) {
		return (date >= 0)?
				new Date(date).toString() : Long.toString(date);
	}
	private void print(PrintWriter out, String name, String value) {
		out.println("<tr><td>" + name + "</td><td>"
				+ (value == null ? "&lt;none&gt;" : escapeChar(value))
				+ "</td></tr>");
	}

	private void print(PrintWriter out, String name, int value) {
		out.print("<tr><td>" + name + "</td><td>");
		if (value == -1) {
			out.print("&lt;none&gt;");
		} else {
			out.print(value);
		}
		out.println("</td></tr>");
	}

	private String escapeChar(String str) {
		return Html.escapeChar(str);
	}
}
