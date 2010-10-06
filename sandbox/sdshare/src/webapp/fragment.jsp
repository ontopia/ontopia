<%@ page 
  language="java" 
  contentType="text/plain; charset=utf-8"
  import="java.util.Collections,
          net.ontopia.infoset.core.LocatorIF,
          net.ontopia.topicmaps.utils.sdshare.*,
          net.ontopia.topicmaps.entry.TopicMapReferenceIF,
	  net.ontopia.topicmaps.core.*,
	  net.ontopia.topicmaps.xml.*,
          net.ontopia.xml.PrettyPrinter"
%><%

  String tmid = request.getParameter("topicmap");
  String objid = request.getParameter("topic");

  TopicMapTracker tracker = StartUpServlet.topicmaps.get(tmid);
  TopicMapReferenceIF ref = tracker.getReference();
  TopicMapStoreIF store = ref.createStore(true);
  TopicMapIF tm = store.getTopicMap();  

  XTMFragmentExporter writer = new XTMFragmentExporter();
  PrettyPrinter pp = new PrettyPrinter(out, null);
  writer.startTopicMap(pp);

  TopicIF topic = (TopicIF) tm.getObjectById(objid);
  if (topic != null)
    writer.exportTopics(Collections.singleton(topic).iterator(), pp);

  writer.endTopicMap(pp);
%>