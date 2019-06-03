// Decompiled by Jad v1.5.8d. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   HeapWatchServlet.java

package com.ibm.jp.support.heap;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.*;

// Referenced classes of package com.ibm.jp.support.heap:
//            HeapWatcher

public class HeapWatchServlet extends HttpServlet
{

    public HeapWatchServlet()
    {
    }

    public void init()
    {
        watcher = new HeapWatcher();
        isOutStdout = watcher.getIsOutStdout();
        isOutStderr = watcher.getIsOutStderr();
        interval = watcher.getInterval();
        controlDenied = false;
        String str = getInitParameter("isOutStdout");
        if(str != null)
        {
            if(str.equalsIgnoreCase("true"))
                isOutStdout = true;
            else
                isOutStdout = false;
            watcher.setIsOutStdout(isOutStdout);
        }
        str = getInitParameter("isOutStderr");
        if(str != null)
        {
            if(str.equalsIgnoreCase("true"))
                isOutStderr = true;
            else
                isOutStderr = false;
            watcher.setIsOutStderr(isOutStderr);
        }
        str = getInitParameter("interval");
        if(str != null)
        {
            int i;
            try
            {
                i = Integer.valueOf(str).intValue();
            }
            catch(NumberFormatException ex)
            {
                i = 0;
            }
            if(i > 0)
                watcher.setInterval(i);
        }
        str = getInitParameter("controlDenied");
        if(str != null)
            if(str.equalsIgnoreCase("true"))
                controlDenied = true;
            else
                controlDenied = false;
        try
        {
            watcher.start();
        }
        catch(IllegalThreadStateException illegalthreadstateexception) { }
    }

    public void destroy()
    {
        if(watcher != null && watcher.isAlive())
        {
            watcher.stopWatcher();
            watcher = null;
        }
    }

    public void service(HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException
    {
        res.setContentType("text/html");
        PrintWriter out = res.getWriter();
        out.println("<HTML>");
        out.println("<HEAD><TITLE>JVM Heap Watcher</TITLE></HEAD>");
        out.println("<BODY>");
        String str = req.getParameter("Statstic");
        if(str != null || controlDenied)
        {
            out.println("<H1>JVM Heap statics</H1>");
            if(watcher != null)
            {
                out.println("<PRE>");
                long rate = 1024L;
                for(int i = 0; watcher.getDate(i) != null; i++)
                    while(watcher.getTotal(i) / rate >= 120L) 
                        rate *= 2L;

                out.print("Used Memory: * = ");
                if(rate >= 0x100000L)
                    out.println(rate / 0x100000L + "Mbyte(s)");
                else
                    out.println(rate / 1024L + "Kbyte(s)");
                for(int i = 0; (str = watcher.getDate(i)) != null; i++)
                {
                    int t = (int)(watcher.getTotal(i) / rate);
                    int u = t - (int)(watcher.getFree(i) / rate);
                    out.print(str + " ");
                    int j;
                    for(j = 1; j <= u; j++)
                        out.print("*");

                    for(; j <= t; j++)
                        if(j % 10 == 0)
                            out.print("+");
                        else
                            out.print("-");

                    out.println("+");
                }

                out.println("");
                for(int i = 0; (str = watcher.getStat(i)) != null; i++)
                    out.println(str);

                out.println("");
                out.println("</PRE>");
            } else
            {
                out.println("Heap Watcher is not running.");
            }
            out.println("</BODY></HTML>");
            return;
        }
        str = req.getParameter("Command");
        if(str != null)
        {
            if(str.compareTo("Start") == 0)
            {
                if(watcher == null)
                    watcher = new HeapWatcher();
                watcher.setIsOutStdout(isOutStdout);
                watcher.setIsOutStderr(isOutStderr);
                try
                {
                    if(!watcher.isAlive())
                        watcher.start();
                }
                catch(IllegalThreadStateException illegalthreadstateexception) { }
            }
            if(str.compareTo("Reset") == 0 && watcher != null)
                watcher.init();
            if(str.compareTo("Stop") == 0)
            {
                if(watcher != null && watcher.isAlive())
                    watcher.stopWatcher();
                watcher = null;
            }
            if(str.compareTo("Update") == 0 && watcher != null && watcher.isAlive())
            {
                int i;
                try
                {
                    i = Integer.valueOf(req.getParameter("interval")).intValue();
                }
                catch(NumberFormatException ex)
                {
                    i = 0;
                }
                if(i > 0 && (long)i != interval)
                {
                    interval = i;
                    watcher.setInterval(interval);
                }
                isOutStdout = Boolean.valueOf(req.getParameter("stdout")).booleanValue();
                watcher.setIsOutStdout(isOutStdout);
                isOutStderr = Boolean.valueOf(req.getParameter("stderr")).booleanValue();
                watcher.setIsOutStderr(isOutStderr);
            }
        }
        out.println("<H1>Heap Watcher Control</H1>");
        out.print("<HR>");
        out.println("<FORM action=\"" + req.getRequestURI() + "\" method=\"POST\">");
        out.print("<B>Current Status:</B> ");
        if(watcher != null)
        {
            if(watcher.isAlive())
                out.println("running");
            else
                out.println("not running");
        } else
        {
            out.println("stop");
        }
        out.println("<BR>");
        out.println("<INPUT type=\"SUBMIT\" value=\"Start\" name=\"Command\">&nbsp;");
        out.println("<INPUT type=\"SUBMIT\" value=\"Reset\" name=\"Command\">&nbsp;");
        out.println("<INPUT type=\"SUBMIT\" value=\"Stop\"  name=\"Command\">&nbsp;");
        out.println("</FORM>");
        out.print("<HR>");
        if(watcher != null)
        {
            interval = watcher.getInterval();
            out.println("<FORM action=\"" + req.getRequestURI() + "\" method=\"POST\">");
            out.println("Interval = ");
            out.println("<INPUT type=\"text\" value=\"" + interval + "\" name=\"interval\">");
            out.println("msec.<BR>");
            out.print("<INPUT type=\"checkbox\" name=\"stdout\" value=\"true\"");
            if(isOutStdout)
                out.print(" checked");
            out.println(">Standard Out&nbsp;&nbsp;&nbsp;");
            out.print("<INPUT type=\"checkbox\" name=\"stderr\" value=\"true\"");
            if(isOutStderr)
                out.print(" checked");
            out.println(">Standard Error<BR>");
            out.println("<INPUT type=\"SUBMIT\" value=\"Update\" name=\"Command\">");
            out.println("</FORM>");
            out.print("<HR>");
            out.println("<FORM action=\"" + req.getRequestURI() + "\" method=\"GET\">");
            out.println("<INPUT type=\"SUBMIT\" value=\"ShowStatstic\" name=\"Statstic\">");
            out.println("</FORM>");
            out.println("<HR>");
        }
        out.println("<FORM action=\"" + req.getRequestURI() + "\" method=\"GET\">");
        out.println("<INPUT type=\"SUBMIT\" value=\"GerbageCollection\" name=\"GC\">");
        out.println("</FORM>");
        out.println("<HR>");
        out.println("</BODY></HTML>");
        str = req.getParameter("GC");
        if(str != null && !str.equals(""))
            System.gc();
    }

    private static final long serialVersionUID = 0x5e6d912c62403ac3L;
    public static final String version = "HeapWatchServlet 1.30";
    private static boolean isOutStdout;
    private static boolean isOutStderr;
    private static boolean controlDenied;
    private static long interval;
    private HeapWatcher watcher;
}
