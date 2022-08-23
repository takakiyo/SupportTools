<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<%@ page 
language="java"
contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8"
%>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="GENERATOR" content="IBM WebSphere Studio">
<meta http-equiv="Content-Style-Type" content="text/css">
<title>Send Error Test</title>
</head>
<body>
<h1>Send Error Test</h1>
<form action="sendErrorTest" method="GET">
<table border="1">
	<tbody>
		<tr>
			<td>Status Code</td>
			<td><input type="text" name="status.code" size="20" value="404"></td>
		</tr>
		<tr>
			<td>Message</td>
			<td><input type="text" name="message" size="20"></td>
		</tr>
	</tbody>
</table><input type="submit" name="execute" value="Do Test"></form>
</body>
</html>
