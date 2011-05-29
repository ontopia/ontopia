<%@ page language="java" isErrorPage="true"
         import="java.io.StringWriter, java.io.PrintWriter,
            java.util.Properties, net.ontopia.utils.OntopiaRuntimeException,
            org.xml.sax.SAXParseException" %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/logic'     prefix='logic'  %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/template'  prefix='template'  %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/framework' prefix='framework' %>
<framework:response/>

<%-- Omnigator: General Error handling page  --%>

<logic:context tmparam="tm">
<template:insert template='/views/template_no_frames.jsp'>

<%
  String summary = request.getParameter("summary");
  String errormsg = request.getParameter("errormsg");
%>

  <template:put name='title' body='true'>[Omnigator] Servlet Error</template:put>
  <template:put name='heading' body='true'>
      <h1 class='boxed'>Omnigator: Error</h1>
  </template:put>
  <template:put name='plugins' body='true'>
    <framework:pluginList separator=" | " group="topicmap"/>
  </template:put>
  
  <template:put name='navigation' body='true'>
   <center>
     <table border="0" with="100%"><tr><td>
        <img src='/omnigator/images/baustelle.gif' width="80" height="90" />
      </td></tr></table>
    </center>
  </template:put>
  <template:put name='content' body='true'>
      
    <h3 class="boxed"><font color="white"><strong><%= summary %></strong></font></h3>

    <p><%= errormsg %>

    <p>Please return to the <a href="/omnigator/index.jsp">homepage</a>.</p>
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
</logic:context>