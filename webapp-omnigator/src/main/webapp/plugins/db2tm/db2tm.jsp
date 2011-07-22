<%@ page language="java" 
  import="java.io.File,
          java.util.*"
%><%@ taglib uri='http://psi.ontopia.net/jsp/taglib/template' prefix='template'%>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/framework' prefix='framework' %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/logic'     prefix='logic' %>

<logic:context tmparam="tm">
<template:insert template='/views/template_%view%.jsp'>
  <template:put name='title'>
    [Omnigator] DB2TM
  </template:put>

  <template:put name='plugins' body='true'>
    <framework:pluginList separator=" | " group="topicmap"/>
  </template:put>

  <template:put name="navigation" body="true">

    <h2>DB2TM</h2>

<%  
  String cfgdir = getServletContext().getRealPath("/plugins/db2tm/");
  List<String> files = new ArrayList();
  for (File candidate : new File(cfgdir).listFiles()) {
    if (candidate.getName().endsWith(".xml") &&
        !candidate.getName().equals("plugin.xml"))
      files.add(candidate.getName());
  }

  if (!files.isEmpty()) {
%>
      <form method="POST" action="run.jsp">

      <p>Here you can run the DB2TM synchronization process. Doing this
      will update the topic map
      <b><%= request.getParameter("tm") %></b>
      with all changes made in the database, as configured in

<% if (files.size() == 1) { %>
      <b><%= files.get(0) %></b>.</p>
      <input type=hidden name=cfgfile value="<%= files.get(0) %>">
<% } else { %>

      <p><select name="cfgfile">
<%   for (String cfgfile : files) { %>
       <option><%= cfgfile %></option>
<%   } %>
      </select></p>

<% } %>
        <p>Force rescan <input type=checkbox name=force_rescan></p>

        <input type=hidden name=tm value="<%= request.getParameter("tm") %>">
        <input type=submit name="add"    value="Add">
        <input type=submit name="sync"   value="Sync">
        <input type=submit name="cancel" value="Cancel">
      </form>
<%
  } else {
%>

  <p>This plug-in lets you synchronize your topic map against a
  relational database with DB2TM. To use it, you must make a configuration
  file and put it in
  <b><%= cfgdir %></b>.

<%
  }
%>

  </template:put>


  <template:put name='outro' body='true'></template:put>

  <%-- ============== Outsourced application wide standards ============== --%>
  <template:put name='application' content='/fragments/application.jsp'/>
  <template:put name='header-tagline' content='/fragments/tagline-header.jsp'/>
  <template:put name='footer-tagline' content='/fragments/tagline-footer.jsp'/>
</template:insert>
</logic:context>