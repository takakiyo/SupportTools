<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" %><!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@page import="sun.net.InetAddressCachePolicy"%>
<%@page import="java.security.Security"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>InetAddress Cache Policy</title>
</head>
<body>
<h1>InetAddress Cache Policy</h1>
<dl>
<dt>Positive Cache Policy</dt>
<dd><%= InetAddressCachePolicy.get() %></dd>
<dt>Negative Cache Policy</dt>
<dd><%= InetAddressCachePolicy.getNegative() %></dd>
    <dt>Security.getProperty("networkaddress.cache.ttl")</dt>
<dd><%= Security.getProperty("networkaddress.cache.ttl") %></dd>
    <dt>Security.getProperty("networkaddress.cache.negative.ttl")</dt>
<dd><%= Security.getProperty("networkaddress.cache.negative.ttl") %></dd>
    <dt>Security.getProperty("sun.net.inetaddr.ttl")</dt>
<dd><%= Security.getProperty("sun.net.inetaddr.ttl") %></dd>
    <dt>Security.getProperty("sun.net.inetaddr.negative.ttl")</dt>
<dd><%= Security.getProperty("sun.net.inetaddr.negative.ttl") %></dd>
</dl>
</body>
</html>
