<%@ page language="java"
         import="java.io.StringWriter, java.io.PrintWriter,
                 java.util.Properties, java.io.FileNotFoundException,
                 net.ontopia.utils.FileUtils" %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/template'  prefix='template'  %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/framework' prefix='framework' %>
<framework:response/>

<%-- Omnigator: File not found error handling page  --%>
  
<%
FileNotFoundException cause = (FileNotFoundException) request.getAttribute("cause");
%>

<template:insert template='/views/template_no_frames.jsp'>

  <template:put name='title' body='true'>[Omnigator] File Not Found Error</template:put>
  <template:put name='heading' body='true'>
      <h1 class='boxed'>File Not Found Error</h1>
  </template:put>
  <template:put name='toplinks' body='true'>
      <a href="/omnigator/models/index.jsp">Back to Omnigator's Welcome Page.</a>
  </template:put>
  
  <template:put name='navigation' body='true'>
   <center>
     <table border="0" with="100%"><tr><td>
        <img src='/omnigator/images/baustelle.gif' width="80" height="90" />
      </td></tr></table>
    </center>
  </template:put>

  <template:put name='content' body='true'>
    <p>File not found:
       <b><%= cause.getMessage() %></b>
    <p>Your topic map has not loaded because it has a reference to a file
    that does not exist. This typically means you have a mergemap pointing
    to a file that doesn't exist, but sometimes it means that you've forgotten
    the '#' character in front of a reference in an XTM file.</p>
    <p>Either way, the thing to do is to fix your file and do a reload.
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
