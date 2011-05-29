<%@ page contentType="text/html; charset=utf-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt"  prefix="fmt" %>

<% String language = request.getParameter("language");
  if (! (language == null || language.equals("") 
      || language.equals("null"))) { %>
  <fmt:setLocale value="<%= language %>"/>
<% } %>
<fmt:setBundle basename="languagepack"/>

<HTML>
<HEAD>
  <title>Accessctl</title>
</HEAD>
<BODY>

<H2><fmt:message key="Login"/></H2>

<% if (request.getParameter("error") != null) { %>
  <p><font color="#FF0000"><fmt:message key="IncorrectLogin"/></font></p>
<% } %>
  

<form method="POST" action='<%= response.encodeURL("j_security_check") %>' >
  <table border="0" cellspacing="5">
    <tr>
      <th align="right"><fmt:message key="User"/>:</th>
      <td align="left"><input type="text" id="name" name="j_username"></td>
    </tr>
    <tr>
      <th align="right"><fmt:message key="Password"/>:</th>
      <td align="left"><input type="password" id="passw" name="j_password"></td>
    </tr>
    <tr>
      <td align="right"><input type="submit" value="<fmt:message key="Login"/>"
           id="submit"></td>
      <td align="left"><input type="reset" value="<fmt:message key="Reset"/>"
          id="cancel"></td>
    </tr>
  </table>
</form>

</BODY>
</HTML>
