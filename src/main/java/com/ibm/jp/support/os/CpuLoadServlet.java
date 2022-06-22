package com.ibm.jp.support.os;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class CpuLoadServlet
 */
public class CpuLoadServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CpuLoadServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String action = request.getParameter("action");
		if (action != null) {
			if (action.equals("start")) {
				start();
			}
			if (action.equals("stop")) {
				stop();
			}
		}
		response.setContentType("text/plain; charset=UTF-8");
		if (threads > 1) {
			response.getWriter().println(threads + " CPU loaders are running.");
		} else if (threads == 1) {
			response.getWriter().println("CPU loader is running.");
		} else {
			response.getWriter().println("CPU loader is not running.");
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	private static int start() {
		Thread roader = new Thread() {
			@Override
			public void run() {
				System.out.println("Roader has started.");
				tarai(100,50,0);
				System.out.println("Roader stopped.");
			}
		};
		running = true;
		roader.start();
		return ++threads;
	}
	
	private static void stop() {
		if (!running) return;
		running = false;
		threads = 0;
	}
	
	private static int tarai(int x, int y, int z) {
		if (!running) return 0;
		if (x <= y) {
			return y;
		} else {
			return tarai(tarai(x-1,y,z), tarai(y-1,z,x), tarai(z-1,x,y));
		}
	}
	
	private static boolean running = false;
	private static int threads;

}
