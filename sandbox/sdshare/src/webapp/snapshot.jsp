<%@ page 
  language="java" 
  contentType="text/plain; charset=utf-8"
  import="net.ontopia.topicmaps.utils.sdshare.*,
          net.ontopia.topicmaps.entry.*,
	  net.ontopia.topicmaps.core.*,
          net.ontopia.topicmaps.xml.XTMTopicMapWriter"
%><%
 
  String tmid = request.getParameter("topicmap");
  TopicMapRepositoryIF rep = TopicMaps.getRepository();
  TopicMapReferenceIF ref = rep.getReferenceByKey(tmid);
  TopicMapIF tm = ref.createStore(true).getTopicMap();

  XTMTopicMapWriter writer = new XTMTopicMapWriter(out, null);
  writer.write(tm);

%>