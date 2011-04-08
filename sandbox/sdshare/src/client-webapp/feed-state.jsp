<%@ page 
  language="java" 
  contentType="text/html; charset=utf-8"
  import="java.util.List,
          java.util.Collection,
          java.net.InetAddress,
          net.ontopia.infoset.core.LocatorIF,
          net.ontopia.topicmaps.utils.sdshare.*,
          net.ontopia.topicmaps.utils.sdshare.client.*,
          net.ontopia.topicmaps.entry.TopicMapReferenceIF,
	  net.ontopia.topicmaps.core.*"
%><%

  String tmid = request.getParameter("topicmap");
  TopicMapTracker tracker = TrackerManager.getTracker(tmid);
  if (tracker == null) {
    // means either there's no such TM, or we are not supposed to produce
    // a feed for it.
    response.setStatus(404);
    response.setHeader("Content-type", "text/plain");
    out.write("No such topic map: '" + tmid + "'.");
    return;
  }  
%>
<h1>State for <%= tmid %></h1>

<table>
<%
  for (ChangedTopic topic : tracker.getChangeFeed()) {
%>
  <tr><td><%= topic.getObjectId() %>
      <td><%= topic.getTimestamp() %>
<%
  }
%>
</table>