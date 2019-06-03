<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<jsp:useBean id="result"
	class="com.ibm.jp.support.jndi.LookupTestResultBean" scope="request"></jsp:useBean>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" session="false"%>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta name="GENERATOR" content="IBM WebSphere Studio" />
<meta http-equiv="Content-Style-Type" content="text/css" />
<title>JNDI Lookup Test</title>
</head>
<body>
<h1>JNDI Lookup Test</h1>
<FORM action="jndiLookup" method="post">
<h2>InitialContext</h2>
<TABLE border="0">
	<TBODY>
		<TR>
			<td>javax.naming.Context.PROVIDER_URL</td>
			<TD><INPUT type="text" name="provider" size="32"
				value="<%= result.getProviderUrl() %>" /></TD>
		</TR>
		<TR>
			<TD>javax.naming.Context.INITIAL_CONTEXT_FACTORY</TD>
			<TD><INPUT type="text" name="factory" size="32"
				value="<%= result.getInitialContextFactory() %>"></TD>
		</TR>
		<TR>
			<TD><INPUT type="text" name="key1" size="24"
				value="<%= result.getAltKey1() %>"></TD>
			<TD><INPUT type="text" name="value1" size="32"
				value="<%= result.getAltValue1() %>"></TD>
		</TR>
		<TR>
			<TD><INPUT type="text" name="key2" size="24"
				value="<%= result.getAltKey2() %>"></TD>
			<TD><INPUT type="text" name="value2" size="32"
				value="<%= result.getAltValue2() %>"></TD>
		</TR>
	</TBODY>
</TABLE>
<H2>JNDI Name Lookup</H2>
<INPUT type="text" name="name" size="40"
	value="<%= result.getJndiName() %>"> <INPUT type="submit"
	name="lookup" value="Lookup">
<H2>List Names</H2>
<INPUT type="text" name="root" size="40"
	value="<%= result.getRoot() %>"> 
<INPUT type="submit" name="list" value="List Names"></FORM>
<%
if (result.getResult() != null) {
%>
<HR>
<H2>Result</H2>
<pre><%=result.getResult()%></pre>
<%
}
%>
</body>
</html>
