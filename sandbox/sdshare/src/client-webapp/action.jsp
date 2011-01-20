<%@ page 
  language="java" 
  contentType="text/html; charset=utf-8"
  import="net.ontopia.topicmaps.utils.sdshare.client.*"
%><%
  ClientManager manager = (ClientManager) getServletContext().getAttribute("client-manager");
  if (manager == null) {
    ClientConfig cconfig = ClientConfig.readConfig();
    manager = new ClientManager(cconfig);
    getServletContext().setAttribute("client-manager", manager);
  }

  if (request.getParameter("start") != null) {
    manager.startThread();
  } else if (request.getParameter("snapshots") != null) {
    manager.loadSnapshots();
  } else if (request.getParameter("stop") != null) {
    manager.stopThread();
  } else if (request.getParameter("sync") != null) {
    manager.sync();
  }

  int no = Integer.parseInt(request.getParameter("number"));
  for (int ix = 0; ix < no; ix++) {
    if (request.getParameter("clear" + ix) == null)
      continue;

    String key = request.getParameter("id" + ix);
    SyncSource source = manager.getSource(key);
    source.clearError();
  }

  response.sendRedirect("/sdshare-client/");
%>