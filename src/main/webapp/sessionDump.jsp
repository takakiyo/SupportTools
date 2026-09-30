<%@page
	language="java" contentType="text/html; charset=UTF-8"
	session="true"
	pageEncoding="UTF-8"
	import="java.lang.reflect.Field"
	import="java.lang.reflect.Modifier"
	import="java.io.ByteArrayOutputStream"
	import="java.io.IOException"
	import="java.io.NotSerializableException"
	import="java.io.ObjectOutputStream"
	import="java.text.SimpleDateFormat"
	import="java.util.Date"
	import="java.util.Enumeration"
	import="java.util.Hashtable"
	import="java.util.Stack"
	import="java.util.Vector"
%><!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>HttpSession Dumper</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
</head>
<body>
<h1>HttpSession Dumper</h1>
<H2>General Session Informations</H2>
<TABLE Border="2" WIDTH="100%" BGCOLOR="#DDDDFF">
<tr><td>HttpSession Implement Class</td><td><%= session.getClass().getName() %></td></tr>
<tr><td>isRequestedSessionIdFromCookie</td><td><%= request.isRequestedSessionIdFromCookie() %></td></tr>
<tr><td>isRequestedSessionIdFromURL</td><td><%= request.isRequestedSessionIdFromURL() %></td></tr>
<tr><td>Session ID from HttpServletRequest</td><td><%= request.getRequestedSessionId() %></td></tr>
<tr><td>Session ID from HttpSession</td><td><%= session.getId() %></td></tr>
<tr><td>Created Time</td><td><%= formatter.format(new Date(session.getCreationTime())) %></td></tr>
<tr><td>Last Accessed Time</td><td><%= formatter.format(new Date(session.getLastAccessedTime())) %></td></tr>
<tr><td>Current Time</td><td><%= formatter.format(new Date()) %></td></tr>
<tr><td>Max Inactive Interval</td><td><%= session.getMaxInactiveInterval() %></td></tr>
</TABLE>
<H2>Session Object Contents</H2><%	
String values[] = session.getValueNames();
if (values == null) {
%>There is no content in the session.<%
} else {
    Hashtable nonSerializableClasses = new Hashtable();
%># of contents is <%= values.length %>.
<TABLE Border="2" WIDTH="100%" BGCOLOR="#DDDDFF"><%
    for (int i = 0; i < values.length; i++) {
        Object obj = session.getValue(values[i]);
        byte[] data = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(obj);
            oos.flush();
            oos.close();
            data = baos.toByteArray();
            baos.close();
        } catch (NotSerializableException ex) {
            nonSerializableClasses.put(obj.getClass().getName(), obj.getClass());
        }
 %>
<TR><TD colspan=2 align="center">Name: <B><%= escapeChar(values[i]) %></B></TD></TR>
<TR><TD>Object Class</TD><TD><%= obj.getClass().getName() %></TD></TR>
<TR><TD>String Value</TD><TD><%= escapeChar(obj.toString()) %></TD></TR>
<TR><TD>Serialized data Dump</TD><TD><PRE><%= (data == null)? "The object is not serializable." : escapeChar(dump(data)) %></PRE></TD></TR>
<TR><TD>Serialized data size</TD><TD><%= (data == null)? 0 : data.length %> bytes</TD></TR><%
        } %>
</TABLE>
<H2>Non Serializable Class Info.</H2>
<UL><% 
        Enumeration classes = nonSerializableClasses.keys();
        while (classes.hasMoreElements()) {
            String key = (String)classes.nextElement();
            Class cl = (Class)nonSerializableClasses.get(key);
            out.print("<LI>");
            printClassInfo(out, cl, new Stack());
        }%>
</UL>

<% } %>

