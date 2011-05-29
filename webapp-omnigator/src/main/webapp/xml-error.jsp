<%@ page language="java"
         import="java.io.StringWriter, java.io.PrintWriter,
                 java.util.Properties, org.xml.sax.SAXParseException,
                 net.ontopia.utils.FileUtils" %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/template'  prefix='template'  %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/framework' prefix='framework' %>
<framework:response/>

<%-- Omnigator: XML error handling page  --%>
  
<%
SAXParseException cause = (SAXParseException) request.getAttribute("cause");

%>

<template:insert template='/views/template_no_frames.jsp'>

  <template:put name='title' body='true'>[Omnigator] XML Syntax Error</template:put>
  <template:put name='heading' body='true'>
      <h1 class='boxed'>XML Syntax Error</h1>
  </template:put>
  <template:put name='toplinks' body='true'>
      <a href="/omnigator/models/index.jsp">Back to Omnigator's Welcome Page.</a>
  </template:put>
  
  <template:put name='navigation' body='true'>
    <p>Your topic map has not loaded because there is an XML syntax
    error in it. If the problem is a well-formedness error the only
    thing you can do is to correct the file and reload this page. If
    the problem is a validity error you can either correct the file
    and reload, or you can go into <tt>tm-sources.xml</tt> and turn
    off validation there (this requires a reload of the topic map
    registry on the <a href="/manage/manage.jsp">Manage
    page</a> or a server restart).</p>
  </template:put>

  <template:put name='content' body='true'>

    <!--h3 class="boxed"><font color="red"><b><%= cause.getMessage() %></b></font></h3-->

    <table>
    <tr><th valign=top align=left>Cause:  <td><%= cause.getMessage() %>
    <tr><th valign=top align=left>File:  <td><%= cause.getSystemId() %>
    <tr><th valign=top align=left>Line:  <td><%= cause.getLineNumber() %>
    <tr><th valign=top align=left>Column:  <td><%= cause.getColumnNumber() %>
    </table>

    <pre>
    <% // FileUtils.getLineNumber(cause.getSystemId(), cause.getLineNumber()); %>
    </pre>

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
