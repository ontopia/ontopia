<%@ page 
  language="java" 
  contentType="application/atom+xml; charset=utf-8"
  import="java.util.Collection,
          net.ontopia.infoset.core.LocatorIF,
          net.ontopia.topicmaps.utils.sdshare.*,
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

  TopicMapReferenceIF ref = tracker.getReference();
  TopicMapStoreIF store = ref.createStore(true);
  LocatorIF base = store.getBaseAddress();
  String prefix;
  if (base != null)
    prefix = base.getExternalForm();
  else
    prefix = StartUpServlet.getTopicMapURL(tmid);

  AtomWriter atom = new AtomWriter(out);
  atom.startFeed("Snapshots feed for " + ref.getTitle(),
                 System.currentTimeMillis(),
                 prefix + "/snapshots");
  atom.addServerPrefix(prefix);

  atom.startEntry("Snapshot of " + ref.getTitle(),
                  prefix + "/snapshot/" + tracker.getLastChanged(),
                  System.currentTimeMillis());
  atom.addLink("snapshot.jsp?topicmap=" + tmid,
               "application/x-tm+xml; version=1.0",
               "alternate");
  atom.endEntry();

  atom.endFeed();

%>
