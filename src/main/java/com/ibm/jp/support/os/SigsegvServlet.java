package com.ibm.jp.support.os;

import java.io.IOException;
import java.lang.management.ManagementFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class SigsegvServlet
 */
@WebServlet("/sigsegv")
public class SigsegvServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SigsegvServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String sig = request.getParameter("sig");
		boolean kill = (sig != null && sig.equals("kill"));
//		long pid = ProcessHandle.current().pid();
		String pid = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
		if (System.getProperty("os.name").toLowerCase().contains("windows")) {
			Runtime.getRuntime().exec("taskkill /PID " + pid);
		} else {
			if (kill) {
				Runtime.getRuntime().exec("kill -9 " + pid);
			} else {
				Runtime.getRuntime().exec("kill -SEGV " + pid);
			}
		}
		response.getWriter().println("Done");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
