<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<HTML>
<HEAD>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" session="true" import="com.ibm.jp.util.ClassCache" %>
<META http-equiv="Content-Type" content="text/html; charset=UTF-8">
<META name="GENERATOR" content="IBM WebSphere Studio">
<META http-equiv="Content-Style-Type" content="text/css" />
<TITLE>JSP Objects Dumper</TITLE>
</HEAD>
<BODY>
<H1>JSP Objects Dumper</H1>
<TABLE border="1">
	<TBODY>
		<TR>
			<TH>Object</TH>
			<TH>Class Name</TH>
			<TH>Real Class Name</TH>
		</TR>
		<TR>
			<TD>out</TD>
			<TD>javax.servlet.jsp.JspWriter</TD>
			<TD><A href="classInfo?class=<%=out.getClass().getName()%>"><%=out.getClass().getName()%></A></TD>
		</TR>
		<TR>
			<TD>request</TD>
			<TD>javax.servlet.http.HttpServletRequest</TD>
			<TD><A href="classInfo?class=<%=request.getClass().getName()%>"><%=request.getClass().getName()%></A></TD>
		</TR>
		<TR>
			<TD>response</TD>
			<TD>javax.servlet.http.HttpServletResponse</TD>
			<TD><A href="classInfo?class=<%=response.getClass().getName()%>"><%=response.getClass().getName()%></A></TD>
		</TR>
		<TR>
			<TD>page</TD>
			<TD>java.lang.Object</TD>
			<TD><A href="classInfo?class=<%=page.getClass().getName()%>"><%=page.getClass().getName()%></A></TD>
		</TR>
		<TR>
			<TD>session</TD>
			<TD>javax.servlet.http.HttpSessoin</TD>
			<TD><A href="classInfo?class=<%=session.getClass().getName()%>"><%=session.getClass().getName()%></A></TD>
		</TR>
		<TR>
			<TD>application</TD>
			<TD>javax.servlet.ServletContext</TD>
			<TD><A href="classInfo?class=<%=application.getClass().getName()%>"><%=application.getClass().getName()%></A></TD>
		</TR>
		<TR>
			<TD>pageContext</TD>
			<TD>javax.servlet.jsp.PageContext</TD>
			<TD><A href="classInfo?class=<%=pageContext.getClass().getName()%>"><%=pageContext.getClass().getName()%></A></TD>
		</TR>
		<TR>
			<TD>config</TD>
			<TD>javax.servlet.Servlet.Config</TD>
			<TD><A href="classInfo?class=<%=config.getClass().getName()%>"><%=config.getClass().getName()%></A></TD>
		</TR>
	</TBODY>
</TABLE>
</BODY>
</HTML>
<%
ClassCache.put(out.getClass());
ClassCache.put(request.getClass());
ClassCache.put(response.getClass());
ClassCache.put(page.getClass());
ClassCache.put(session.getClass());
ClassCache.put(application.getClass());
ClassCache.put(pageContext.getClass());
ClassCache.put(config.getClass());
%>
