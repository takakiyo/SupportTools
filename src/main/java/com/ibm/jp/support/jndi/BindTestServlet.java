// Decompiled by Jad v1.5.8d. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   BindTestServlet.java

package com.ibm.jp.support.jndi;

//import com.ibm.websphere.naming.JndiHelper;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Hashtable;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.*;

// Referenced classes of package com.ibm.jp.support.jndi:
//            Hoge5

public class BindTestServlet extends HttpServlet
{

    public BindTestServlet()
    {
    }

    public void init()
        throws ServletException
    {
        super.init();
//        try
//        {
//            Hashtable param = new Hashtable();
//            param.put("java.naming.factory.initial", "com.ibm.websphere.naming.WsnInitialContextFactory");
//            param.put("java.naming.provider.url", "corbaloc::localhost:2809");
//            InitialContext context = new InitialContext(param);
//            JndiHelper.recursiveRebind(context, "config/Hoge1", "hoge1");
//            JndiHelper.recursiveRebind(context, "config/Hoge2", new Integer(2));
//            JndiHelper.recursiveRebind(context, "config/Hoge3", new char[] {
//                '1', '2', '3'
//            });
//            Hashtable hoge4 = new Hashtable();
//            hoge4.put("4", "hoge4");
//            JndiHelper.recursiveRebind(context, "config/Hoge4", hoge4);
//            JndiHelper.recursiveRebind(context, "config/Hoge5", new Hoge5("5"));
//        }
//        catch(NamingException ex)
//        {
//            throw new ServletException("NamingException", ex);
//        }
    }

    public void doGet(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException
    {
        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();
        out.print("<heml><head>bind</head><body>");
        try
        {
            Hashtable<String,String> param = new Hashtable<String,String>();
            param.put("java.naming.factory.initial", "com.ibm.websphere.naming.WsnInitialContextFactory");
            param.put("java.naming.provider.url", "corbaloc::localhost:2809");
            InitialContext context = new InitialContext(param);
            for(int i = 1; i <= 5; i++)
            {
                String name = "config/Hoge" + i;
                Object o = context.lookup(name);
                out.println("<b>" + name + "</b><br>Class = " + o.getClass().getName() + "<br>Value = " + o.toString() + "<br>");
            }

        }
        catch(NamingException ex)
        {
            throw new ServletException("NamingException", ex);
        }
        out.print("</body></html>");
    }

    private static final long serialVersionUID = 0xc90fb4207f7c646aL;
}
