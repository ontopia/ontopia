<%@ page 
  language="java" 
  contentType="application/atom+xml; charset=utf-8"
  import="net.ontopia.topicmaps.utils.sdshare.*,
          net.ontopia.topicmaps.entry.TopicMapReferenceIF,
          net.ontopia.topicmaps.entry.TopicMapRepositoryIF,
          net.ontopia.topicmaps.nav2.utils.NavigatorUtils
"
%><%
  TopicMapRepositoryIF repo = NavigatorUtils.getTopicMapRepository(pageContext);
  AtomWriter atom = new AtomWriter(out);
  atom.startFeed(StartUpServlet.getTitle(),
                 System.currentTimeMillis(),
                 StartUpServlet.getEndpointURL());
  atom.addLink(StartUpServlet.getEndpointURL());

  for (String tmid : StartUpServlet.getTopicMapIds()) {
    TopicMapReferenceIF ref = repo.getReferenceByKey(tmid);
    atom.startEntry(ref.getTitle(), 
                    StartUpServlet.getEndpointURL() + ref.getId(),
                    System.currentTimeMillis());
    atom.addLink("collection.jsp?topicmap=" + ref.getId(),
                 "application/atom+xml",
		 "alternate");
    atom.addLink("collection.jsp?topicmap=" + ref.getId(),
                 "application/atom+xml",
                 "http://www.egovpt.org/sdshare/collectionfeed");
    atom.addLink("collection.jsp?topicmap=" + ref.getId());
    atom.endEntry();
  }

  atom.endFeed();
%>
