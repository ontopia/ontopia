<%@ page 
  language="java" 
  contentType="text/html; charset=utf-8"
  import="net.ontopia.topicmaps.utils.sdshare.*,
          net.ontopia.topicmaps.nav2.core.*,
          net.ontopia.topicmaps.nav2.utils.*,
	  net.ontopia.topicmaps.entry.*"
%><%@ taglib prefix="c"        uri="http://java.sun.com/jsp/jstl/core" %><%
  ClientManager manager = (ClientManager) getServletContext().getAttribute("client-manager");

  if (manager == null) {
    NavigatorApplicationIF navApp = NavigatorUtils.getNavigatorApplication(pageContext);
    TopicMapRepositoryIF repository = navApp.getTopicMapRepository();
    ClientConfig cconfig = new ClientConfig(repository);
    manager = new ClientManager(cconfig, repository);
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
</form>

<p>Configured to synchronize into these topic maps every
<%
  ClientConfig cconfig = manager.getConfig();
%>
<%= cconfig.getCheckInterval() %> seconds:</p>

<ul>
  <% for (ClientConfig.TopicMap tm : cconfig.getTopicMaps()) { %>
    <li><%= tm.getId() %>
    <ul>
    <% for (ClientConfig.SyncSource ss : tm.getSources()) { %>
      <li><%= ss.getURL() %>
    <% } %>
    </ul>
  <% } %>
</ul>