<H2>Request Information</H2>
<H3>Basic Request</H3>
<TABLE Border="2" WIDTH="100%" BGCOLOR="#DDDDFF">
<tr><td>Method</td><td><%= escapeChar(request.getMethod()) %></td></tr>
<tr><td>Request URI</td><td><%= escapeChar(request.getRequestURI()) %></td></tr>
<tr><td>Request URL</td><td><%= escapeChar(request.getRequestURL().toString()) %></td></tr>
<tr><td>Query String</td><td><%= request.getQueryString() == null ? "(none)" : escapeChar(request.getQueryString()) %></td></tr>
<tr><td>Context Path</td><td><%= escapeChar(request.getContextPath()) %></td></tr>
<tr><td>Servlet Path</td><td><%= escapeChar(request.getServletPath()) %></td></tr>
<tr><td>Path Info</td><td><%= request.getPathInfo() == null ? "(none)" : escapeChar(request.getPathInfo()) %></td></tr>
<tr><td>Path Translated</td><td><%= request.getPathTranslated() == null ? "(none)" : escapeChar(request.getPathTranslated()) %></td></tr>
<tr><td>Protocol</td><td><%= escapeChar(request.getProtocol()) %></td></tr>
<tr><td>Scheme</td><td><%= escapeChar(request.getScheme()) %></td></tr>
<tr><td>isSecure</td><td><%= request.isSecure() %></td></tr>
<tr><td>Content Type</td><td><%= request.getContentType() == null ? "(none)" : escapeChar(request.getContentType()) %></td></tr>
<tr><td>Content Length</td><td><%= request.getContentLength() %></td></tr>
<tr><td>Character Encoding</td><td><%= request.getCharacterEncoding() == null ? "(none)" : escapeChar(request.getCharacterEncoding()) %></td></tr>
<tr><td>Locale</td><td><%= escapeChar(request.getLocale().toString()) %></td></tr>
</TABLE>
<H3>Client / Server</H3>
<TABLE Border="2" WIDTH="100%" BGCOLOR="#DDDDFF">
<tr><td>Remote Address</td><td><%= escapeChar(request.getRemoteAddr()) %></td></tr>
<tr><td>Remote Host</td><td><%= escapeChar(request.getRemoteHost()) %></td></tr>
<tr><td>Remote Port</td><td><%= request.getRemotePort() %></td></tr>
<tr><td>Local Address</td><td><%= escapeChar(request.getLocalAddr()) %></td></tr>
<tr><td>Local Name</td><td><%= escapeChar(request.getLocalName()) %></td></tr>
<tr><td>Local Port</td><td><%= request.getLocalPort() %></td></tr>
<tr><td>Server Name</td><td><%= escapeChar(request.getServerName()) %></td></tr>
<tr><td>Server Port</td><td><%= request.getServerPort() %></td></tr>
</TABLE>
<H3>Auth / User</H3>
<TABLE Border="2" WIDTH="100%" BGCOLOR="#DDDDFF">
<tr><td>Auth Type</td><td><%= request.getAuthType() == null ? "(none)" : escapeChar(request.getAuthType()) %></td></tr>
<tr><td>Remote User</td><td><%= request.getRemoteUser() == null ? "(none)" : escapeChar(request.getRemoteUser()) %></td></tr>
<tr><td>User Principal</td><td><%= request.getUserPrincipal() == null ? "(none)" : escapeChar(request.getUserPrincipal().getName()) %></td></tr>
</TABLE>
<H3>Request Headers</H3>
<TABLE Border="2" WIDTH="100%" BGCOLOR="#DDDDFF">
<tr><th>Name</th><th>Value</th></tr><%
{
    Enumeration headerNames = request.getHeaderNames();
    if (headerNames != null) {
        while (headerNames.hasMoreElements()) {
            String hname = (String) headerNames.nextElement();
            Enumeration hvals = request.getHeaders(hname);
            while (hvals.hasMoreElements()) {
                String hval = (String) hvals.nextElement();
%><tr><td><%= escapeChar(hname) %></td><td><%= escapeChar(hval) %></td></tr><%
            }
        }
    }
}
%>
</TABLE>
<H3>Request Parameters</H3><%
{
    Enumeration paramNames = request.getParameterNames();
    if (!paramNames.hasMoreElements()) {
%>There are no request parameters.<%
    } else {
%><TABLE Border="2" WIDTH="100%" BGCOLOR="#DDDDFF">
<tr><th>Name</th><th>Value</th></tr><%
        while (paramNames.hasMoreElements()) {
            String pname = (String) paramNames.nextElement();
            String[] pvals = request.getParameterValues(pname);
            for (int pi = 0; pi < pvals.length; pi++) {
%><tr><td><%= escapeChar(pname) %></td><td><%= escapeChar(pvals[pi]) %></td></tr><%
            }
        }
%></TABLE><%
    }
}
%>
<H3>Request Attributes</H3><%
{
    Enumeration attrNames = request.getAttributeNames();
    if (!attrNames.hasMoreElements()) {
%>There are no request attributes.<%
    } else {
%><TABLE Border="2" WIDTH="100%" BGCOLOR="#DDDDFF">
<tr><th>Name</th><th>Class</th><th>Value</th></tr><%
        while (attrNames.hasMoreElements()) {
            String aname = (String) attrNames.nextElement();
            Object aobj = request.getAttribute(aname);
%><tr><td><%= escapeChar(aname) %></td><td><%= aobj == null ? "(null)" : escapeChar(aobj.getClass().getName()) %></td><td><%= aobj == null ? "(null)" : escapeChar(aobj.toString()) %></td></tr><%
        }
%></TABLE><%
    }
}
%>
<H3>Cookies</H3><%
{
    javax.servlet.http.Cookie[] cookies = request.getCookies();
    if (cookies == null || cookies.length == 0) {
%>There are no cookies.<%
    } else {
%><TABLE Border="2" WIDTH="100%" BGCOLOR="#DDDDFF">
<tr><th>Name</th><th>Value</th><th>Domain</th><th>Path</th><th>Max Age</th><th>Secure</th><th>HttpOnly</th></tr><%
        for (int ci = 0; ci < cookies.length; ci++) {
            javax.servlet.http.Cookie ck = cookies[ci];
%><tr>
<td><%= escapeChar(ck.getName()) %></td>
<td><%= escapeChar(ck.getValue() == null ? "(null)" : ck.getValue()) %></td>
<td><%= ck.getDomain() == null ? "(none)" : escapeChar(ck.getDomain()) %></td>
<td><%= ck.getPath() == null ? "(none)" : escapeChar(ck.getPath()) %></td>
<td><%= ck.getMaxAge() %></td>
<td><%= ck.getSecure() %></td>
<td><%= ck.isHttpOnly() %></td>
</tr><%
        }
%></TABLE><%
    }
}
%>
<H3>Call Stack Trace</H3>
<PRE><%
{
    StackTraceElement[] elems = new Throwable().getStackTrace();
    for (int si = 0; si < elems.length; si++) {
        out.print(escapeChar("\tat " + elems[si].toString()));
        out.println();
    }
}
%></PRE>

