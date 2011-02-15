<%@ page 
  language="java" 
  contentType="text/html; charset=utf-8"
  import="java.text.SimpleDateFormat,
          net.ontopia.topicmaps.utils.sdshare.client.*,
          net.ontopia.topicmaps.nav2.core.*,
          net.ontopia.topicmaps.nav2.utils.*,
	  net.ontopia.topicmaps.entry.*"
%><%@ taglib prefix="c"        uri="http://java.sun.com/jsp/jstl/core" %><%!

  private static SimpleDateFormat format = 
    new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");

  private static String format(long time) {
    if (time == 0)
      return "&nbsp;";
    else
      return format.format(time);
  }

%><%
  ClientManager manager = (ClientManager) getServletContext().getAttribute("client-manager");

  if (manager == null) {
    ClientConfig cconfig = ClientConfig.readConfig();
    manager = new ClientManager(cconfig);
    getServletContext().setAttribute("client-manager", manager);
  }
%>

<style>th { text-align: left; }
td, th { padding-right: 6pt }</style>
<title>Ontopia SDshare client</title>

<h1>SDshare client</h1>

<form action="action.jsp" method="post">
<p><b>State:</b> 
<%= manager.getStatus() %>
<% if (manager.isStopped()) { 
     if (manager.getConfig().getStartButton()) { %>
       <input type=submit name=start value="Start">
     <% } %>
    <input type=submit name=sync value="Sync">
<% } else if (manager.isRunning()) { %>
<input type=submit name=stop value="Stop">
<% } %>
</p>

<p><input type=submit name=snapshots value="Download snapshots"></p>

<p>Endpoints to synchronize into:</p>

<%
  ClientConfig cconfig = manager.getConfig();
  int ix = 0;
  for (SyncEndpoint endpoint : cconfig.getEndpoints()) { %>
    <h2><%= endpoint.getHandle() %></h2>

    <table>
    <tr><th>Source <th>Last change <th>Last sync

    <% for (SyncSource ss : endpoint.getSources()) { %>
      <tr><td><%= ss.getHandle() %>
          <td><%= format(ss.getLastChange()) %>
          <td><%= format(ss.getLastCheck()) %>

      <%
        if (ss.isBlockedByError()) {
      %>
        <tr><td colspan=3><span style="color: red"><b><%= ss.getError() %></b></span> <br>
        <input type=submit name=clear<%= ix %> value="Clear">
        <input type=hidden name=id<%= ix++ %> 
          value="<%= endpoint.getHandle() %> <%= ss.getHandle() %>">
     <% } %>
   <% } %>
   </table>
  <% } %>

<input type=hidden name=number value="<%= ix %>">
</form>
