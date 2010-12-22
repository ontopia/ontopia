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

  TopicMapTracker tracker = StartUpServlet.topicmaps.get(tmid);
  if (tracker == null) {
    // means either there's no such TM, or we are not supposed to produce
    // a feed for it.
    response.setStatus(404);
    response.setHeader("Content-type", "text/plain");
    out.write("No such topic map: '" + tmid + "'.");
    return;
  }

  TopicMapReferenceIF ref = tracker.getReference();
  TopicMapStoreIF store = ref.createStore(true);
  TopicMapIF tm = store.getTopicMap();  
  String prefix = store.getBaseAddress().getExternalForm();
  SyntaxIF[] syntaxes = StartUpServlet.getSyntaxes();

  AtomWriter atom = new AtomWriter(out);
  atom.startFeed("Fragments feed for " + ref.getTitle(),
                 System.currentTimeMillis(),
                 prefix + "/fragments");

  atom.addServerPrefix(prefix);

  for (ChangedTopic change : tracker.getChangeFeed()) {
    atom.startEntry("Topic with object ID " + change.getObjectId(),
                    prefix + "/" + change.getObjectId() + "/" + change.getTimestamp(),
		    change.getTimestamp());

    for (int ix = 0; ix < syntaxes.length; ix++)
      atom.addLink("fragment.jsp?topicmap=" + tmid + "&topic=" + change.getObjectId() + "&syntax=" + syntaxes[ix].getId(),
                   syntaxes[ix].getMIMEType(),
                   "alternate");

    Collection<LocatorIF> psis;
    if (change.isDeleted()) {
      DeletedTopic delete = (DeletedTopic) change;
      psis = delete.getSubjectIdentifiers();
    } else {
      TopicIF topic = (TopicIF) tm.getObjectById(change.getObjectId());
      psis = topic.getSubjectIdentifiers();
    }

    if (!psis.isEmpty())
      atom.addTopicSI(psis.iterator().next());

    atom.endEntry();
  }

  atom.endFeed();
%>
