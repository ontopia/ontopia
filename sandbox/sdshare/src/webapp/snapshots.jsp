<%@ page 
  language="java" 
  contentType="text/plain; charset=utf-8"
  import="java.util.Collection,
          net.ontopia.infoset.core.LocatorIF,
          net.ontopia.topicmaps.utils.sdshare.*,
          net.ontopia.topicmaps.entry.TopicMapReferenceIF,
	  net.ontopia.topicmaps.core.*"
%><%

  String tmid = request.getParameter("topicmap");
  String prefix = StartUpServlet.getEndpointURL() + tmid;

  TopicMapTracker tracker = StartUpServlet.topicmaps.get(tmid);
  TopicMapReferenceIF ref = tracker.getReference();
  AtomWriter atom = new AtomWriter(out);
  atom.startFeed("Snapshots feed for " + ref.getTitle(),
                 tracker.getLastChanged(),
                 prefix + "/snapshots");

  atom.startEntry("Snapshot of " + ref.getTitle(),
                  prefix + "/snapshot/" + tracker.getLastChanged(),
                  tracker.getLastChanged());
  atom.addLink("snapshot.jsp?topicmap=" + tmid,
               "application/x-tm+xml; version=1.0",
               "alternate");
  atom.endEntry();

  atom.endFeed();

%>
