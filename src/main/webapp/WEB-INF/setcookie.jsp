<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" session="false"
    import="javax.servlet.http.Cookie, com.ibm.jp.util.Html" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Set Cookie Tool</title>
<style type="text/css">
body { font-family: sans-serif; margin: 20px; }
h1 { border-bottom: 2px solid #336699; padding-bottom: 4px; }
h2 { border-bottom: 1px solid #cccccc; padding-bottom: 2px; margin-top: 24px; }
table.form td { padding: 4px 8px; vertical-align: middle; }
table.form td.label { font-weight: bold; white-space: nowrap; }
table.cookies { border-collapse: collapse; width: 100%; margin-top: 8px; }
table.cookies th { background: #336699; color: #ffffff; padding: 4px 8px; text-align: left; }
table.cookies td { border: 1px solid #cccccc; padding: 4px 8px; word-break: break-all; }
table.cookies tr:nth-child(even) td { background: #f5f5f5; }
.msg-ok { background: #dff0d8; border: 1px solid #3c763d; color: #3c763d; padding: 6px 12px; margin-bottom: 12px; }
.no-cookie { color: #888888; font-style: italic; }
input[type=text], input[type=number] { width: 220px; }
</style>
</head>
<body>
<h1>Set Cookie Tool</h1>

<%-- メッセージ表示 --%>
<%
	String msg = (String) request.getAttribute("message");
	if (msg != null) {
%>
<p class="msg-ok"><%=Html.escapeChar(msg)%></p>
<%
	}
%>

<%-- 現在のCookie一覧 --%>
<h2>現在のCookie一覧</h2>
<%
	Cookie[] cookies = request.getCookies();
	if (cookies == null || cookies.length == 0) {
%>
<p class="no-cookie">Cookieはセットされていません。</p>
<%
	} else {
%>
<table class="cookies">
	<thead>
		<tr>
			<th>Name</th>
			<th>Value</th>
			<th>Version</th>
			<th>Path</th>
			<th>Domain</th>
			<th>Max-Age</th>
			<th>Secure</th>
			<th>HttpOnly</th>
			<th>操作</th>
		</tr>
	</thead>
	<tbody>
<%
		for (Cookie ck : cookies) {
%>
		<tr>
			<td><%=Html.escapeChar(ck.getName())%></td>
			<td><%=Html.escapeChar(ck.getValue())%></td>
			<td><%=ck.getVersion()%></td>
			<td><%=ck.getPath() != null ? Html.escapeChar(ck.getPath()) : ""%></td>
			<td><%=ck.getDomain() != null ? Html.escapeChar(ck.getDomain()) : ""%></td>
			<td><%=ck.getMaxAge() == -1 ? "(セッション)" : String.valueOf(ck.getMaxAge())%></td>
			<td><%=ck.getSecure() ? "Yes" : "No"%></td>
			<td><%=ck.isHttpOnly() ? "Yes" : "No"%></td>
			<td>
				<form method="post" action="setCookie" style="margin:0">
					<input type="hidden" name="action" value="delete">
					<input type="hidden" name="deleteName" value="<%=Html.escapeChar(ck.getName())%>">
					<input type="submit" value="削除">
				</form>
			</td>
		</tr>
<%
		}
%>
	</tbody>
</table>
<%
	}
%>

<%-- Cookie追加フォーム --%>
<h2>Cookie を追加する</h2>
<form action="setCookie" method="post">
	<input type="hidden" name="action" value="set">
	<table class="form">
		<tbody>
			<tr>
				<td class="label">Cookie Name <span style="color:red">*</span></td>
				<td><input name="key" type="text" value="<%=Html.escapeChar(request.getParameter("key") != null ? request.getParameter("key") : "")%>"></td>
			</tr>
			<tr>
				<td class="label">Value</td>
				<td><input name="value" type="text" value="<%=Html.escapeChar(request.getParameter("value") != null ? request.getParameter("value") : "")%>"></td>
			</tr>
			<tr>
				<td class="label">Version</td>
				<td>
					<input name="version" type="radio" value="0"<%="1".equals(request.getParameter("version")) ? "" : " checked=\"checked\""%>>Version 0 (Netscape)&nbsp;&nbsp;
					<input name="version" type="radio" value="1"<%="1".equals(request.getParameter("version")) ? " checked=\"checked\"" : ""%>>Version 1 (RFC 2109)
				</td>
			</tr>
			<tr>
				<td class="label">Max-Age (秒)</td>
				<td><input name="maxAge" type="number" value="<%=Html.escapeChar(request.getParameter("maxAge") != null ? request.getParameter("maxAge") : "")%>" placeholder="省略時はセッションCookie"></td>
			</tr>
			<tr>
				<td class="label">Path</td>
				<td><input name="path" type="text" value="<%=Html.escapeChar(request.getParameter("path") != null ? request.getParameter("path") : "/")%>"></td>
			</tr>
			<tr>
				<td class="label">Domain</td>
				<td><input name="domain" type="text" value="<%=Html.escapeChar(request.getParameter("domain") != null ? request.getParameter("domain") : "")%>" placeholder="省略可"></td>
			</tr>
			<tr>
				<td class="label">Secure</td>
				<td><input name="secure" type="checkbox" value="on"<%="on".equals(request.getParameter("secure")) ? " checked=\"checked\"" : ""%>></td>
			</tr>
			<tr>
				<td class="label">HttpOnly</td>
				<td><input name="httpOnly" type="checkbox" value="on"<%="on".equals(request.getParameter("httpOnly")) ? " checked=\"checked\"" : ""%>></td>
			</tr>
		</tbody>
	</table>
	<p><input type="submit" value="Cookie をセット"></p>
</form>

</body>
</html>
