<%@ page 
  language="java" 
  contentType="application/atom+xml; charset=utf-8"
  import="java.util.Collection,
          net.ontopia.infoset.core.LocatorIF,
          net.ontopia.topicmaps.utils.sdshare.*,
          net.ontopia.topicmaps.entry.TopicMapReferenceIF,
	  net.ontopia.topicmaps.core.*"
%><%

  // TODO: should support if-modified-since

  String tmid = request.getParameter("topicmap");
  String prefix = StartUpServlet.getEndpointURL() + tmid;

  TopicMapTracker tracker = StartUpServlet.topicmaps.get(tmid);
  TopicMapReferenceIF ref = tracker.getReference();
  AtomWriter atom = new AtomWriter(out);
  atom.startFeed("Fragments feed for " + ref.getTitle(),
                 System.currentTimeMillis(),
                 prefix + "/fragments");

  TopicMapStoreIF store = ref.createStore(true);
  TopicMapIF tm = store.getTopicMap();  

  atom.addServerPrefix(prefix);

  for (ChangedTopic change : tracker.getChangeFeed()) {
    atom.startEntry("Topic with object ID " + change.getObjectId(),
                    prefix + "/" + change.getObjectId() + "/" + change.getTimestamp(),
		    change.getTimestamp());
    atom.addLink("fragment.jsp?topicmap=" + tmid + "&topic=" + change.getObjectId(),
                 "application/x-tm+xml; version=1.0",
                 "alternate");

    // FIXME: if the topic has been deleted we can't look it up, and need
    // identifiers to be stored in ChangedTopic instead, so that we can
    // output TopicSI anyway.

    TopicIF topic = (TopicIF) tm.getObjectById(change.getObjectId());
    Collection<LocatorIF> psis = topic.getSubjectIdentifiers();
    for (LocatorIF psi : psis)
      atom.addTopicSI(psi);

    atom.endEntry();
  }

  atom.endFeed();
%>
