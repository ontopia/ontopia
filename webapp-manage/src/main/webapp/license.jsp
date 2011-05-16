<%@ page import="java.io.PrintWriter" %>
<html>
<head>
<title>License Information</title>
</head>
<body>
<center>
<table width="70%" cellpadding="16" border="0">
<tr><td>
<h1>License Information</h1>

<pre>
<%
  PrintWriter pw = new PrintWriter(out);
  boolean displayKey = false;
  //LicenseInfo.checkLicenseKey(pw, displayKey); 
  pw.flush();
%>
</pre>
Go to the <a href="/">Welcome page</a>.<br/>

</td>
<td valign="top">
<a href="/"><img src="images/ontopia-logo.gif" alt="Ontopia" border="0"/></a>
</td></tr>
</table>
</center>
</body>
</html>
