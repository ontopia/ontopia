<%@ page 
  language="java" 
  contentType="text/plain; charset=utf-8"
  import="net.ontopia.topicmaps.utils.sdshare.*,
          net.ontopia.topicmaps.entry.TopicMapReferenceIF"
%><%
  AtomWriter atom = new AtomWriter(out);
  atom.startFeed(StartUpServlet.getTitle(),
                 System.currentTimeMillis(),
                 StartUpServlet.getEndpointURL());
  atom.addLink(StartUpServlet.getEndpointURL());

  for (TopicMapTracker tracker : StartUpServlet.topicmaps.values()) {
    TopicMapReferenceIF ref = tracker.getReference();
    atom.startEntry(ref.getTitle(), 
                    StartUpServlet.getEndpointURL() + ref.getId(),
                    System.currentTimeMillis());
    atom.addLink("collection.jsp?topicmap=" + ref.getId(),
                 "application/atom+xml",
                 "http://www.egovpt.org/sdshare/collectionfeed");
    atom.endEntry();
  }

  atom.endFeed();
%>
