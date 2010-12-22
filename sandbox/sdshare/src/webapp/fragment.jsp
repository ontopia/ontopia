<%@ page 
  language="java" 
  import="java.util.Collections,
          net.ontopia.infoset.core.LocatorIF,
          net.ontopia.topicmaps.utils.sdshare.*,
          net.ontopia.topicmaps.entry.TopicMapReferenceIF,
	  net.ontopia.topicmaps.core.*,
	  net.ontopia.topicmaps.xml.*"
%><%

  String tmid = request.getParameter("topicmap");
  String objid = request.getParameter("topic");
  String synid = request.getParameter("syntax");

  TopicMapTracker tracker = StartUpServlet.topicmaps.get(tmid);
  TopicMapReferenceIF ref = tracker.getReference();
  TopicMapStoreIF store = ref.createStore(true);
  TopicMapIF tm = store.getTopicMap();
  SyntaxIF syntax = StartUpServlet.getSyntax(synid);

  response.setHeader("Content-type", syntax.getMIMEType() + "; charset=utf-8");

  TopicMapFragmentWriterIF writer = 
    syntax.getFragmentWriter(response.getOutputStream(), "utf-8");
  writer.startTopicMap();

  TopicIF topic = (TopicIF) tm.getObjectById(objid);
  if (topic != null)
    writer.exportTopics(Collections.singleton(topic).iterator());

  writer.endTopicMap();
%>