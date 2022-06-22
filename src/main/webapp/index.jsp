<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<HTML>
<HEAD>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" session="false"
	import="com.ibm.jp.util.Env" %>
<META http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<META name="GENERATOR" content="IBM WebSphere Studio" />
<META http-equiv="Content-Style-Type" content="text/css" />
<TITLE>Support Tools</TITLE>
</HEAD>
<BODY>
<H1>WebSphere Application Server: Support Tools</H1>
<P align="right">T.Takakiyo (takakiyo@jp.ibm.com)<BR />
IBM Japan</P>
<H2>Tools</H2>
<UL>
	<LI><A href="jndiLookup">JNDI Lookup Test</A></LI>
	<LI><A href="classInfo">Class Infomation Dumper</A></LI>
	<LI><A href="heapWatcher">Heap Watcher</A></LI>
	<LI><A href="encodingTest">Encoding Test</A></LI>
	<LI><A href="senderr.jsp">HTTP Error code Test</A></LI>
	<li><a href="javaDump">Create Java Dump</a></li>
</UL>
<H2>View Informations</H2>
<UL>
	<LI><A href="showThreads">Server Threads</A></LI>
	<% if (Env.isTiger()) { %>
	<LI><A href="threadDump">Thread Dump</A></LI>
	<% } %>
	<LI><A href="systemProperties">JVM System Properties</A></LI>
	<LI><A href="osEnv">OS Environments</A></LI>
	<LI><A href="osInfo">OS Infomation</A></LI>
	<LI><A href="snoop">HTTP Request infomations (snoop)</A></LI>
	<LI><A href="jspObjDump.jsp">JSP Objects</A></LI>
	<LI><A href="mbeanServerInfo">MBean Server Info</A></LI>
	<LI><A href="networkaddress.cache.jsp">InetAddress Cache Policy</A></LI>
	<LI><A href="security.provider.jsp">Security Provider</A></LI>
</UL>
<H2>Trouble Test</H2>
<UL>
	<li>CPU Loader <a href="cpuload?action=start">start/add</a> / <a href="cpuload?action=stop">stop all</a>
	<li>Long response <a href="longResponse?time=10000">10sec.</a> / <a href="longResponse">1min.</a> / <a href="longResponse?time=99999999">Infinity</a></li>
	<li>Process Abend <a href="sigsegv">SIGSEGV</a> / <a href="sigsegv?sig=kill">SIGKILL</a></li>
</UL>
</BODY>
<!--
	WebSphere Application Server: Support Tools
	Version 1.6
	
	2003-06-21    1.0    tnk    Created.
	2003-07-25    1.1    tnk    Added Heap Watcher
	2003-09-12    1.2    tnk    Added JVM System Properties
	2003-09-19    1.3    tnk    Added OS informations
	2004-11-09    1.4    tnk    Added Encoding Test
	2006-07-25    1.5    tnk    Support J2SE 5.0 Tiger
	2007-06-01    1.6    tnk    Added HTTP Error code Test
	2007-12-??    1.7    tnk    Added MBean Server Info
	2010-11-25    1.8    tnk    Added InetAddress Cache Policy
	2012-05-11    1.9    tnk    Support Windows 7/Mac OS X
	2016-08-10    2.0    tnk    fix JDNI lookup tool
	2022-06-22    3.0    tnk    fix JDNI lookup tool
-->
</HTML>
