<%@ page 
  language="java" 
  contentType="text/html; charset=utf-8"
  import="net.ontopia.topicmaps.utils.sdshare.*,
          net.ontopia.topicmaps.nav2.core.*,
          net.ontopia.topicmaps.nav2.utils.*,
	  net.ontopia.topicmaps.entry.*"
%><%
  ClientManager manager = (ClientManager) getServletContext().getAttribute("client-manager");

  if (request.getParameter("start") != null) {
    manager.startThread();
  } else if (request.getParameter("snapshots") != null) {
    manager.loadSnapshots();
  } else if (request.getParameter("stop") != null) {
    manager.stopThread();
  } else if (request.getParameter("sync") != null) {
    manager.sync();
  }

  response.sendRedirect("/sdshare-client/");
%>