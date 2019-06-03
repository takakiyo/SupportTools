package com.ibm.jp.support;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Comparator;

import javax.servlet.ServletException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @version 	$Id: ShowThreadsServlet.java,v 1.1 2004/10/25 06:37:54 takakiyo Exp $
 * @author T.Takakiyo
 */
public class ShowThreadsServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5956344989575392238L;

	/**
	* @see javax.servlet.http.HttpServlet#void (javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	*/
	public void service(HttpServletRequest req, HttpServletResponse resp)
		throws ServletException, IOException
	{
        resp.setContentType("text/html; charset=UTF-8");
        PrintWriter out = resp.getWriter();
        out.println("<HTML>");
        out.println("<HEAD><TITLE>ShowThreadsServlet</TITLE></HEAD>");
        out.println("<BODY>");
        out.println("<H1>ShowThreadsServlet</H1>");
        
        ThreadGroup current = Thread.currentThread().getThreadGroup();
        ThreadGroup parent = current.getParent();
        while (parent.getParent() != null) parent = parent.getParent();
        
        int n = parent.activeCount() + 10;
        Thread threads[] = new Thread[n];
        n = parent.enumerate(threads);
        Arrays.sort(threads, new Comparator<Thread>() {
        	public int compare(Thread a, Thread b) {
        		if (a == null) {
        			return (b == null)? 0 : 1;
        		} else if (b == null) {
        			return -1;
        		}
        		int n = a.getThreadGroup().getName().compareTo(b.getThreadGroup().getName());
        		if (n != 0) return n;
        		return a.getName().compareTo(b.getName());
        	}
        });

        out.println("# of threads = " + n + "<!-- " + threads.length + " --><BR>");
        out.println("<TABLE border>");
        out.println("<TR><TH>Name</TH><TH>Priority</TH><TH>ThreadGroup</TH><TH>Daemon</TH></TR>");
        for (int i = 0; i < n; i++) {
            out.println("<TR>");
            out.println("<TD>" + threads[i].getName() + "</TD>");
            out.println("<TD>" + threads[i].getPriority() + "</TD>");
            out.println("<TD>" + threads[i].getThreadGroup().getName() + "</TD>");
            out.println("<TD>" + threads[i].isDaemon() + "</TD>");
            out.println("</TR>");
        }
        out.println("</TABLE>");
        out.println("</BODY></HTML>");
	}

	/**
	* @see javax.servlet.GenericServlet#void ()
	*/
	public void init() throws ServletException {

		super.init();

	}

}
