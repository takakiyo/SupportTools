/*
 * SendErrorTestServlet.java
 * 
 * $Id: $
 */

package com.ibm.jp.support.http;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.*;

/**
 * 
 * @author takakiyo
 */
public class SendErrorTestServlet extends HttpServlet {

	public SendErrorTestServlet() {
	}

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		perform(req, resp);
	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		perform(req, resp);
	}

	public void perform(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException
	{
		int sc = 0;
		try {
			sc = Integer.parseInt(req.getParameter("status.code"));
		} catch (Exception exception) {
		}
		if (sc == 0) {
			req.getRequestDispatcher("senderr.jsp").forward(req, resp);
		} else {
			String msg = req.getParameter("message");
			if (msg == null || msg.equals(""))
				resp.sendError(sc);
			else
				resp.sendError(sc, msg);
		}
	}

	private static final long serialVersionUID = 0xeb50f7afabcb51daL;
}
