<%@ page 
  language="java" 
  contentType="text/html; charset=utf-8"
  import="net.ontopia.topicmaps.utils.sdshare.client.*,
          net.ontopia.topicmaps.nav2.core.*,
          net.ontopia.topicmaps.nav2.utils.*,
	  net.ontopia.topicmaps.entry.*"
%><%@ taglib prefix="c"        uri="http://java.sun.com/jsp/jstl/core" %><%
  ClientManager manager = (ClientManager) getServletContext().getAttribute("client-manager");

  if (manager == null) {
    ClientConfig cconfig = ClientConfig.readConfig();
    manager = new ClientManager(cconfig);
    getServletContext().setAttribute("client-manager", manager);
  }
%>

<h1>SDshare client</h1>

<form action="action.jsp" method="post">
<p><b>State:</b> 
<%= manager.getStatus() %>
<% if (manager.isStopped()) { %>
<input type=submit name=start value="Start">
<input type=submit name=sync value="Sync">
<% } else if (manager.isRunning()) { %>
<input type=submit name=stop value="Stop">
<% } %>
</p>

<p><input type=submit name=snapshots value="Download snapshots"></p>

<p>Configured to synchronize into these topic maps:</p>

<%
  ClientConfig cconfig = manager.getConfig();
%>
<ul>
  <% int ix = 0;
     for (SyncEndpoint endpoint : cconfig.getEndpoints()) { %>
    <li><%= endpoint.getHandle() %>
    <ul>
    <% for (SyncSource ss : endpoint.getSources()) { %>
      <li><%= ss.getURL() %>
      <%
        if (ss.isBlockedByError()) {
      %>
        <br><span style="color: red"><b><%= ss.getError() %></b></span> <br>
        <input type=submit name=clear<%= ix %> value="Clear">
        <input type=hidden name=id<%= ix++ %> 
          value="<%= endpoint.getHandle() %> <%= ss.getURL() %>">
      <% } %>
      </li>
    <% } %>
    </ul>
  <% } %>
</ul>

<input type=hidden name=number value="<%= ix %>">
</form>
