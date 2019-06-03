/*
 * EncodeTestServlet.java
 * 
 * 作成日: 2004/10/21
 * Created by: T.Takakiyo
 * $Id: EncodeTestServlet.java,v 1.2 2004/11/09 02:31:23 takakiyo Exp $
 */
package com.ibm.jp.support.http;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * EncodeTestServlet
 * 
 * Created: 2004/10/21
 * @author T.Takakiyo
 */
public class EncodeTestServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3541472631372493645L;

	/* (非 Javadoc)
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
		throws ServletException, IOException
	{
		resp.setContentType("text/html; charset=UTF-8");
		PrintWriter out = resp.getWriter();
		out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
		out.println("<html>");
		out.println("<head>");
		out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">");
		out.println("<title>Encoding Test</title>");
		out.println("</head>");
		out.println("<body>");
		out.println("<h1>Encoding Test</h1>");
		out.println("<form method=\"POST\" name=\"charset\" action=\"" + req.getRequestURI() + "\">");
		out.println("<h2>Content-Type Header charset</h2>");
		out.println("<p><input type=\"radio\" checked name=\"contentType\" value=\"none\">None <input type=\"radio\" name=\"contentType\" value=\"Shift_JIS\">Shift_JIS <input type=\"radio\" name=\"contentType\" value=\"Windows-31J\">Windows-31J <input type=\"radio\" name=\"contentType\" value=\"EUC-JP\">EUC-JP <input type=\"radio\" name=\"contentType\" value=\"ISO-2022-JP\">ISO-2022-JP <input type=\"radio\" name=\"contentType\" value=\"UTF-8\">UTF-8<br>");
		out.println("<input type=\"radio\" name=\"contentType\" value=\"other\">Other <input type=\"text\" name=\"contentTypeOther\" value=\"\" size=\"12\"></p>");
		out.println("<h2>&quot;http-equiv&quot; Meta Tag</h2>");
		out.println("<p><input type=\"radio\" checked name=\"httpEquiv\" value=\"none\">None <input type=\"radio\" name=\"httpEquiv\" value=\"Shift_JIS\">Shift_JIS <input type=\"radio\" name=\"httpEquiv\" value=\"Windows-31J\">Windows-31J <input type=\"radio\" name=\"httpEquiv\" value=\"EUC-JP\">EUC-JP <input type=\"radio\" name=\"httpEquiv\" value=\"ISO-2022-JP\">ISO-2022-JP <input type=\"radio\" name=\"httpEquiv\" value=\"UTF-8\">UTF-8<br>");
		out.println("<input type=\"radio\" name=\"httpEquiv\" value=\"other\">Other <input type=\"text\" name=\"httpEquivOther\" value=\"\" size=\"12\"></p>");
		out.println("<h2>Content encoding</h2>");
		out.println("<p><input type=\"radio\" checked name=\"encoding\" value=\"Cp943C\">Cp943C <input type=\"radio\" name=\"encoding\" value=\"MS932\">MS932 <input type=\"radio\" name=\"encoding\" value=\"EUC-JP\">EUC-JP <input type=\"radio\" name=\"encoding\" value=\"ISO-2022-JP\">ISO-2022-JP <input type=\"radio\" name=\"encoding\" value=\"UTF-8\">UTF-8<br>");
		out.println("<input type=\"radio\" name=\"encoding\" value=\"other\">Other <input type=\"text\" name=\"encodingOther\" value=\"\" size=\"12\"></p>");
		out.println("<h2>Display Text</h2>");
		out.println("<textarea rows=\"4\" cols=\"64\" name=\"text\"></textarea>");
		out.println("<p><input type=\"submit\" name=\"submit\" value=\"Submit\"></p>");
		out.println("</form>");
		out.println("</body>");
		out.println("</html>");
	}

	/* (非 Javadoc)
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
		throws ServletException, IOException
	{
		req.setCharacterEncoding("UTF-8");
		
		String contentType = req.getParameter("contentType");
		if ("other".equals(contentType)) {
			contentType = req.getParameter("contentTypeOther");
		}
		if (contentType == null || contentType.equals("") ||contentType.equals("none")) {
			resp.setContentType("text/html");
		} else {
			resp.setContentType("text/html; charset=" + contentType);
		}
		
		String httpEquiv = req.getParameter("httpEquiv");
		if ("other".equals(httpEquiv)) {
			httpEquiv = req.getParameter("httpEquivOther");
		}
		String text = req.getParameter("text");
		
		StringBuffer buf = new StringBuffer();
		buf.append("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\r\n<html>\r\n<head>\r\n");
		if (httpEquiv != null && !httpEquiv.equals("") && !httpEquiv.equals("none")) {
			buf.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=");
			buf.append(httpEquiv);
			buf.append("\">\r\n");
		}
		buf.append("<title>Encoding Test</title>\r\n</head>\r\n<body>\r\n");
		buf.append(text);
		buf.append("</body>\r\n</html>\r\n");
		
		String encoding = req.getParameter("encoding");
		if ("other".equals(encoding)) {
			encoding = req.getParameter("encodingOther");
		}
		ServletOutputStream out = resp.getOutputStream();
		out.write(buf.toString().getBytes(encoding));
	}

}
