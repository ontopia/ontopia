<%@ page import="java.util.Calendar,java.text.SimpleDateFormat,net.ontopia.Ontopia" %>
<html>
<head>
<meta http-equiv="Refresh" content="0;URL=models/index.jsp" />
<title>Omnigator start up</title>
<style>
<!--
body {
  font-family: verdana, geneva, sans-serif;
}
-->
</style>
</head>
<body>
<center>
<table width="70%" cellpadding="16" border="0">
<tr><td>
<h1>Welcome to the Omnigator</h1>
<h2>Ontopia's Omnigator is starting up. Please wait a few moments.</h2>

<p>
  The version you are using was built on: <%= Ontopia.getBuild() %>.
  A current version can be downloaded from
  <a href="http://www.ontopia.net/download/freedownload.html">here</a>.
</p>
</td>
<td valign="top">
<a href="models/index.jsp"><img src="images/ontopia-logo.gif" alt="Ontopia" border="0"/></a>
</td></tr>
</table>
</center>
</body>
</html>
