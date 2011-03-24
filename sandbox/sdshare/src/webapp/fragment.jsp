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
  if (objid == null) {
    out.write("'topic' parameter must be specified!");
    response.setStatus(400);
    return;
  }
  String synid = request.getParameter("syntax");
  if (synid == null) {
    out.write("'syntax' parameter must be specified!");
    response.setStatus(400);
    return;
  }

  TopicMapTracker tracker = TrackerManager.getTracker(tmid);
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