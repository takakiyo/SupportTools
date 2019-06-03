package com.ibm.jp.support.os;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ibm.jp.util.Html;

public class OsEnvServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3348738687478218523L;

	/**
	 * 
	 */
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.setContentType("text/html; charset=UTF-8");
		PrintWriter out = resp.getWriter();

		out.print(
			"<HTML>\n" +
			"<HEAD>\n" +
			"<TITLE>OS Environments</TITLE>\n" +
			"</HEAD>\n" +
			"<BODY>\n" +
			"<H1>OS Environments</H1>\n"
		);
    
		out.println("<table border>");
		Map<String,String> envs = System.getenv();
		List<String> keys = new ArrayList<String>(envs.keySet());
		Collections.sort(keys);
		for (String key : keys) {
			out.print("<tr><td>");
			out.print(Html.escapeChar(key));
			out.print("</td><td>");
			out.print(Html.escapeChar(envs.get(key)));
			out.println("</td></tr>");
		}
		out.println("</table>");

		out.println("</BODY></HTML>");
	}

}
