<%@ page language="java" isErrorPage="true"
         import="java.io.StringWriter, java.io.PrintWriter,
            java.util.Properties, net.ontopia.utils.OntopiaRuntimeException,
            org.xml.sax.SAXParseException,
            net.ontopia.products.*" %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/template'  prefix='template'  %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/framework' prefix='framework' %>
<framework:response/>

<%-- Omnigator: General Error handling page  --%>
  
<%
Properties sys_props = System.getProperties();

// collect information about the exception
StringWriter sw = new StringWriter();
String exceptionStackTrace = "";
String exceptionMessage = "";

if (exception != null) {
  if (exception instanceof OntopiaRuntimeException) {
    Throwable cause = ((OntopiaRuntimeException) exception).getCause();
    out.println("<h1>" + cause + "</h1>");
    if (cause instanceof SAXParseException) {
      request.setAttribute("cause", cause);
      pageContext.forward("xml-error.jsp");
    } else if (cause instanceof java.io.FileNotFoundException) {
      request.setAttribute("cause", cause);
      pageContext.forward("filenotfound-error.jsp");
    }
  }

  exceptionMessage = exception.getMessage();

  // --- get out real root cause exception
  Throwable e = exception;
  while (e instanceof javax.servlet.ServletException
         && ((javax.servlet.ServletException)e).getRootCause() != null)
    e = ((javax.servlet.ServletException)e).getRootCause();

  if (e instanceof OutOfMemoryError)
    pageContext.forward("out-of-memory.jsp");

  e.printStackTrace(new PrintWriter(sw));
  
  exceptionStackTrace = sw.toString();
}

//GenericNavigator prod = GenericNavigator.getInstance();
%>

<template:insert template='/views/template_no_frames.jsp'>

  <template:put name='title' body='true'>Error page</template:put>
  <template:put name='heading' body='true'>
      <h1 class='boxed'>Error page</h1>
  </template:put>
  <template:put name='toplinks' body='true'>
      <a href="/">Back to the Welcome Page.</a>
  </template:put>
  
  <template:put name='navigation' body='true'>
   <center>
     <table border="0" with="100%"><tr><td>
        <img src='images/baustelle.gif' width="80" height="90" />
      </td></tr></table>
    </center>
  </template:put>
  <template:put name='content' body='true'>
    <p><font color="red"><strong><%= exceptionMessage %></strong></font></p>
    <p>Please return to the <a href="index.jsp">homepage</a> or
       complete the form below if you wish to make a bug report.</p>
    <h3>Error Report Form</h3>
    <p>If you think that this error is a bug,
       please submit this form and the error will be forwarded to Ontopia's Support Team.</p>
    <form method="post" action="http://www.ontopia.net/cgi-bin/formmail.py">
      <input type="hidden" name="formtype" value="Omnigator Bug Report">
      <input type="hidden" name="EMAIL" value="support@ontopia.net">
      <input type="hidden" name="REQUIRED" value="user_email">
      <input type="hidden" name="SPAMFILTER" value="clever">
      <input type="hidden" name="java_version" value="<%= sys_props.getProperty("java.vm.vendor") %>, <%= sys_props.getProperty("java.vm.version") %>">
      <input type="hidden" name="os_version" value="<%= sys_props.getProperty("os.name") %>, <%= sys_props.getProperty("os.version") %> (<%= sys_props.getProperty("os.arch") %>)">
      <input type="hidden" name="oks_version" value="<%= net.ontopia.Ontopia.getInfo() %>">
      <input type="hidden" name="server_name" value="<%= request.getServerName() %>">
      <input type="hidden" name="server_info" value="<%= application.getServerInfo() %>">
      <input type="hidden" name="server_port" value="<%= request.getServerPort() %>">
      <input type="hidden" name="remote_address" value="<%= request.getRemoteAddr() %>">
      <input type="hidden" name="remote_host" value="<%= request.getRemoteHost() %>">
      <input type="hidden" name="stack_trace" value="<%= exceptionStackTrace %>"/ >
      <input type="hidden" name="error_message" value="<%= exceptionMessage %>"/ >
          <table>
            <tr>
              <td>Email:</td>
                    <td><input type="text" name="user_email" size="40" maxlength="255" /></td>
            </tr>
            <tr>
                    <td valign="top">Description:</td>
              <td><textarea name="user_comments" cols="60" rows="5"></textarea></td>
            </tr>
            <tr>
              <td></td>
              <td><input type="submit" value="Submit Report" /></td>
            </tr>
<%-- SPAM DETECTION --%>
<tr id=spam style="display: normal"><td><span title="As in, 'is this comment spam?'">Spam</span><td><input type=checkbox name=clever checked><span class=hint><b>don't</b> check this if you want to be posted</span></td></tr>
<tr id=spam2 style="display: normal"><td><span title="As in, 'please confirm that this is not comment spam'">Not spam</span><td><input type=checkbox name=clever2><span class=hint><b>do</b> check this if you want to be posted</span></td></tr>
<%-- SPAM DETECTION --%>
          </table>
    </form>

<%-- SPAM DETECTION --%>
<script type="text/javascript">
document.forms[0].clever.checked = false;
document.forms[0].clever2.checked = true;
document.getElementById("spam").style.display = "none";
document.getElementById("spam2").style.display = "none";
</script>
<%-- SPAM DETECTION --%>

    <br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br>
    <hr>
    <pre><font size="-5">Stack Trace of root cause is:
<%= exceptionStackTrace %>
    </font></pre>
  </template:put>

  <%-- Constants --%>
  <template:put name='application' content='/fragments/application.jsp'/>
  <template:put name='header-tagline' content='/fragments/tagline-header.jsp'/>
  <template:put name='footer-tagline' content='/fragments/tagline-footer.jsp'/>
          
  <%-- Unused --%>
  <template:put name='intro' body='true'></template:put>
  <template:put name='outro' body='true'></template:put>
  <template:put name='head' body='true'></template:put>
          
</template:insert>