</body>
</html>
<%!
    private static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss zzz");
    private static String escapeChar(String str) {
        char src[] = str.toCharArray();
        int len = src.length;
        for (int i = 0; i < src.length; i++) {
            switch (src[i]) {
                case '<':   // to "&lt;"
                    len += 3;
                    break;
                case '>':   // to "&gt;"
                    len += 3;
                    break;
                case '&':   // to "&amp;"
                    len += 4;
                    break;
            }
        }
        char ret[] = new char[len];
        int j = 0;
        for (int i = 0; i < src.length; i++) {
            switch (src[i]) {
                case '<':   // to "&lt;"
                    ret[j++] = '&';
                    ret[j++] = 'l';
                    ret[j++] = 't';
                    ret[j++] = ';';
                    break;
                case '>':   // to "&gt;"
                    ret[j++] = '&';
                    ret[j++] = 'g';
                    ret[j++] = 't';
                    ret[j++] = ';';
                    break;
                case '&':   // to "&amp;"
                    ret[j++] = '&';
                    ret[j++] = 'a';
                    ret[j++] = 'm';
                    ret[j++] = 'p';
                    ret[j++] = ';';
                    break;
                default:
                    ret[j++] = src[i];
                    break;
            }
        }
            
        return new String(ret);
    }
    private String dump(byte[] data) {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < data.length; i++) {
            if (i%16 == 0) {
                buf.append(Integer.toHexString(i).toUpperCase());
                buf.append("\t");
            }

            // print 16 bytes
            int anInt = data[i] & 0xFF;
            if (anInt > 15) {
                buf.append(Integer.toHexString(anInt).toUpperCase());
            } else {
                buf.append("0");
                buf.append(Integer.toHexString(anInt).toUpperCase());
            }
            if ((i+1)%4 == 0) {
                buf.append(" ");
            }

            // translate the 16 bytes and left overs
            if ((i+1) % 16 == 0 || i == (data.length-1)) {
                if ((i+1)%16 == 0) {
                    for (int j = 15; j >= 0; j--) {
                        anInt = data[i-j] & 0xFF;
                        if (anInt >= 32 && anInt <= 126)
                            buf.append((char)data[i-j]);
                        else
                            buf.append(".");
                    }
                } else {
                    for (int j=35-(data.length%16)/4-(data.length%16)*2; j>=0; j--)
                        buf.append(" ");
                    for ( int j=data.length%16-1; j>=0; j-- ) {
                        anInt = data[i-j] & 0xFF;
                        if (anInt >= 32 && anInt <=126)
                            buf.append((char)data[i-j]);
                        else
                            buf.append(".");
                    }
                }
                buf.append("\n");
            }
        }
        return buf.toString();
    }
    void printClassInfo(JspWriter out, Class cl, Stack st)  throws IOException {
	if (cl.isArray()) cl = cl.getComponentType();
        st.push(cl);
        Class su = cl.getSuperclass();
        Class in[] = cl.getInterfaces();
        out.print("--&gt;<FONT color=\"#007f7f\">");
        out.print(Modifier.toString((~Modifier.SYNCHRONIZED)&cl.getModifiers()));
        out.print(cl.isInterface()?
            "</FONT> <FONT color=\"#00007f\">interface</FONT> <B>":
            "</FONT> <FONT color=\"#00007f\">class</FONT> <B>");
        out.print(cl.getName());
        out.print("</B> <FONT color=\"#00007f\">extends</FONT> ");
        out.print(su.getName());
        if (in.length > 0) {
            out.print(" <FONT color=\"#00007f\">implements</FONT> ");
            for (int i = 0; i < in.length; i++) {
                if (i > 0) out.print(", ");
                out.print(in[i].getName());
            }
        }
        out.println();
        out.println("<UL>");
        Vector fields = new Vector();
        for (Class c = cl; !c.equals(Object.class); c = c.getSuperclass()) {
            Field[] fs = c.getDeclaredFields();
            for (int i = 0; i < fs.length; i++) {
                fields.add(fs[i]);
            }
        }
        for (int i = 0; i < fields.size(); i++) {
            Field field = (Field)fields.elementAt(i);
            int m = field.getModifiers();
            Class fcl = field.getType();
            String array = "";
            while (fcl.isArray()) {
                fcl = fcl.getComponentType();
                array += "[]";
            }
            if (Modifier.isStatic(m)) continue;
            out.print("<LI><FONT color=\"#007f7f\">");
            out.print(Modifier.toString(m));
            out.print("</FONT> ");
            out.print(fcl.getName());
            out.print(array);
            out.print(" ");
            out.print(field.getName());
            out.print(";");
            Class dcl = field.getDeclaringClass();
            if (!cl.equals(dcl)) {
                out.print(" (Declared in ");
                out.print(dcl.getName());
                out.print(")");
            }
            out.println();
            if (Modifier.isTransient(m)) {
                continue;
            }
            if (fcl.isPrimitive()) {
                continue;
            }
            if (fcl.isInterface()) {
                continue;
            }
            if (java.io.Serializable.class.isAssignableFrom(fcl)) {
                continue;
            }
            if (st.indexOf(fcl) >= 0) {
                continue;
            }
            if (fcl.getName().equals("java.lang.Object")) {
                continue;
            }
            out.println("<BR>");
            printClassInfo(out, fcl, st);
        }
        out.println("</UL>");
        st.pop();
    }
%>
