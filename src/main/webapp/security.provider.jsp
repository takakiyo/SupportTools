<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" %>
<%@page import="java.util.*" %>
<%@page import="java.security.Provider" %>
<%@page import="java.security.Security" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Security Providers</title>
</head>
<body>
<H1>Security Provider Index</H1>
<UL>
<%  Provider[] provs = Security.getProviders();
    Arrays.sort(provs, new Comparator<Provider>() {
    	public int compare(Provider o1, Provider o2) {
    		return o1.getName().compareTo(o2.getName());
    	}
    });
    for (Provider provider : provs) { %>
    <LI><A href="#<%= provider.getName() %>"><%= provider.getName() %></A>
<%  } %>
</UL>

<%  for (Provider provider : provs) { %>
<h1><A name="<%= provider.getName() %>">Provider: <%= provider.getName() %></A></h1>
<dl>
<dt>Implementation Class</dt>
<dd><%= provider.getClass().getName() %></dd>
<dt>Infomation</dt>
<dd><pre><%= provider.getInfo() %></pre></dd>
<dt>Version</dt>
<dd><%= provider.getVersion() %></dd>
<dt>Properties</dt>
<dd><ul><%  
        ArrayList<String> keys = new ArrayList<String>();
        for (Object key : provider.keySet()) keys.add(key.toString());
        Collections.sort(keys);
        for (String key : keys) {
%><li><%= key %> = <%= provider.getProperty(key.toString()) %></li><%
        }
%></ul></dd></dl>
<%  } %>
</body>
</html>
