package com.ibm.jp.support.http;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SetCookieServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.getRequestDispatcher("WEB-INF/setcookie.jsp").forward(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");

		String action = req.getParameter("action");

		if ("delete".equals(action)) {
			// Cookie 削除: 同名・同パスで Max-Age=0 のクッキーを返す
			String deleteName = req.getParameter("deleteName");
			if (deleteName != null && !deleteName.isEmpty()) {
				Cookie c = new Cookie(deleteName, "");
				c.setMaxAge(0);
				c.setPath("/");
				resp.addCookie(c);
				req.setAttribute("message", "Cookie \"" + deleteName + "\" を削除しました。");
			}
		} else {
			// Cookie 追加
			String key = req.getParameter("key");
			String value = req.getParameter("value");
			String versionStr = req.getParameter("version");
			String maxAgeStr = req.getParameter("maxAge");
			String path = req.getParameter("path");
			String domain = req.getParameter("domain");
			String secure = req.getParameter("secure");
			String httpOnly = req.getParameter("httpOnly");

			if (key != null && !key.isEmpty()) {
				Cookie c = new Cookie(key, value != null ? value : "");

				int version = 0;
				try {
					version = Integer.parseInt(versionStr);
				} catch (NumberFormatException e) {
					// ignore
				}
				c.setVersion(version);

				if (maxAgeStr != null && !maxAgeStr.isEmpty()) {
					try {
						c.setMaxAge(Integer.parseInt(maxAgeStr));
					} catch (NumberFormatException e) {
						// ignore — omit Max-Age
					}
				}

				if (path != null && !path.isEmpty()) {
					c.setPath(path);
				} else {
					c.setPath("/");
				}

				if (domain != null && !domain.isEmpty()) {
					c.setDomain(domain);
				}

				if ("on".equals(secure)) {
					c.setSecure(true);
				}

				if ("on".equals(httpOnly)) {
					c.setHttpOnly(true);
				}

				resp.addCookie(c);
				req.setAttribute("message", "Cookie \"" + key + "\" をセットしました。");
			}
		}

		req.getRequestDispatcher("WEB-INF/setcookie.jsp").forward(req, resp);
	}

}
