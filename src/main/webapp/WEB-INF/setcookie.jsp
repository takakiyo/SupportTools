<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" session="false" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Set Cookie Tools</title>
</head>
<body>
	<h1>Set Cookie Tool</h1>
	<form action="setCookie" method="post">
		<table>
			<tbody>
				<tr>
					<td>Cookie Name</td>
					<td><input name="key" type="text" length="16"></td>
				</tr>
				<tr>
					<td>Value</td>
					<td><input name="value" type="text" length="16"></td>
				</tr>
				<tr>
					<td>Version</td>
					<td><input name="version" type="radio" value="0" checked="checked">Version 0
						<input name="version" type="radio" value="1">Version 1</td>
				</tr>
			</tbody>
		</table>
		<input type="submit" value="Submit" />
	</form>

</body>
</html>