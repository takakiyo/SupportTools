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
